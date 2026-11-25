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


import java.util.concurrent.TimeUnit;

import org.burningwave.jvm.NativeExecutor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.github.toolfactory.narcissus.Narcissus;


/**
 * Finds the private instance {@code String} field of {@link Target} by name.
 * <p>
 * The names of the benchmarks follow the ones of {@link InstanceStringFieldBenchmark}. Burningwave also needs the JNI
 * signature of the field and finds it with {@code GetFieldID} and {@code ToReflectedField}; Narcissus needs only the
 * name and scans the fields declared by the class and by its superclasses, which it reads by invoking
 * {@code Class.getDeclaredFields0} through JNI; the reflection reads the cached fields of the class.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class FieldLookupBenchmark {
	private static final String FIELD_NAME = "stringValue";
	private static final String FIELD_SIGNATURE = "Ljava/lang/String;";

	@Benchmark
	public void findField03_java_reflect_field(Blackhole blackhole) throws NoSuchFieldException {
		blackhole.consume(Target.class.getDeclaredField(FIELD_NAME));
	}

	@Benchmark
	public void findField17_narcissus(NarcissusEngine engine, Blackhole blackhole) throws NoSuchFieldException {
		blackhole.consume(Narcissus.findField(Target.class, FIELD_NAME));
	}

	@Benchmark
	public void findField20_burningwave_native_executor(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getDeclaredField(Target.class, FIELD_NAME, FIELD_SIGNATURE));
	}

}
