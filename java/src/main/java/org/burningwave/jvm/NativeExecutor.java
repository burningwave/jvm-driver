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
 * Copyright (c) 2021 Roberto Gentili
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
package org.burningwave.jvm;


import java.lang.reflect.Field;

import org.burningwave.jni.Executor;


/**
 * The JNI engine of Burningwave JVM Driver up to version 8.21.0: it now passes all the calls to {@link Executor}.
 *
 * @deprecated The JNI engine is now the {@link org.burningwave.jni.Executor} class of the
 * {@code org.burningwave:jni-executor} artifact, on which Burningwave JVM Driver depends: use it instead. This class
 * will be removed in version 9.0.0.
 */
@Deprecated
public class NativeExecutor {
	private final static NativeExecutor INSTANCE = new NativeExecutor(Executor.getInstance());

	private final Executor executor;

	private NativeExecutor(Executor executor) {
		this.executor = executor;
	}

	public final static NativeExecutor getInstance() {
		return INSTANCE;
	}

	public Object getFieldValue(Object target, Field field) {
		return executor.getFieldValue(target, field);
	}

	public void setFieldValue(Object origTarget, Field field, Object value) {
		executor.setFieldValue(origTarget, field, value);
	}

	public Field getDeclaredField(Class<?> target, String name, String signature) {
		return executor.getDeclaredField(target, name, signature);
	}

	public Field getDeclaredStaticField(Class<?> target, String name, String signature) {
		return executor.getDeclaredStaticField(target, name, signature);
	}

	public Object getObjectFieldValue(Object target, Field field) {
		return executor.getObjectFieldValue(target, field);
	}

	public short getShortFieldValue(Object target, Field field) {
		return executor.getShortFieldValue(target, field);
	}

	public int getIntFieldValue(Object target, Field field) {
		return executor.getIntFieldValue(target, field);
	}

	public long getLongFieldValue(Object target, Field field) {
		return executor.getLongFieldValue(target, field);
	}

	public float getFloatFieldValue(Object target, Field field) {
		return executor.getFloatFieldValue(target, field);
	}

	public double getDoubleFieldValue(Object target, Field field) {
		return executor.getDoubleFieldValue(target, field);
	}

	public boolean getBooleanFieldValue(Object target, Field field) {
		return executor.getBooleanFieldValue(target, field);
	}

	public byte getByteFieldValue(Object target, Field field) {
		return executor.getByteFieldValue(target, field);
	}

	public char getCharFieldValue(Object target, Field field) {
		return executor.getCharFieldValue(target, field);
	}

	public Object getStaticObjectFieldValue(Field field) {
		return executor.getStaticObjectFieldValue(field);
	}

	public short getStaticShortFieldValue(Field field) {
		return executor.getStaticShortFieldValue(field);
	}

	public int getStaticIntFieldValue(Field field) {
		return executor.getStaticIntFieldValue(field);
	}

	public long getStaticLongFieldValue(Field field) {
		return executor.getStaticLongFieldValue(field);
	}

	public float getStaticFloatFieldValue(Field field) {
		return executor.getStaticFloatFieldValue(field);
	}

	public double getStaticDoubleFieldValue(Field field) {
		return executor.getStaticDoubleFieldValue(field);
	}

	public boolean getStaticBooleanFieldValue(Field field) {
		return executor.getStaticBooleanFieldValue(field);
	}

	public byte getStaticByteFieldValue(Field field) {
		return executor.getStaticByteFieldValue(field);
	}

	public char getStaticCharFieldValue(Field field) {
		return executor.getStaticCharFieldValue(field);
	}

	public void setObjectFieldValue(Object target, Field field, Object value) {
		executor.setObjectFieldValue(target, field, value);
	}

	public void setShortFieldValue(Object target, Field field, short value) {
		executor.setShortFieldValue(target, field, value);
	}

	public void setIntFieldValue(Object target, Field field, int value) {
		executor.setIntFieldValue(target, field, value);
	}

	public void setLongFieldValue(Object target, Field field, long value) {
		executor.setLongFieldValue(target, field, value);
	}

	public void setFloatFieldValue(Object target, Field field, float value) {
		executor.setFloatFieldValue(target, field, value);
	}

	public void setDoubleFieldValue(Object target, Field field, double value) {
		executor.setDoubleFieldValue(target, field, value);
	}

	public void setBooleanFieldValue(Object target, Field field, boolean value) {
		executor.setBooleanFieldValue(target, field, value);
	}

	public void setByteFieldValue(Object target, Field field, byte value) {
		executor.setByteFieldValue(target, field, value);
	}

	public void setCharFieldValue(Object target, Field field, char value) {
		executor.setCharFieldValue(target, field, value);
	}

	public void setStaticObjectFieldValue(Field field, Object value) {
		executor.setStaticObjectFieldValue(field, value);
	}

	public void setStaticShortFieldValue(Field field, short value) {
		executor.setStaticShortFieldValue(field, value);
	}

	public void setStaticIntFieldValue(Field field, int value) {
		executor.setStaticIntFieldValue(field, value);
	}

	public void setStaticLongFieldValue(Field field, long value) {
		executor.setStaticLongFieldValue(field, value);
	}

	public void setStaticFloatFieldValue(Field field, float value) {
		executor.setStaticFloatFieldValue(field, value);
	}

	public void setStaticDoubleFieldValue(Field field, double value) {
		executor.setStaticDoubleFieldValue(field, value);
	}

	public void setStaticBooleanFieldValue(Field field, boolean value) {
		executor.setStaticBooleanFieldValue(field, value);
	}

	public void setStaticByteFieldValue(Field field, byte value) {
		executor.setStaticByteFieldValue(field, value);
	}

	public void setStaticCharFieldValue(Field field, char value) {
		executor.setStaticCharFieldValue(field, value);
	}

	public Object allocateInstance(Class<?> cls) {
		return executor.allocateInstance(cls);
	}

	public void throwException(Throwable exc) {
		executor.throwException(exc);
	}


	/**
	 * @deprecated Use {@link org.burningwave.jni.Executor.Initializer} instead. This class will be removed in version
	 * 9.0.0.
	 */
	@Deprecated
	public static class Initializer {

		public static NativeExecutor setNativeLibraryNameAndInit(String name) {
			Executor.Initializer.setNativeLibraryNameAndInit(name);
			return NativeExecutor.getInstance();
		}

	}
}
