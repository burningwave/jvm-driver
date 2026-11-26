# Burningwave JVM Driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40burningwave_org%20JVM%20driver%2C%20a%20%23Java%20driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions&url=https://burningwave.github.io/jvm-driver/)

<a href="https://www.burningwave.org">
<img src="https://raw.githubusercontent.com/burningwave/burningwave.github.io/main/logo.png" alt="Burningwave-logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/org.burningwave/jvm-driver/8)](https://maven-badges.herokuapp.com/maven-central/org.burningwave/jvm-driver/)
[![GitHub](https://img.shields.io/github/license/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/burningwave/jvm-driver/actions/runs/45650376569)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-7%2C%208%2C%209+%20(26)-blueviolet)](https://github.com/burningwave/jvm-driver/actions/runs/45650376569)

[![GitHub open issues](https://img.shields.io/github/issues/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Artifact downloads](https://www.burningwave.org/generators/generate-burningwave-artifact-downloads-badge.php?artifactId=jvm-driver)](https://www.burningwave.org/artifact-downloads/?show-overall-trend-chart=false&artifactId=jvm-driver&startDate=2021-08)
[![Repository dependents](https://badgen.net/github/dependents-repo/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/network/dependents)
[![HitCount](https://www.burningwave.org/generators/generate-visited-pages-badge.php)](https://www.burningwave.org#bw-counters)

A driver derived from [**ToolFactory JVM Driver**](https://toolfactory.github.io/jvm-driver/) with a fully extensible architecture and with a custom and [**extremely optimized JNI engine**](https://github.com/burningwave/jvm-driver/issues/3#issuecomment-1149840706) to allow deep interaction with the JVM **without any restrictions**. 

<br/>

To include Burningwave JVM Driver in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>org.burningwave</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>8.22.0</version>
</dependency>	
```
### Requiring the Burningwave JVM Driver module

To use Burningwave JMV Driver as a Java module, add the following to your `module-info.java`: 

```java
//Mandatory if you use the default, dynamic or hybrid driver
requires jdk.unsupported;

requires org.burningwave.jvm;

//Mandatory to use io.github.toolfactory.jvm.Driver, because the org.burningwave.jvm module does not require it transitively
requires io.github.toolfactory.jvm;

//Only if you use the JNI engine directly, through the org.burningwave.jni.Executor class
requires org.burningwave.jni;
```

<br/>

## Overview

There are four kinds of driver:

* the **default driver** completely based on Java api
* the **dynamic driver** that extends the default driver and uses a JNI function only if a Java based function offered by the default driver cannot be initialized
* the **hybrid driver** that extends the default driver and uses some JNI functions only when run on JVM 17 and later
* the **native driver** that extends the hybrid driver and uses JNI functions more consistently regardless of the Java version it is running on

All JNI methods used by the dynamic, hybrid and native driver are supplied by a **custom and highly optimized JNI engine written in C++**, released as the separate [**JNI Executor**](#JNI-Executor) artifact, that works on the following system configurations:
* Windows x86/x64/aarch64
* Linux x86/x64/aarch64
* macOS x64/aarch64 (Apple Silicon)

The native libraries for Linux link the C++ runtime statically, so they do not require libstdc++.

<br/>

## Usage

To create a driver instance you should use this code:
```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.getNew();
```

The driver type returned by the method `io.github.toolfactory.jvm.Driver.Factory.getNew()` is **the first driver that can be initialized among the default, hybrid and native drivers respectively**.

If you need to create a specific driver type you should use:

* this code to create a default driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewDefault();
```

* this code to create a dynamic driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewDynamic();
```

* this code to create an hybrid driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewHybrid();
```

* this code to create a native driver instance:

```java
io.github.toolfactory.jvm.Driver driver = io.github.toolfactory.jvm.Driver.Factory.getNewNative();
```

<br/>

Each functionality offered by the driver is **initialized in deferred way** at the first call if the driver is not obtained through the method `io.github.toolfactory.jvm.Driver.getNew()`. However, it is possible to initialize all of the functionalities at once by calling the method `Driver.init()`.

The methods exposed by the Driver interface are the following:
```java
public <D extends Driver> D init();

public <T> T allocateInstance(Class<?> cls);

// Return a ClassLoaderDelegate or the input itself if the input is a 
// BuiltinClassLoader or null if the JVM version is less than 9
public ClassLoader convertToBuiltinClassLoader(ClassLoader classLoader);

public Class<?> defineHookClass(Class<?> clientClass, byte[] byteCode);

public Class<?> getBuiltinClassLoaderClass();

public Class<?> getClassLoaderDelegateClass();

public Class<?> getClassByName(String className, Boolean initialize, ClassLoader classLoader, Class<?> caller);

public MethodHandles.Lookup getConsulter(Class<?> cls);

public <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls);

public Field[] getDeclaredFields(Class<?> cls);

public Method[] getDeclaredMethods(Class<?> cls);

public <T> T getFieldValue(Object target, Field field);

public Package getPackage(ClassLoader classLoader, String packageName);

public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, ClassLoader... classLoaders);

public Collection<URL> getResources(String resourceRelativePath, boolean findFirst, Collection<ClassLoader> classLoaders);

public <T> T invoke(Object target, Method method, Object[] params);

public boolean isBuiltinClassLoader(ClassLoader classLoader);

public boolean isClassLoaderDelegate(ClassLoader classLoader);

public <T> T newInstance(Constructor<T> ctor, Object[] params);

public CleanableSupplier<Collection<Class<?>>> getLoadedClassesRetriever(ClassLoader classLoader);

public Map<String, ?> retrieveLoadedPackages(ClassLoader classLoader);

public void setAccessible(AccessibleObject object, boolean flag);

public void setFieldValue(Object target, Field field, Object value);

public void stop(Thread thread);

public <T> T throwException(String message, Object... placeHolderReplacements);

public <T> T throwException(Throwable exception);
```

<br/>

## <a name="JNI-Executor"></a>JNI Executor

The JNI engine of the dynamic, hybrid and native drivers is the `org.burningwave.jni.Executor` class of the **JNI Executor** artifact, which Burningwave JVM Driver includes as a dependency. JNI Executor has no dependencies and contains the native libraries of all the platforms listed above: it can also be used on its own to find fields, read and write their values, allocate instances without calling their constructors and throw exceptions, regardless of access modifiers and module encapsulation. To use it on its own, add it with **Apache Maven**:
```xml
<dependency>
    <groupId>org.burningwave</groupId>
    <artifactId>jni-executor</artifactId>
    <version>1.0.0</version>
</dependency>
```
To use JNI Executor as a Java module, add the following to your `module-info.java`:
```java
requires org.burningwave.jni;
```
For example:
```java
org.burningwave.jni.Executor executor = org.burningwave.jni.Executor.getInstance();
//The last parameter is the JNI signature of the type of the field
java.lang.reflect.Field field = executor.getDeclaredField(Person.class, "name", "Ljava/lang/String;");
Person person = (Person)executor.allocateInstance(Person.class);
executor.setFieldValue(person, field, "Alice");
String name = (String)executor.getFieldValue(person, field);
```
The `org.burningwave.jvm.NativeExecutor` class, which contained the JNI engine up to version 8.21.0 of Burningwave JVM Driver, is deprecated: it passes all the calls to `org.burningwave.jni.Executor` and it will be removed in version 9.0.0.

<br/>

## Benchmark

The [**benchmark**](https://github.com/burningwave/jvm-driver/tree/main/benchmark) folder contains a [JMH](https://github.com/openjdk/jmh) benchmark that runs the same operations with several approaches.

The benchmark runs on GitHub Actions on Linux and macOS, x64 and aarch64, and on Windows x64, when a pull request changes it or when it is started manually. Each run measures the throughput and the average time of every operation and shows the results in its summary: see the [**runs of the benchmark workflow**](https://github.com/burningwave/jvm-driver/actions/workflows/%5BM%5D%20benchmark.yml). Results on shared CI runners vary from run to run.

To run the benchmark locally you need a JDK 11 or later and Apache Maven:
```bash
mvn package -f benchmark/pom.xml
java -jar benchmark/target/benchmarks.jar
```

The last command measures the throughput in operations per millisecond: add `-bm avgt -tu ns` to measure the average time in nanoseconds per operation, or any other JMH option, for example `-f 1 -wi 1 -i 1 InstanceStringFieldBenchmark` for a quick run of a single class. By default the benchmark uses the release of Burningwave JVM Driver set in the `burningwave-jvm-driver.version` property of [`benchmark/pom.xml`](https://github.com/burningwave/jvm-driver/blob/main/benchmark/pom.xml), downloaded from Maven Central with JNI Executor. To measure the code of your working copy, build the native library of your platform, install the jni-executor and java modules and pass the version of the java module to the build of the benchmark:
```bash
mvn compile -f native/pom.xml
mvn install -Dproject_jdk_version=8 -DskipTests=true -f jni-executor/pom.xml
mvn install -Dproject_jdk_version=8 -DskipTests=true -f java/sa-pom.xml
mvn package -f benchmark/pom.xml -Dburningwave-jvm-driver.version=<version>
```

<br/>

## Compilation requirements

**A JDK version 9 or higher and a GCC compiler are required to compile the project** (on Linux also g++, which links the native libraries). On Windows you can find compiler and GDB debugger from [**MinGW-W64 project**](https://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win64/Personal%20Builds/mingw-builds/8.1.0/threads-win32/seh/). On Windows aarch64 the native library is compiled with [**llvm-mingw**](https://github.com/mstorsjo/llvm-mingw), whose gcc program accepts the same options: its C++ runtime, libc++, is linked statically.

The modules are built in this order:
1. **native** compiles the C++ sources of the JNI engine into the [native/bin](https://github.com/burningwave/jvm-driver/tree/main/native/bin) folder
2. **jni-executor** builds the JNI Executor artifact, which contains the native libraries of the native/bin folder
3. **java** builds Burningwave JVM Driver, which depends on JNI Executor

Running `mvn package` in the root folder builds the three modules in this order. To build only the Java modules with the native libraries already in the native/bin folder, run `mvn package -pl jni-executor,java`. The tests run only with `-DskipTests=false`.

<br />

## Contributing
The [native module](https://github.com/burningwave/jvm-driver/tree/main/native) is currently compiled for Linux x86/x64/aarch64, Windows x86/x64/aarch64 and macOS x64/aarch64: **feel free to contribute native code builds for other platforms or architectures**. Once compiled, the library must be added to the [native/bin](https://github.com/burningwave/jvm-driver/tree/main/native/bin) folder with the name expected by the [native library loader](https://github.com/burningwave/jvm-driver/blob/main/jni-executor/src/main/java/org/burningwave/jni/Libraries.java).

<br />

# <a name="Ask-for-assistance"></a>Ask for assistance
**For assistance you can**:
* [open a discussion](https://github.com/burningwave/jvm-driver/discussions) here on GitHub
* [report a bug](https://github.com/burningwave/jvm-driver/issues) here on GitHub
* ask on [Stack Overflow](https://stackoverflow.com/search?q=burningwave)
