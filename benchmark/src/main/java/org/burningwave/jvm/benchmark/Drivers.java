/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Roberto Gentili
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.burningwave.jvm.benchmark;


import java.lang.reflect.Field;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import io.github.toolfactory.jvm.Driver;


/**
 * The drivers whose {@link Driver#getFieldValue(Object, Field)} and
 * {@link Driver#setFieldValue(Object, Field, Object)} methods are measured.
 * <p>
 * Each driver is created directly and initialized with {@link Driver#init()} instead of being obtained from
 * {@code Driver.Factory}, which returns the drivers of Burningwave when its jar is on the class path: this way it is
 * always clear which engine is measured. Only the benchmarks that declare a driver as a parameter create it.
 */
public final class Drivers {

	private Drivers() {}

	static Driver check(Driver driver) throws NoSuchFieldException {
		Field field = Target.class.getDeclaredField("intValue");
		Object value = driver.getFieldValue(new Target(), field);
		if (!Integer.valueOf(Target.INITIAL_INT_VALUE).equals(value)) {
			throw new IllegalStateException(
				driver.getClass().getName() + " read " + value + " instead of " + Target.INITIAL_INT_VALUE + " from " + field
			);
		}
		return driver;
	}

	/**
	 * The default driver of ToolFactory JVM Driver ({@link io.github.toolfactory.jvm.DefaultDriver}), which accesses
	 * the fields with {@code sun.misc.Unsafe} without native code.
	 */
	@State(Scope.Benchmark)
	public static class Default {
		Driver instance;

		@Setup(Level.Trial)
		public void init() throws NoSuchFieldException {
			instance = check(new io.github.toolfactory.jvm.DefaultDriver().init());
		}
	}

	/**
	 * The native driver of Burningwave JVM Driver ({@link org.burningwave.jvm.NativeDriver}), which accesses the
	 * fields with {@link org.burningwave.jvm.NativeExecutor}.
	 */
	@State(Scope.Benchmark)
	public static class BurningwaveNative {
		Driver instance;

		@Setup(Level.Trial)
		public void init() throws NoSuchFieldException {
			instance = check(new org.burningwave.jvm.NativeDriver().init());
		}
	}

	/**
	 * The native driver of ToolFactory JVM Driver ({@link io.github.toolfactory.jvm.NativeDriver}), which accesses the
	 * fields with Narcissus.
	 */
	@State(Scope.Benchmark)
	public static class NarcissusNative {
		Driver instance;

		@Setup(Level.Trial)
		public void init() throws NoSuchFieldException {
			instance = check(new io.github.toolfactory.jvm.NativeDriver().init());
		}
	}

}
