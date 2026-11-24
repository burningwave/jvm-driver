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

import org.burningwave.jvm.NativeExecutor;
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
 * Reads and writes the private instance {@code int} field of {@link Target}.
 * <p>
 * See {@link InstanceStringFieldBenchmark} for the names of the benchmarks.
 * The methods without the {@code _typed} suffix accept fields of any type, so they box the value;
 * the others are dedicated to the {@code int} type.
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)
public class InstanceIntFieldBenchmark {
	private static final Field FIELD = Fields.accessible("intValue");
	private static final VarHandle HANDLE = Fields.varHandle(FIELD);
	private static final long OFFSET = Fields.UNSAFE.objectFieldOffset(FIELD);

	private Target target;
	private int value;
	private Integer boxedValue;

	@Setup
	public void setup() {
		target = new Target();
		value = Target.INITIAL_INT_VALUE + 1;
		boxedValue = value;
	}


	@Benchmark
	public void get02_java_unsafe(Blackhole blackhole) {
		blackhole.consume(Fields.UNSAFE.getInt(target, OFFSET));
	}

	@Benchmark
	public void get03_java_reflect_field(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(FIELD.get(target));
	}

	@Benchmark
	public void get03_java_reflect_field_typed(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(FIELD.getInt(target));
	}

	@Benchmark
	public void get05_java_invoke_varHandle(Blackhole blackhole) {
		blackhole.consume((int)HANDLE.get(target));
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
	public void get16_jvm_driver_native_narcissus(Drivers.NarcissusNative driver, Blackhole blackhole) {
		blackhole.consume(driver.instance.<Object>getFieldValue(target, FIELD));
	}

	@Benchmark
	public void get17_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getField(target, FIELD));
	}

	@Benchmark
	public void get17_narcissus_typed(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getIntField(target, FIELD));
	}

	@Benchmark
	public void get20_burningwave_native_executor(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getFieldValue(target, FIELD));
	}

	@Benchmark
	public void get20_burningwave_native_executor_typed(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getIntFieldValue(target, FIELD));
	}

	@Benchmark
	public void set02_java_unsafe() {
		Fields.UNSAFE.putInt(target, OFFSET, value);
	}

	@Benchmark
	public void set03_java_reflect_field() throws IllegalAccessException {
		FIELD.set(target, boxedValue);
	}

	@Benchmark
	public void set03_java_reflect_field_typed() throws IllegalAccessException {
		FIELD.setInt(target, value);
	}

	@Benchmark
	public void set05_java_invoke_varHandle() {
		HANDLE.set(target, value);
	}

	@Benchmark
	public void set15_jvm_driver_default(Drivers.Default driver) {
		driver.instance.setFieldValue(target, FIELD, boxedValue);
	}

	@Benchmark
	public void set16_jvm_driver_native_burningwave(Drivers.BurningwaveNative driver) {
		driver.instance.setFieldValue(target, FIELD, boxedValue);
	}

	@Benchmark
	public void set16_jvm_driver_native_narcissus(Drivers.NarcissusNative driver) {
		driver.instance.setFieldValue(target, FIELD, boxedValue);
	}

	@Benchmark
	public void set17_narcissus(NarcissusEngine engine) {
		Narcissus.setField(target, FIELD, boxedValue);
	}

	@Benchmark
	public void set17_narcissus_typed(NarcissusEngine engine) {
		Narcissus.setIntField(target, FIELD, value);
	}

	@Benchmark
	public void set20_burningwave_native_executor(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setFieldValue(target, FIELD, boxedValue);
	}

	@Benchmark
	public void set20_burningwave_native_executor_typed(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setIntFieldValue(target, FIELD, value);
	}

}
