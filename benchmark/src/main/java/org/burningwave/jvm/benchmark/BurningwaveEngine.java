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

import org.burningwave.jni.Executor;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;


/**
 * Loads the native library of the Burningwave JNI engine ({@link Executor}) before the measurement and checks
 * that the engine reads a field correctly.
 * <p>
 * Only the benchmarks that declare this state as a parameter load the library: if it cannot be loaded on a
 * platform, only the Burningwave benchmarks fail and the others still run.
 */
@State(Scope.Benchmark)
public class BurningwaveEngine {

	@Setup(Level.Trial)
	public void loadAndCheck() throws NoSuchFieldException {
		Field field = Target.class.getDeclaredField("intValue");
		int value = Executor.getInstance().getIntFieldValue(new Target(), field);
		if (value != Target.INITIAL_INT_VALUE) {
			throw new IllegalStateException(
				"Burningwave read " + value + " instead of " + Target.INITIAL_INT_VALUE + " from " + field
			);
		}
	}

}
