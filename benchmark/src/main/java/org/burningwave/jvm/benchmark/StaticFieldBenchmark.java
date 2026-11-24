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
 * Reads and writes the private static fields of {@link Target}.
 * <p>
 * Each benchmark is named {@code <operation>_<engine>}, like in {@link InstanceFieldBenchmark}. The operations without
 * the {@code Typed} suffix use the methods that accept fields of any type ({@code getFieldValue}/{@code setFieldValue}
 * of Burningwave, {@code getStaticField}/{@code setStaticField} of Narcissus, {@code Field.get}/{@code Field.set}),
 * which box the {@code int} values; the others use the methods dedicated to the type of the field.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Thread)
public class StaticFieldBenchmark {
	private static final Field OBJECT_FIELD = Fields.accessible("staticObjectValue");
	private static final Field INT_FIELD = Fields.accessible("staticIntValue");
	private static final VarHandle OBJECT_HANDLE = Fields.varHandle(OBJECT_FIELD);
	private static final VarHandle INT_HANDLE = Fields.varHandle(INT_FIELD);

	private Object objectValue;
	private int intValue;
	private Integer boxedIntValue;

	@Setup
	public void setup() {
		objectValue = "new value";
		intValue = Target.INITIAL_INT_VALUE + 1;
		boxedIntValue = intValue;
	}


	@Benchmark
	public void getObject_burningwave(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getFieldValue(null, OBJECT_FIELD));
	}

	@Benchmark
	public void getObject_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getStaticField(OBJECT_FIELD));
	}

	@Benchmark
	public void getObject_reflection(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(OBJECT_FIELD.get(null));
	}


	@Benchmark
	public void getObjectTyped_burningwave(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getStaticObjectFieldValue(OBJECT_FIELD));
	}

	@Benchmark
	public void getObjectTyped_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getStaticObjectField(OBJECT_FIELD));
	}

	@Benchmark
	public void getObjectTyped_varHandle(Blackhole blackhole) {
		blackhole.consume((Object)OBJECT_HANDLE.get());
	}


	@Benchmark
	public void getInt_burningwave(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getFieldValue(null, INT_FIELD));
	}

	@Benchmark
	public void getInt_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getStaticField(INT_FIELD));
	}

	@Benchmark
	public void getInt_reflection(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(INT_FIELD.get(null));
	}


	@Benchmark
	public void getIntTyped_burningwave(BurningwaveEngine engine, Blackhole blackhole) {
		blackhole.consume(NativeExecutor.getInstance().getStaticIntFieldValue(INT_FIELD));
	}

	@Benchmark
	public void getIntTyped_narcissus(NarcissusEngine engine, Blackhole blackhole) {
		blackhole.consume(Narcissus.getStaticIntField(INT_FIELD));
	}

	@Benchmark
	public void getIntTyped_reflection(Blackhole blackhole) throws IllegalAccessException {
		blackhole.consume(INT_FIELD.getInt(null));
	}

	@Benchmark
	public void getIntTyped_varHandle(Blackhole blackhole) {
		blackhole.consume((int)INT_HANDLE.get());
	}


	@Benchmark
	public void setObject_burningwave(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setFieldValue(null, OBJECT_FIELD, objectValue);
	}

	@Benchmark
	public void setObject_narcissus(NarcissusEngine engine) {
		Narcissus.setStaticField(OBJECT_FIELD, objectValue);
	}

	@Benchmark
	public void setObject_reflection() throws IllegalAccessException {
		OBJECT_FIELD.set(null, objectValue);
	}


	@Benchmark
	public void setObjectTyped_burningwave(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setStaticObjectFieldValue(OBJECT_FIELD, objectValue);
	}

	@Benchmark
	public void setObjectTyped_narcissus(NarcissusEngine engine) {
		Narcissus.setStaticObjectField(OBJECT_FIELD, objectValue);
	}

	@Benchmark
	public void setObjectTyped_varHandle() {
		OBJECT_HANDLE.set(objectValue);
	}


	@Benchmark
	public void setInt_burningwave(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setFieldValue(null, INT_FIELD, boxedIntValue);
	}

	@Benchmark
	public void setInt_narcissus(NarcissusEngine engine) {
		Narcissus.setStaticField(INT_FIELD, boxedIntValue);
	}

	@Benchmark
	public void setInt_reflection() throws IllegalAccessException {
		INT_FIELD.set(null, boxedIntValue);
	}


	@Benchmark
	public void setIntTyped_burningwave(BurningwaveEngine engine) {
		NativeExecutor.getInstance().setStaticIntFieldValue(INT_FIELD, intValue);
	}

	@Benchmark
	public void setIntTyped_narcissus(NarcissusEngine engine) {
		Narcissus.setStaticIntField(INT_FIELD, intValue);
	}

	@Benchmark
	public void setIntTyped_reflection() throws IllegalAccessException {
		INT_FIELD.setInt(null, intValue);
	}

	@Benchmark
	public void setIntTyped_varHandle() {
		INT_HANDLE.set(intValue);
	}

}
