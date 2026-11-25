#!/usr/bin/env python3
#
# This file is part of Burningwave JVM driver.
#
# Author: Roberto Gentili
#
# Hosted at: https://github.com/burningwave/jvm-driver
#
# --
#
# The MIT License (MIT)
#
# Copyright (c) 2026 Roberto Gentili
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
# documentation files (the "Software"), to deal in the Software without restriction, including without
# limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
# conditions:
#
# The above copyright notice and this permission notice shall be included in all copies or substantial
# portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
# LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
# EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
# AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
# OR OTHER DEALINGS IN THE SOFTWARE.
#
"""Turns the results of the JNI engine benchmark into Markdown tables.

Each argument is a directory with the files written by the '[M] benchmark' workflow:

  jmh-result-thrpt.json  the results in throughput mode, written by JMH with '-rf json'
  jmh-result-avgt.json   the results in average time mode (optional)
  benchmark-list.txt     the benchmarks that were run, written by JMH with '-l' (optional): the ones that have no
                         result in a mode are reported as failed
  environment.json       the platform, the runner and the versions of the libraries (optional)

Any other jmh-result*.json file is read as well. Throughput is shown in ops/ms and average time in ns/op, whatever
the unit used by JMH.

With one directory it prints the ratios between Burningwave and Narcissus and the results of every approach; with
more directories it prints the ratios on each platform and, with --details, the results of each platform. The
ratios of the benchmark published in 2022 in toolfactory/narcissus#11 are shown next to the current ones.

The workflow uploads the directory of each platform as an artifact, which GitHub deletes after 90 days together with
the logs and the summaries of the run. To keep the results of a run, download them and write their tables:

  gh run download <run id> --repo burningwave/jvm-driver --pattern "benchmark-results-*" --dir <directory>
  python benchmark/scripts/summarize.py --details --methods --heading-level 2 --title "Results of run <run id>" \\
    --output <directory>/RESULTS.md <directory>/*/

Examples:

  python benchmark/scripts/summarize.py benchmark/target/results/linux-x64
  python benchmark/scripts/summarize.py --details --methods benchmark/results/<run id>/*/
"""

import argparse
import glob
import io
import json
import math
import os
import re
import sys


# The approaches, in the order of the benchmark published in 2022: the benchmarks are named
# <operation><approach>, for example get17_narcissus
APPROACHES = (
    ("02_java_unsafe", "`sun.misc.Unsafe`"),
    ("03_java_reflect_field", "Reflection"),
    ("03_java_reflect_field_typed", "Reflection, typed methods"),
    ("05_java_invoke_varHandle", "`VarHandle`"),
    ("15_jvm_driver_default", "ToolFactory `DefaultDriver`"),
    ("16_jvm_driver_native_burningwave", "Burningwave `NativeDriver`"),
    ("16_jvm_driver_native_narcissus", "ToolFactory `NativeDriver` with Narcissus"),
    ("17_narcissus", "Narcissus"),
    ("17_narcissus_typed", "Narcissus, typed methods"),
    ("20_burningwave_native_executor", "Burningwave `NativeExecutor`"),
    ("20_burningwave_native_executor_typed", "Burningwave `NativeExecutor`, typed methods"),
)

# The methods called by each approach, by operation
METHODS = {
    "02_java_unsafe": (
        "`getObject`, `getInt` with the offset of the field", "`putObject`, `putInt`", "`allocateInstance`", ""),
    "03_java_reflect_field": ("`Field.get`", "`Field.set`", "", "`Class.getDeclaredField(name)`"),
    "03_java_reflect_field_typed": ("`Field.getInt`", "`Field.setInt`", "", ""),
    "05_java_invoke_varHandle": ("`VarHandle.get`", "`VarHandle.set`", "", ""),
    "15_jvm_driver_default": ("`Driver.getFieldValue`", "`Driver.setFieldValue`", "", ""),
    "16_jvm_driver_native_burningwave": ("`Driver.getFieldValue`", "`Driver.setFieldValue`", "", ""),
    "16_jvm_driver_native_narcissus": ("`Driver.getFieldValue`", "`Driver.setFieldValue`", "", ""),
    "17_narcissus": (
        "`getField`, `getStaticField`", "`setField`, `setStaticField`", "`allocateInstance`",
        "`findField(class, name)`"),
    "17_narcissus_typed": (
        "`getObjectField`, `getIntField`, `getStaticObjectField`, `getStaticIntField`",
        "`setObjectField`, `setIntField`, `setStaticObjectField`, `setStaticIntField`", "", ""),
    "20_burningwave_native_executor": (
        "`getFieldValue`", "`setFieldValue`", "`allocateInstance`", "`getDeclaredField(class, name, signature)`"),
    "20_burningwave_native_executor_typed": (
        "`getObjectFieldValue`, `getIntFieldValue`, `getStaticObjectFieldValue`, `getStaticIntFieldValue`",
        "`setObjectFieldValue`, `setIntFieldValue`, `setStaticObjectFieldValue`, `setStaticIntFieldValue`", "", ""),
}

# (benchmark class, description of the field)
FIELDS = (
    ("InstanceStringFieldBenchmark", "private instance `String` field"),
    ("InstanceIntFieldBenchmark", "private instance `int` field"),
    ("StaticStringFieldBenchmark", "private static `String` field"),
    ("StaticIntFieldBenchmark", "private static `int` field"),
)

# (benchmark class, operation, label) of the rows of the tables of the ratios
OPERATIONS = tuple(
    item for benchmark_class, field in FIELDS for item in (
        (benchmark_class, "get", "Read " + field),
        (benchmark_class, "set", "Write " + field),
    )
) + (
    ("AllocateInstanceBenchmark", "allocateInstance", "Allocate an instance without calling the constructor"),
    ("FieldLookupBenchmark", "findField", "Find a field by name"),
)

# (label, approach of Burningwave, approach of Narcissus)
COMPARISONS = (
    ("Engines", "20_burningwave_native_executor", "17_narcissus"),
    ("Engines, typed methods", "20_burningwave_native_executor_typed", "17_narcissus_typed"),
    ("Native drivers", "16_jvm_driver_native_burningwave", "16_jvm_driver_native_narcissus"),
)

# Throughput in ops/ms of get/set20_burningwave_native_executor and get/set17_narcissus in the benchmark published
# in 2022 in https://github.com/toolfactory/narcissus/discussions/11 (JDK 18)
RESULTS_2022 = {
    ("InstanceStringFieldBenchmark", "get"): (17394.136, 2258.050),
    ("InstanceStringFieldBenchmark", "set"): (19833.011, 1580.701),
}
DISCUSSION_2022 = "https://github.com/toolfactory/narcissus/discussions/11"

# The order of the platforms in the tables, the same as in the matrix of the workflow
PLATFORMS = ("Linux x64", "Windows x64", "macOS x64", "Linux aarch64", "macOS aarch64")

MODES = ("thrpt", "avgt")
UNITS = {"thrpt": "ops/ms", "avgt": "ns/op"}
SECONDS = {"ns": 1e-9, "us": 1e-6, "ms": 1e-3, "s": 1.0, "min": 60.0, "hr": 3600.0, "day": 86400.0}

FAILED = "**failed**"
NOT_SIGNIFICANT = "*"


class Metric(object):
    """A score of JMH converted to ops/ms (throughput) or ns/op (average time)."""

    def __init__(self, mode, primary_metric):
        factor = conversion_factor(mode, primary_metric.get("scoreUnit", UNITS.get(mode, "")))
        self.score = primary_metric["score"] * factor
        error = primary_metric.get("scoreError")
        self.error = error * factor if is_number(error) else None
        interval = primary_metric.get("scoreConfidence") or ()
        self.interval = tuple(bound * factor for bound in interval) if len(interval) == 2 and all(
            is_number(bound) for bound in interval
        ) else None


class Platform(object):
    """The results of the benchmark on one platform, read from a directory."""

    def __init__(self, directory):
        self.directory = directory
        self.environment = read_json(os.path.join(directory, "environment.json"), {})
        self.label = self.environment.get("platform") or os.path.basename(os.path.normpath(directory))
        self.entries = []
        self.modes = set()
        for path in sorted(glob.glob(os.path.join(directory, "jmh-result*.json"))):
            match = re.match(r"^jmh-result-(\w+)\.json$", os.path.basename(path))
            if match and match.group(1) in MODES:
                self.modes.add(match.group(1))
            self.entries += read_json(path, [])
        # (class, method, mode) -> Metric
        self.results = {}
        for entry in self.entries:
            name = split_name(entry["benchmark"])
            if name:
                self.modes.add(entry["mode"])
                self.results[name + (entry["mode"],)] = Metric(entry["mode"], entry["primaryMetric"])
        list_file = os.path.join(directory, "benchmark-list.txt")
        names = set()
        if os.path.isfile(list_file):
            with io.open(list_file, encoding="utf-8") as stream:
                names = set(filter(None, (split_name(line.strip()) for line in stream)))
        self.expected = set(name + (mode,) for name in names for mode in self.modes)
        self.failed = sorted(self.expected - set(self.results))

    def metric(self, benchmark_class, method, mode):
        """Returns the Metric of a benchmark, FAILED if it has no result, or None if it was not run."""
        key = (benchmark_class, method, mode)
        if key in self.results:
            return self.results[key]
        if key in self.expected:
            return FAILED
        return None

    def ran(self, benchmark_class, method):
        return any(self.metric(benchmark_class, method, mode) is not None for mode in MODES)


def read_json(path, default):
    if not os.path.isfile(path):
        return default
    with io.open(path, encoding="utf-8") as stream:
        return json.load(stream)


def is_number(value):
    # JMH writes NaN as the string "NaN"
    return isinstance(value, (int, float)) and not math.isnan(value)


def conversion_factor(mode, unit):
    """Returns the factor that converts a score from the given unit to ops/ms (thrpt) or ns/op (avgt)."""
    if mode == "thrpt" and unit.startswith("ops/") and unit[4:] in SECONDS:
        return SECONDS["ms"] / SECONDS[unit[4:]]
    if mode != "thrpt" and unit.endswith("/op") and unit[:-3] in SECONDS:
        return SECONDS[unit[:-3]] / SECONDS["ns"]
    return 1.0


def split_name(name):
    """Splits 'package.Class.method' into (Class, method)."""
    if " " in name or "." not in name:
        return None
    class_name, _, method = name.rpartition(".")
    return (class_name.rpartition(".")[2], method)


def format_number(value, decimals):
    return "{:,.{}f}".format(value, decimals)


def format_metric(metric):
    """Formats score and error with 0, 1 or 2 decimals, depending on the magnitude of the score."""
    if metric is None:
        return ""
    if metric == FAILED:
        return FAILED
    decimals = 0 if metric.score >= 100 else 1 if metric.score >= 10 else 2
    text = format_number(metric.score, decimals)
    if metric.error is not None:
        text += " ± " + format_number(metric.error, decimals)
    return text


def format_ratio(burningwave, narcissus):
    """Formats the ratio between the throughput of Burningwave and the one of Narcissus."""
    if burningwave is None or narcissus is None:
        return ""
    if burningwave == FAILED or narcissus == FAILED:
        return FAILED
    ratio = "{:.2f}".format(burningwave.score / narcissus.score)
    first, second = burningwave.interval, narcissus.interval
    if first and second and first[0] <= second[1] and second[0] <= first[1]:
        ratio += NOT_SIGNIFICANT
    return ratio


def ratio_2022(benchmark_class, operation):
    if (benchmark_class, operation) not in RESULTS_2022:
        return ""
    burningwave, narcissus = RESULTS_2022[(benchmark_class, operation)]
    return "{:.2f}".format(burningwave / narcissus)


def table(header, alignments, rows):
    lines = [
        "| " + " | ".join(header) + " |",
        "|" + "|".join(":--" if alignment == "left" else "--:" for alignment in alignments) + "|",
    ]
    for row in rows:
        lines.append("| " + " | ".join(row) + " |")
    return "\n".join(lines)


def first_entry(platform):
    return platform.entries[0] if platform.entries else {}


def jvm_description(platform):
    entry = first_entry(platform)
    if not entry:
        return "?"
    distribution = platform.environment.get("javaDistribution")
    description = "{} {}".format(entry.get("vmName", "?"), entry.get("vmVersion", "?"))
    return "{} {}".format(distribution[:1].upper() + distribution[1:], description) if distribution else description


def jmh_description(platform):
    entry = first_entry(platform)
    if not entry:
        return "?"
    return "{}, {}, forks: {}, warmup: {} × {}, measurement: {} × {}".format(
        entry.get("jmhVersion", "?"),
        " and ".join(UNITS[mode] for mode in MODES if mode in platform.modes),
        entry.get("forks", "?"),
        entry.get("warmupIterations", "?"),
        entry.get("warmupTime", "?"),
        entry.get("measurementIterations", "?"),
        entry.get("measurementTime", "?"),
    )


def burningwave_description(platform):
    version = platform.environment.get("burningwaveJvmDriverVersion", "?")
    commit = platform.environment.get("commit")
    return "{} (commit `{}`)".format(version, commit[:7]) if commit else version


def environment_table(platforms):
    rows = []
    for platform in platforms:
        runner = platform.environment.get("runner")
        rows.append((
            platform.label,
            "`{}`".format(runner) if runner else "?",
            jvm_description(platform),
            burningwave_description(platform),
            platform.environment.get("narcissusVersion", "?"),
            jmh_description(platform),
        ))
    return table(
        ("Platform", "Runner", "JVM", "Burningwave JVM Driver", "Narcissus", "JMH"),
        ("left",) * 6,
        rows,
    )


def ratios_table(platform):
    """The ratios of one platform: a row for each operation, a column for each comparison."""
    rows = []
    for benchmark_class, operation, label in OPERATIONS:
        cells = [
            format_ratio(
                platform.metric(benchmark_class, operation + burningwave, "thrpt"),
                platform.metric(benchmark_class, operation + narcissus, "thrpt"),
            )
            for _, burningwave, narcissus in COMPARISONS
        ]
        if any(cells):
            rows.append([label] + cells + [ratio_2022(benchmark_class, operation)])
    return table(
        ["Operation"] + [label for label, _, _ in COMPARISONS] + ["Engines in 2022"],
        ["left"] + ["right"] * (len(COMPARISONS) + 1),
        rows,
    )


def platforms_ratios_table(platforms, comparison, with_2022):
    """The ratios of one comparison: a row for each operation, a column for each platform."""
    _, burningwave, narcissus = comparison
    rows = []
    for benchmark_class, operation, label in OPERATIONS:
        cells = [
            format_ratio(
                platform.metric(benchmark_class, operation + burningwave, "thrpt"),
                platform.metric(benchmark_class, operation + narcissus, "thrpt"),
            )
            for platform in platforms
        ]
        if any(cells):
            rows.append([label] + ([ratio_2022(benchmark_class, operation)] if with_2022 else []) + cells)
    return table(
        ["Operation"] + (["2022"] if with_2022 else []) + [platform.label for platform in platforms],
        ["left"] + ["right"] * (len(platforms) + (1 if with_2022 else 0)),
        rows,
    )


def approaches_table(platform, columns):
    """The results of every approach: columns is a list of (header, benchmark class, operation, mode)."""
    rows = []
    for approach, label in APPROACHES:
        cells = [
            format_metric(platform.metric(benchmark_class, operation + approach, mode))
            for _, benchmark_class, operation, mode in columns
        ]
        if any(cells):
            rows.append(["{} (`{}`)".format(label, approach)] + cells)
    if not rows:
        return None
    return table(
        ["Approach"] + [header for header, _, _, _ in columns],
        ["left"] + ["right"] * len(columns),
        rows,
    )


def approaches_sections(platform, heading):
    modes = [mode for mode in MODES if mode in platform.modes]
    sections = []
    for benchmark_class, field in FIELDS:
        columns = [
            ("{} ({})".format(action, UNITS[mode]), benchmark_class, operation, mode)
            for mode in modes for action, operation in (("Read", "get"), ("Write", "set"))
        ]
        content = approaches_table(platform, columns)
        if content:
            sections += ["{} Read and write a {}".format(heading, field), content]
    columns = [
        ("{} ({})".format(action, UNITS[mode]), benchmark_class, operation, mode)
        for mode in modes for action, benchmark_class, operation in (
            ("Allocate", "AllocateInstanceBenchmark", "allocateInstance"),
            ("Find", "FieldLookupBenchmark", "findField"),
        )
    ]
    content = approaches_table(platform, columns)
    if content:
        sections += [
            "{} Allocate an instance without calling the constructor and find a field by name".format(heading), content
        ]
    return sections


def methods_table():
    return table(
        ["Approach", "Read", "Write", "Allocate", "Find"],
        ("left",) * 5,
        [["{} (`{}`)".format(label, approach)] + list(METHODS[approach]) for approach, label in APPROACHES],
    )


RATIOS_NOTE = (
    "Throughput of Burningwave divided by the one of Narcissus: above 1 Burningwave is faster. Engines: "
    "`NativeExecutor` against `Narcissus` with the methods that accept fields of any type, as in the [benchmark "
    "published in 2022](" + DISCUSSION_2022 + "), whose ratios are in the 2022 column; typed methods: the methods "
    "dedicated to the type of the field; native drivers: `Driver.getFieldValue` and `Driver.setFieldValue` of the "
    "native drivers of Burningwave JVM Driver and ToolFactory JVM Driver. `" + NOT_SIGNIFICANT + "` marks the "
    "ratios of two throughputs whose 99.9% confidence intervals overlap, so the difference may not be significant."
)

RESULTS_NOTE = (
    "Throughput in operations per millisecond (higher is better) and average time in nanoseconds per operation "
    "(lower is better), with the 99.9% confidence interval computed by JMH."
)


def failures_section(platforms):
    lines = []
    for platform in platforms:
        if platform.failed:
            lines.append("Benchmarks without results on {} (see the output of JMH): {}".format(
                platform.label, ", ".join("`{}.{}` ({})".format(*key) for key in platform.failed),
            ))
    return "\n\n".join(lines)


def summarize(platforms, details, methods, heading_level, title=None):
    heading = "#" * heading_level
    sections = ["# " + title] if title else []
    if len(platforms) == 1:
        platform = platforms[0]
        sections += ["{} {}".format(heading, platform.label), environment_table(platforms)]
        sections += ["{}# Burningwave / Narcissus".format(heading), ratios_table(platform), RATIOS_NOTE]
        sections += approaches_sections(platform, heading + "#") + [RESULTS_NOTE]
    else:
        sections.append(environment_table(platforms))
        for index, comparison in enumerate(COMPARISONS):
            sections.append("{} Burningwave / Narcissus: {}".format(heading, comparison[0].lower()))
            sections.append(platforms_ratios_table(platforms, comparison, index == 0))
        sections.append(RATIOS_NOTE)
        if details:
            for platform in platforms:
                sections.append("{} {}".format(heading, platform.label))
                sections += approaches_sections(platform, heading + "#")
            sections.append(RESULTS_NOTE)
    failures = failures_section(platforms)
    if failures:
        sections.append(failures)
    if methods:
        sections += ["{} Methods called by each approach".format(heading), methods_table()]
    run_urls = sorted(set(
        platform.environment["runUrl"] for platform in platforms if platform.environment.get("runUrl")
    ))
    if run_urls:
        sections.append("\n".join("Results of the CI run {}".format(url) for url in run_urls))
    return "\n\n".join(sections) + "\n"


def main():
    parser = argparse.ArgumentParser(
        description=__doc__.split("\n\n")[0],
        epilog="\n\n".join(__doc__.split("\n\n")[1:]),
        formatter_class=argparse.RawDescriptionHelpFormatter,
    )
    parser.add_argument("directories", nargs="+", metavar="DIRECTORY", help="a directory with the results")
    parser.add_argument("--output", help="the Markdown file to write (default: the standard output)")
    parser.add_argument("--details", action="store_true", help="with more directories, add the results of each one")
    parser.add_argument("--methods", action="store_true", help="add the methods called by each approach")
    parser.add_argument("--heading-level", type=int, default=3, help="the level of the headings (default: 3)")
    parser.add_argument("--title", help="a title, written as a level 1 heading before everything else")
    parser.add_argument(
        "--check", action="store_true",
        help="exit with status 1 if a benchmark listed in benchmark-list.txt has no result in a mode",
    )
    arguments = parser.parse_args()

    platforms = [Platform(directory) for directory in arguments.directories]
    platforms.sort(key=lambda platform: PLATFORMS.index(platform.label) if platform.label in PLATFORMS else len(PLATFORMS))
    markdown = summarize(platforms, arguments.details, arguments.methods, arguments.heading_level, arguments.title)
    if arguments.output:
        with io.open(arguments.output, "w", encoding="utf-8", newline="\n") as stream:
            stream.write(markdown)
    else:
        stream = io.open(sys.stdout.fileno(), "w", encoding="utf-8", newline="\n", closefd=False)
        stream.write(markdown)
        stream.flush()
    failed = [(platform.label, key) for platform in platforms for key in platform.failed]
    if arguments.check and failed:
        for label, (benchmark_class, method, mode) in failed:
            sys.stderr.write("No result for {}.{} in {} mode on {}\n".format(benchmark_class, method, mode, label))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
