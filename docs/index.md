# Burningwave JVM Driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40burningwave_org%20JVM%20driver%2C%20a%20%23Java%20driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions&url=https://burningwave.github.io/jvm-driver/)

<a href="https://www.burningwave.org">
<img src="https://raw.githubusercontent.com/burningwave/burningwave.github.io/main/logo.png" alt="Burningwave-logo.png" height="180px" align="right"/>
</a>

[![Maven Central](https://img.shields.io/maven-central/v/org.burningwave/jni-executor)](https://central.sonatype.com/artifact/org.burningwave/jni-executor)
[![GitHub](https://img.shields.io/github/license/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/burningwave/jvm-driver/actions/runs/45988448525)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-7%2C%208%2C%209+%20(27)-blueviolet)](https://github.com/burningwave/jvm-driver/actions/runs/45988448525)

[![GitHub open issues](https://img.shields.io/github/issues/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Repository dependents](https://badgen.net/github/dependents-repo/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/network/dependents)
[![HitCount](https://www.burningwave.org/generators/generate-visited-pages-badge.php)](https://www.burningwave.org#bw-counters)

**JNI Executor** is a custom and [**extremely optimized JNI engine**](https://github.com/burningwave/jvm-driver/issues/3#issuecomment-1149840706) written in C++ that allows deep interaction with the JVM **without any restrictions**: it finds fields, reads and writes their values, allocates instances without calling their constructors and throws exceptions, regardless of access modifiers and module encapsulation. Since version 10.0.0 it is the default JNI engine of the drivers of [**ToolFactory JVM Driver**](https://toolfactory.github.io/jvm-driver/).

**Since version 9.0.0 Burningwave JVM Driver no longer contains drivers**: the drivers deprecated in version 8.24.0 have been removed, because the drivers of ToolFactory JVM Driver do the same with the JNI engine of Burningwave. Since version 9.0.0 the `org.burningwave:jvm-driver` artifact is a [**relocation**](https://maven.apache.org/guides/mini/guide-relocation.html) to `io.github.toolfactory:jvm-driver`: see how to [**move to ToolFactory JVM Driver**](#Moving-to-ToolFactory-JVM-Driver).

<br/>

To include JNI Executor in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>org.burningwave</groupId>
    <artifactId>jni-executor</artifactId>
    <version>1.1.2</version>
</dependency>
```
### Requiring the JNI Executor module

To use JNI Executor as a Java module, add the following to your `module-info.java`:

```java
requires org.burningwave.jni;
```

<br/>

## Supported platforms

JNI Executor has no dependencies and contains the native libraries of all the following platforms, loading the one of the platform it runs on:

| Platform | Native library |
|:--|:--|
| Windows x64 | `Executor-x64.dll` |
| Windows arm64 | `Executor-aarch64.dll` |
| Windows x86 (32-bit) | `Executor-x32.dll` |
| Linux x64 | `libExecutor-x64.so` |
| Linux arm64 | `libExecutor-aarch64.so` |
| Linux x86 (32-bit) | `libExecutor-x32.so` |
| macOS x64 | `libExecutor-x64.dylib` |
| macOS arm64 (Apple Silicon) | `libExecutor-aarch64.dylib` |

The native libraries for Linux link the C++ runtime statically, so they do not require libstdc++.

<br/>

## Usage

All the functionalities of JNI Executor are offered by the `org.burningwave.jni.Executor` class. For example:
```java
org.burningwave.jni.Executor executor = org.burningwave.jni.Executor.getInstance();
//The last parameter is the JNI signature of the type of the field
java.lang.reflect.Field field = executor.getDeclaredField(Person.class, "name", "Ljava/lang/String;");
Person person = (Person)executor.allocateInstance(Person.class);
executor.setFieldValue(person, field, "Alice");
String name = (String)executor.getFieldValue(person, field);
```

On JDK 24 and later the JVM prints a warning when JNI Executor loads its native library, and a future JDK release will block it unless native access is enabled. To enable it, start the JVM with `--enable-native-access=ALL-UNNAMED` or, on the module path, with `--enable-native-access=org.burningwave.jni`.

<br/>

## <a name="Moving-to-ToolFactory-JVM-Driver"></a>Moving to ToolFactory JVM Driver

The drivers of Burningwave JVM Driver extended the ones of ToolFactory JVM Driver, which since version 10.0.0 offers the same drivers with the JNI engine of Burningwave. To move to it:
* replace the dependency on `org.burningwave:jvm-driver` with the one on ToolFactory JVM Driver:
```xml
<dependency>
    <groupId>io.github.toolfactory</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>10.1.1</version>
</dependency>
```
* use the classes with the same names of the `io.github.toolfactory.jvm` package instead of the `DynamicDriver`, `HybridDriver` and `NativeDriver` classes of the `org.burningwave.jvm` package, and `org.burningwave.jni.Executor` instead of `org.burningwave.jvm.NativeExecutor`;
* in your `module-info.java`, require `io.github.toolfactory.jvm` instead of `org.burningwave.jvm`, as described in the [**README of ToolFactory JVM Driver**](https://github.com/toolfactory/jvm-driver#requiring-the-toolfactory-jvm-driver-module).

The `io.github.toolfactory.jvm.Driver` interface and its `Factory` stay the same. If you keep declaring `org.burningwave:jvm-driver` from version 9.0.0 on, Maven resolves it as ToolFactory JVM Driver and prints a warning that suggests to update the dependency.

<br/>

## Benchmark

The [**benchmark**](https://github.com/burningwave/jvm-driver/tree/main/benchmark) folder contains a [JMH](https://github.com/openjdk/jmh) benchmark that compares JNI Executor with [**Narcissus**](https://toolfactory.github.io/narcissus/) by running the same operations with several approaches, using `sun.misc.Unsafe`, standard reflection, `VarHandle` and the default and native drivers of ToolFactory JVM Driver as reference.

The benchmark runs on GitHub Actions on Linux and macOS, x64 and aarch64, and on Windows x64, when a pull request changes it or when it is started manually. Each run measures the throughput and the average time of every operation and shows the results in its summary: see the [**runs of the benchmark workflow**](https://github.com/burningwave/jvm-driver/actions/workflows/%5BM%5D%20benchmark.yml). Results on shared CI runners vary from run to run.

To run the benchmark locally you need a JDK 11 or later and Apache Maven:
```bash
mvn package -f benchmark/pom.xml
java -jar benchmark/target/benchmarks.jar
```

The last command measures the throughput in operations per millisecond: add `-bm avgt -tu ns` to measure the average time in nanoseconds per operation, or any other JMH option, for example `-f 1 -wi 1 -i 1 InstanceStringFieldBenchmark` for a quick run of a single class. By default the benchmark uses the release of JNI Executor set in the `jni-executor.version` property of [`benchmark/pom.xml`](https://github.com/burningwave/jvm-driver/blob/main/benchmark/pom.xml), downloaded from Maven Central. To measure the code of your working copy, build the native library of your platform, install the jni-executor module and pass its version to the build of the benchmark:
```bash
mvn compile -f native/pom.xml
mvn install -Dproject_jdk_version=8 -DskipTests=true -f jni-executor/pom.xml
mvn package -f benchmark/pom.xml -Djni-executor.version=<version>
```

<br/>

## Compilation requirements

**A JDK version 9 or higher and a GCC compiler are required to compile the project** (on Linux also g++, which links the native libraries). On Windows you can find compiler and GDB debugger from [**MinGW-W64 project**](https://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win64/Personal%20Builds/mingw-builds/8.1.0/threads-win32/seh/). On Windows aarch64 the native library is compiled with [**llvm-mingw**](https://github.com/mstorsjo/llvm-mingw), whose gcc program accepts the same options: its C++ runtime, libc++, is linked statically.

The modules are built in this order:
1. **native** compiles the C++ sources of the JNI engine into the [native/bin](https://github.com/burningwave/jvm-driver/tree/main/native/bin) folder
2. **jni-executor** builds the JNI Executor artifact, which contains the native libraries of the native/bin folder
3. **java** contains only the relocation of the `org.burningwave:jvm-driver` artifact to ToolFactory JVM Driver

Running `mvn package` in the root folder builds the three modules in this order. To build only JNI Executor with the native libraries already in the native/bin folder, run `mvn package -pl jni-executor`. The tests run only with `-DskipTests=false`.

<br />

## Contributing
The [native module](https://github.com/burningwave/jvm-driver/tree/main/native) is currently compiled for Linux x86/x64/aarch64, Windows x86/x64/aarch64 and macOS x64/aarch64: **feel free to contribute native code builds for other platforms or architectures**. Once compiled, the library must be added to the [native/bin](https://github.com/burningwave/jvm-driver/tree/main/native/bin) folder with the name expected by the [native library loader](https://github.com/burningwave/jvm-driver/blob/main/jni-executor/src/main/java/org/burningwave/jni/Libraries.java).

<br />

# <a name="Ask-for-assistance"></a>Ask for assistance
**For assistance you can**:
* [open a discussion](https://github.com/burningwave/jvm-driver/discussions) here on GitHub
* [report a bug](https://github.com/burningwave/jvm-driver/issues) here on GitHub
* ask on [Stack Overflow](https://stackoverflow.com/search?q=burningwave)
