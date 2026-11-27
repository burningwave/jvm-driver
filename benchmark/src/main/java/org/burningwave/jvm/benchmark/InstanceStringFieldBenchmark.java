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


import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.burningwave.jni.Executor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import io.github.toolfactory.narcissus.Narcissus;


/**
 * Reads and writes the private instance {@code String} field of {@link Target}.
 * <p>
 * The names of the benchmarks follow the ones of the benchmark published in 2022 in
 * <a href="https://github.com/toolfactory/narcissus/discussions/11">toolfactory/narcissus#11</a>, which read
 * and wrote a private instance {@code String} field: {@code get} or {@code set}, the number of the approach
 * in that benchmark and the approach.
 * <ul>
 * <li>{@code 02_java_unsafe}: {@code sun.misc.Unsafe} with the offset of the field;</li>
 * <li>{@code 03_java_reflect_field}: {@link Field#get(Object)} and {@link Field#set(Object, Object)} after
 * {@code setAccessible(true)};</li>
 * <li>{@code 05_java_invoke_varHandle}: {@link VarHandle};</li>
 * <li>{@code 15_jvm_driver_default}: the default driver of ToolFactory JVM Driver;</li>
 * <li>{@code 16_jvm_driver_native_burningwave}: the native driver of ToolFactory JVM Driver, which uses the
 * JNI engine of Burningwave, see {@link Drivers};</li>
 * <li>{@code 17_narcissus}: Narcissus;</li>
 * <li>{@code 20_burningwave_native_executor}: {@link Executor}, the JNI engine of Burningwave JVM
 * Driver.</li>
 * </ul>
 * JMH runs the benchmarks in alphabetical order, so the approaches are measured in this order. The methods
 * with the {@code _typed} suffix are the ones dedicated to the type of the field ({@code getObjectField} of
 * Narcissus, {@code getObjectFieldValue} of {@link Executor}); the others accept fields of any type.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)
public class InstanceStringFieldBenchmark {
	private static final Field FIELD = Fields.accessible("stringValue");
	private static final VarHandle HANDLE = Fields.varHandle(FIELD);
	private static final long OFFSET = Fields.UNSAFE.objectFieldOffset(FIELD);

	private Target target;
	private String value;

	@Setup
	public void setup() {
		target = new Target();
		value = "new value";
	}


	@Benchmark
	public void get02_java_unsafe(Blackhole blackhole) {
		blackhole.consume(Fields.UNSAFE.getObject(target, OFFSET));
	}

	@Benchmark
	public void get03_java_reflect_field(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(FIELD.get(target));
	}

	@Benchmark
	public void get05_java_invoke_varHandle(Blackhole blackhole) {
		blackhole.consume((String)HANDLE.get(target));
	}

	@Benchmark
	public void get15_jvm_driver_default(Drivers.Default driver, Blackhole blackhole) {
		blackhole.consume(driver.instance.<Object>getFieldValue(target, FIELD));
	}

	@Benchmark
	public void get16_jvm_driver_native_burningwave(Drivers.BurningwaveNative driver, Blackhole blackhole) {
		blackhole.consume(driver.instance.<Object>getFieldValue(target, FIELD));
	}

	@Benchmark
	public void get17_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getField(target, FIELD));
	}

	@Benchmark
	public void get17_narcissus_typed(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getObjectField(target, FIELD));
	}

	@Benchmark
	public void get20_burningwave_native_executor(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(Executor.getInstance().getFieldValue(target, FIELD));
	}

	@Benchmark
	public void get20_burningwave_native_executor_typed(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(Executor.getInstance().getObjectFieldValue(target, FIELD));
	}

	@Benchmark
	public void set02_java_unsafe() {
		Fields.UNSAFE.putObject(target, OFFSET, value);
	}

	@Benchmark
	public void set03_java_reflect_field() throws IllegalAccessException {
		FIELD.set(target, value);
	}

	@Benchmark
	public void set05_java_invoke_varHandle() {
		HANDLE.set(target, value);
	}

	@Benchmark
	public void set15_jvm_driver_default(Drivers.Default driver) {
		driver.instance.setFieldValue(target, FIELD, value);
	}

	@Benchmark
	public void set16_jvm_driver_native_burningwave(Drivers.BurningwaveNative driver) {
		driver.instance.setFieldValue(target, FIELD, value);
	}

	@Benchmark
	public void set17_narcissus(NarcissusEngine engine) {
		Narcissus.setField(target, FIELD, value);
	}

	@Benchmark
	public void set17_narcissus_typed(NarcissusEngine engine) {
		Narcissus.setObjectField(target, FIELD, value);
	}

	@Benchmark
	public void set20_burningwave_native_executor(BurningwaveEngine engine) {
		Executor.getInstance().setFieldValue(target, FIELD, value);
	}

	@Benchmark
	public void set20_burningwave_native_executor_typed(BurningwaveEngine engine) {
		Executor.getInstance().setObjectFieldValue(target, FIELD, value);
	}

}
