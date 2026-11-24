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

  jmh-result.json      the results, written by JMH with '-rf json'
  benchmark-list.txt   the benchmarks that were run, written by JMH with '-l' (optional): the ones that have no
                       result are reported as failed
  environment.json     the platform, the runner and the versions of the libraries (optional)

With one directory it prints the times of every engine; with more directories it prints a table with the ratio
between Burningwave and Narcissus on each platform and, with --details, the times of each platform.

The workflow uploads the directory of each platform as an artifact, which GitHub deletes after 90 days. To keep the
results that the README refers to, save them in the repository:

  gh run download <run id> --repo burningwave/jvm-driver --pattern "benchmark-results-*" --dir benchmark/results/<run id>
  python benchmark/scripts/summarize.py --details --methods --heading-level 2 \\
    --output benchmark/results/<run id>/RESULTS.md benchmark/results/<run id>/*/

Examples:

  python benchmark/scripts/summarize.py benchmark/target/results/linux-x64
  python benchmark/scripts/summarize.py --details --methods benchmark/results/<run id>/*/
"""

import argparse
import io
import json
import math
import os
import sys


ENGINES = (
    ("burningwave", "Burningwave"),
    ("narcissus", "Narcissus"),
    ("reflection", "Reflection"),
    ("varHandle", "VarHandle"),
)

# The order of the platforms in the tables, the same as in the matrix of the workflow
PLATFORMS = ("Linux x64", "Windows x64", "macOS x64", "Linux aarch64", "macOS aarch64")

# (benchmark class, operation, label, methods called by each engine). The benchmarks are named
# <class>.<operation>_<engine>.
OPERATIONS = (
    ("InstanceFieldBenchmark", "getObject", "Read instance `Object` field", {
        "burningwave": "getFieldValue(target, field)",
        "narcissus": "getField(target, field)",
        "reflection": "field.get(target)",
    }),
    ("InstanceFieldBenchmark", "getObjectTyped", "Read instance `Object` field, typed method", {
        "burningwave": "getObjectFieldValue(target, field)",
        "narcissus": "getObjectField(target, field)",
        "varHandle": "handle.get(target)",
    }),
    ("InstanceFieldBenchmark", "getInt", "Read instance `int` field, boxed", {
        "burningwave": "getFieldValue(target, field)",
        "narcissus": "getField(target, field)",
        "reflection": "field.get(target)",
    }),
    ("InstanceFieldBenchmark", "getIntTyped", "Read instance `int` field, typed method", {
        "burningwave": "getIntFieldValue(target, field)",
        "narcissus": "getIntField(target, field)",
        "reflection": "field.getInt(target)",
        "varHandle": "handle.get(target)",
    }),
    ("InstanceFieldBenchmark", "setObject", "Write instance `Object` field", {
        "burningwave": "setFieldValue(target, field, value)",
        "narcissus": "setField(target, field, value)",
        "reflection": "field.set(target, value)",
    }),
    ("InstanceFieldBenchmark", "setObjectTyped", "Write instance `Object` field, typed method", {
        "burningwave": "setObjectFieldValue(target, field, value)",
        "narcissus": "setObjectField(target, field, value)",
        "varHandle": "handle.set(target, value)",
    }),
    ("InstanceFieldBenchmark", "setInt", "Write instance `int` field, boxed", {
        "burningwave": "setFieldValue(target, field, value)",
        "narcissus": "setField(target, field, value)",
        "reflection": "field.set(target, value)",
    }),
    ("InstanceFieldBenchmark", "setIntTyped", "Write instance `int` field, typed method", {
        "burningwave": "setIntFieldValue(target, field, value)",
        "narcissus": "setIntField(target, field, value)",
        "reflection": "field.setInt(target, value)",
        "varHandle": "handle.set(target, value)",
    }),
    ("StaticFieldBenchmark", "getObject", "Read static `Object` field", {
        "burningwave": "getFieldValue(null, field)",
        "narcissus": "getStaticField(field)",
        "reflection": "field.get(null)",
    }),
    ("StaticFieldBenchmark", "getObjectTyped", "Read static `Object` field, typed method", {
        "burningwave": "getStaticObjectFieldValue(field)",
        "narcissus": "getStaticObjectField(field)",
        "varHandle": "handle.get()",
    }),
    ("StaticFieldBenchmark", "getInt", "Read static `int` field, boxed", {
        "burningwave": "getFieldValue(null, field)",
        "narcissus": "getStaticField(field)",
        "reflection": "field.get(null)",
    }),
    ("StaticFieldBenchmark", "getIntTyped", "Read static `int` field, typed method", {
        "burningwave": "getStaticIntFieldValue(field)",
        "narcissus": "getStaticIntField(field)",
        "reflection": "field.getInt(null)",
        "varHandle": "handle.get()",
    }),
    ("StaticFieldBenchmark", "setObject", "Write static `Object` field", {
        "burningwave": "setFieldValue(null, field, value)",
        "narcissus": "setStaticField(field, value)",
        "reflection": "field.set(null, value)",
    }),
    ("StaticFieldBenchmark", "setObjectTyped", "Write static `Object` field, typed method", {
        "burningwave": "setStaticObjectFieldValue(field, value)",
        "narcissus": "setStaticObjectField(field, value)",
        "varHandle": "handle.set(value)",
    }),
    ("StaticFieldBenchmark", "setInt", "Write static `int` field, boxed", {
        "burningwave": "setFieldValue(null, field, value)",
        "narcissus": "setStaticField(field, value)",
        "reflection": "field.set(null, value)",
    }),
    ("StaticFieldBenchmark", "setIntTyped", "Write static `int` field, typed method", {
        "burningwave": "setStaticIntFieldValue(field, value)",
        "narcissus": "setStaticIntField(field, value)",
        "reflection": "field.setInt(null, value)",
        "varHandle": "handle.set(value)",
    }),
    ("AllocateInstanceBenchmark", "allocateInstance", "Allocate an instance without calling the constructor", {
        "burningwave": "allocateInstance(Target.class)",
        "narcissus": "allocateInstance(Target.class)",
    }),
    ("FieldLookupBenchmark", "findField", "Find a field by name", {
        "burningwave": "getDeclaredField(Target.class, name, signature)",
        "narcissus": "findField(Target.class, name)",
        "reflection": "Target.class.getDeclaredField(name)",
    }),
)

FAILED = "**failed**"
NOT_SIGNIFICANT = "*"


class Platform(object):
    """The results of the benchmark on one platform, read from a directory."""

    def __init__(self, directory):
        self.directory = directory
        self.environment = read_json(os.path.join(directory, "environment.json"), {})
        self.label = self.environment.get("platform") or os.path.basename(os.path.normpath(directory))
        self.entries = read_json(os.path.join(directory, "jmh-result.json"), [])
        # benchmark name -> (class, operation, engine)
        self.results = {}
        for entry in self.entries:
            key = split_name(entry["benchmark"])
            if key:
                self.results[key] = entry["primaryMetric"]
        list_file = os.path.join(directory, "benchmark-list.txt")
        if os.path.isfile(list_file):
            self.expected = set()
            with io.open(list_file, encoding="utf-8") as stream:
                for line in stream:
                    key = split_name(line.strip())
                    if key:
                        self.expected.add(key)
        else:
            self.expected = set(self.results)
        self.failed = sorted(self.expected - set(self.results))

    def cell(self, key):
        """Returns the metric of a benchmark, FAILED if it has no result, or None if it was not run."""
        if key in self.results:
            return self.results[key]
        if key in self.expected:
            return FAILED
        return None

    def operations(self):
        return set((benchmark_class, operation) for benchmark_class, operation, _ in self.expected | set(self.results))


def read_json(path, default):
    if not os.path.isfile(path):
        return default
    with io.open(path, encoding="utf-8") as stream:
        return json.load(stream)


def split_name(name):
    """Splits 'package.Class.operation_engine' into (Class, operation, engine)."""
    if " " in name or "." not in name:
        return None
    class_name, _, method = name.rpartition(".")
    operation, separator, engine = method.rpartition("_")
    if not separator:
        return None
    return (class_name.rpartition(".")[2], operation, engine)


def ordered_operations(platforms):
    """Returns (class, operation, label, methods) for every operation run on at least one platform."""
    run = set()
    for platform in platforms:
        run |= platform.operations()
    known = [operation for operation in OPERATIONS if operation[:2] in run]
    known_keys = set(operation[:2] for operation in OPERATIONS)
    unknown = [
        (benchmark_class, operation, "`{}.{}`".format(benchmark_class, operation), {})
        for benchmark_class, operation in sorted(run - known_keys)
    ]
    return known + unknown


def is_number(value):
    # JMH writes NaN as the string "NaN"
    return isinstance(value, (int, float)) and not math.isnan(value)


def format_time(metric):
    """Formats score and error with 0, 1 or 2 decimals, depending on the magnitude of the score."""
    if metric is None:
        return ""
    if metric == FAILED:
        return FAILED
    score = metric["score"]
    decimals = 0 if score >= 100 else 1 if score >= 10 else 2
    text = "{:,.{}f}".format(score, decimals)
    error = metric.get("scoreError")
    if is_number(error):
        text += " ± {:,.{}f}".format(error, decimals)
    return text


def confidence_interval(metric):
    interval = metric.get("scoreConfidence")
    if not interval or not all(is_number(bound) for bound in interval):
        return None
    return interval


def format_ratio(burningwave, narcissus):
    if burningwave is None or narcissus is None:
        return ""
    if burningwave == FAILED or narcissus == FAILED:
        return FAILED
    ratio = "{:.2f}".format(burningwave["score"] / narcissus["score"])
    first, second = confidence_interval(burningwave), confidence_interval(narcissus)
    if first and second and first[0] <= second[1] and second[0] <= first[1]:
        ratio += NOT_SIGNIFICANT
    return ratio


def table(header, alignments, rows):
    lines = [
        "| " + " | ".join(header) + " |",
        "|" + "|".join(":--" if alignment == "left" else "--:" for alignment in alignments) + "|",
    ]
    for row in rows:
        lines.append("| " + " | ".join(row) + " |")
    return "\n".join(lines)


def jvm_description(platform):
    if not platform.entries:
        return "?"
    entry = platform.entries[0]
    distribution = platform.environment.get("javaDistribution")
    description = "{} {}".format(entry.get("vmName", "?"), entry.get("vmVersion", "?"))
    return "{} {}".format(distribution[:1].upper() + distribution[1:], description) if distribution else description


def jmh_description(platform):
    if not platform.entries:
        return "?"
    entry = platform.entries[0]
    return "{}, forks: {}, warmup: {} × {}, measurement: {} × {}".format(
        entry.get("jmhVersion", "?"),
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


def times_table(platform, operations):
    rows = []
    for benchmark_class, operation, label, _ in operations:
        cells = dict(
            (engine, platform.cell((benchmark_class, operation, engine))) for engine, _ in ENGINES
        )
        if all(cell is None for cell in cells.values()):
            continue
        rows.append([label] + [format_time(cells[engine]) for engine, _ in ENGINES[:2]] + [
            format_ratio(cells["burningwave"], cells["narcissus"])
        ] + [format_time(cells[engine]) for engine, _ in ENGINES[2:]])
    return table(
        ["Operation", "Burningwave", "Narcissus", "Burningwave / Narcissus", "Reflection", "VarHandle"],
        ["left"] + ["right"] * 5,
        rows,
    )


def ratios_table(platforms, operations):
    rows = []
    for benchmark_class, operation, label, _ in operations:
        cells = []
        for platform in platforms:
            cells.append(format_ratio(
                platform.cell((benchmark_class, operation, "burningwave")),
                platform.cell((benchmark_class, operation, "narcissus")),
            ))
        if any(cells):
            rows.append([label] + cells)
    return table(
        ["Operation"] + [platform.label for platform in platforms],
        ["left"] + ["right"] * len(platforms),
        rows,
    )


def methods_table(operations):
    rows = []
    for _, _, label, methods in operations:
        rows.append([label] + [
            "`{}`".format(methods[engine]) if engine in methods else "" for engine, _ in ENGINES
        ])
    return table(
        ["Operation", "Burningwave (`NativeExecutor`)", "Narcissus", "Reflection (`Field`)", "VarHandle"],
        ("left",) * 5,
        rows,
    )


TIMES_NOTE = (
    "Average time in nanoseconds per operation (lower is better), with the 99.9% confidence interval computed by "
    "JMH. Burningwave / Narcissus is the ratio between the two times: below 1 Burningwave is faster. "
    "`" + NOT_SIGNIFICANT + "` marks the ratios of two times whose confidence intervals overlap, so the difference "
    "may not be significant."
)

RATIOS_NOTE = (
    "Ratio between the average time of Burningwave and the one of Narcissus on each platform: below 1 Burningwave "
    "is faster. `" + NOT_SIGNIFICANT + "` marks the ratios of two times whose 99.9% confidence intervals overlap, "
    "so the difference may not be significant."
)


def failures_section(platforms):
    lines = []
    for platform in platforms:
        if platform.failed:
            lines.append("Benchmarks without results on {} (see the output of JMH): {}".format(
                platform.label,
                ", ".join("`{}.{}_{}`".format(*key) for key in platform.failed),
            ))
    return "\n\n".join(lines)


def summarize(platforms, details, methods, heading_level):
    operations = ordered_operations(platforms)
    heading = "#" * heading_level
    sections = []
    if len(platforms) == 1:
        platform = platforms[0]
        sections.append("{} {}".format(heading, platform.label))
        sections.append(environment_table(platforms))
        sections.append(times_table(platform, operations))
        sections.append(TIMES_NOTE)
    else:
        sections.append(environment_table(platforms))
        sections.append(ratios_table(platforms, operations))
        sections.append(RATIOS_NOTE)
        if details:
            for platform in platforms:
                sections.append("{}# {}".format(heading, platform.label))
                sections.append(times_table(platform, operations))
            sections.append(TIMES_NOTE)
    failures = failures_section(platforms)
    if failures:
        sections.append(failures)
    if methods:
        sections.append("{} Methods compared".format(heading))
        sections.append(methods_table(operations))
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
    parser.add_argument("--details", action="store_true", help="with more directories, add the times of each one")
    parser.add_argument("--methods", action="store_true", help="add the methods called by each engine")
    parser.add_argument("--heading-level", type=int, default=3, help="the level of the headings (default: 3)")
    parser.add_argument(
        "--check", action="store_true",
        help="exit with status 1 if a benchmark listed in benchmark-list.txt has no result",
    )
    arguments = parser.parse_args()

    platforms = [Platform(directory) for directory in arguments.directories]
    platforms.sort(key=lambda platform: PLATFORMS.index(platform.label) if platform.label in PLATFORMS else len(PLATFORMS))
    markdown = summarize(platforms, arguments.details, arguments.methods, arguments.heading_level)
    if arguments.output:
        with io.open(arguments.output, "w", encoding="utf-8", newline="\n") as stream:
            stream.write(markdown)
    else:
        stream = io.open(sys.stdout.fileno(), "w", encoding="utf-8", newline="\n", closefd=False)
        stream.write(markdown)
        stream.flush()
    failed = [(platform.label, key) for platform in platforms for key in platform.failed]
    if arguments.check and failed:
        for label, key in failed:
            sys.stderr.write("No result for {}.{}_{} on {}\n".format(key[0], key[1], key[2], label))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
