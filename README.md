# Burningwave JVM Driver [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=%40burningwave_sw%20JVM%20driver%2C%20a%20%23driver%20to%20allow%20deep%20interaction%20with%20the%20JVM%20without%20any%20restrictions%20%28works%20on%20%23Java7%20%23Java8%20%23Java9%20%23Java10%20%23Java11%20%23Java12%20%23Java13%20%23Java14%20%23Java15%20%23Java16%20%23Java17%29&url=https://burningwave.github.io/jvm-driver/)

<a href="https://www.burningwave.org">
<img src="https://raw.githubusercontent.com/burningwave/burningwave.github.io/main/logo.png" alt="Burningwave-logo.png" height="180px" align="right"/>
</a>

[![Maven Central with version prefix filter](https://img.shields.io/maven-central/v/org.burningwave/jvm-driver/6)](https://maven-badges.herokuapp.com/maven-central/org.burningwave/jvm-driver/)
[![GitHub](https://img.shields.io/github/license/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/blob/main/LICENSE)

[![Platforms](https://img.shields.io/badge/platforms-Windows%2C%20Mac%20OS%2C%20Linux-orange)](https://github.com/burningwave/jvm-driver/actions/runs/1591965837)

[![Supported JVM](https://img.shields.io/badge/supported%20JVM-7%2C%208%2C%209+%20(17)-blueviolet)](https://github.com/burningwave/jvm-driver/actions/runs/1591965837)

[![GitHub open issues](https://img.shields.io/github/issues/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/issues?q=is%3Aissue+is%3Aclosed)

[![Artifact downloads](https://www.burningwave.org/generators/generate-burningwave-artifact-downloads-badge.php?type=svgggggg&artifactId=jvm-driver)](https://www.burningwave.org/artifact-downloads/?show-monthly-trend-chart=false)
[![Repository dependents](https://badgen.net/github/dependents-repo/burningwave/jvm-driver)](https://github.com/burningwave/jvm-driver/network/dependents)

A driver derived from [**ToolFactory JVM Driver**](https://toolfactory.github.io/jvm-driver/) with a fully extensible architecture and with a custom JNI engine to allow deep interaction with the JVM **without any restrictions**.

<br/>

To include Burningwave JVM Driver in your projects simply use with **Apache Maven**:
```xml
<dependency>
    <groupId>org.burningwave</groupId>
    <artifactId>jvm-driver</artifactId>
    <version>6.7.0</version>
</dependency>	
```
### Requiring the Burningwave JMV Driver module

To use Burningwave JMV Driver as a Java module, add the following to your `module-info.java`: 

```java
//Mandatory if you use the default, dynamic or hybrid driver
requires jdk.unsupported;

requires org.burningwave.jvm;
```

<br/>

## Overview

There are four kinds of driver:

* the **default driver** completely based on Java api
* the **dynamic driver** that extends the default driver and uses a JNI function only if a Java based function offered by the default driver cannot be initialized
* the **hybrid driver** that extends the default driver and uses some JNI functions only when run on JVM 17 and later
* the **native driver** that extends the hybrid driver and uses JNI functions more consistently regardless of the Java version it is running on

All JNI methods used by the dynamic, hybrid and native driver are supplied by a custom JNI engine written in C++ that works on the following system configurations:
* Windows (x86, x64)
* Linux (x86, x64)
* MacOs (x64) 

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

public <T> T throwException(String message, Object... placeHolderReplacements);

public <T> T throwException(Throwable exception);
```

<br/>

## Compilation requirements

**A JDK version 9 or higher and a GCC compiler are required to compile the project**. On Windows you can find compiler and GDB debugger from [**MinGW-W64 project**](https://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win64/Personal%20Builds/mingw-builds/8.1.0/threads-win32/seh/).

<br />

# <a name="Ask-for-assistance"></a>Ask for assistance
**For assistance you can**:
* [subscribe](https://www.burningwave.org/registration/) to the [forum](https://www.burningwave.org/forum/) and then ask in the topic ["How to do?"](https://www.burningwave.org/forum/forum/how-to-do-3/)
* [open a discussion](https://github.com/burningwave/jvm-driver/discussions) here on GitHub
* [report a bug](https://github.com/burningwave/jvm-driver/issues) here on GitHub
* ask on [Stack Overflow](https://stackoverflow.com/search?q=burningwave)
