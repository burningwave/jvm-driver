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
 * Copyright (c) 2019-2021 Roberto Gentili
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
import java.lang.reflect.Modifier;

import org.burningwave.jvm.util.Libraries;

import io.github.toolfactory.jvm.util.Classes;


public class NativeExecutor {
	private final static NativeExecutor INSTANCE;

	private NativeExecutor() {}

	static {
		try {
			Libraries.getInstance().loadFor(NativeExecutor.class);
		} catch (Throwable exc) {
			throw new ExceptionInInitializerError(exc);
		}
		INSTANCE = new NativeExecutor();
	}

	public final static NativeExecutor getInstance() {
		return INSTANCE;
	}

	public Object getFieldValue(Object target, Field field) {
		Class<?> fieldType = field.getType();
		if (Modifier.isStatic(field.getModifiers())) {
			target = field.getDeclaringClass();
			if(!fieldType.isPrimitive()) {
				return getStaticObjectFieldValue((Class<?>)target, field);
			} else if (fieldType == int.class) {
				return getStaticIntegerFieldValue((Class<?>)target, field);
			} else if (fieldType == long.class) {
				return getStaticLongFieldValue((Class<?>)target, field);
			} else if (fieldType == float.class) {
				return getStaticFloatFieldValue((Class<?>)target, field);
			} else if (fieldType == double.class) {
				return getStaticDoubleFieldValue((Class<?>)target, field);
			} else if (fieldType == boolean.class) {
				return getStaticBooleanFieldValue((Class<?>)target, field);
			} else if (fieldType == byte.class) {
				return getStaticByteFieldValue((Class<?>)target, field);
			} else {
				return getStaticCharacterFieldValue((Class<?>)target, field);
			}
		} else {
			if(!fieldType.isPrimitive()) {
				return getObjectFieldValue(target, field);
			} else if (fieldType == int.class) {
				return getIntegerFieldValue(target, field);
			} else if (fieldType == long.class) {
				return getLongFieldValue(target, field);
			} else if (fieldType == float.class) {
				return getFloatFieldValue(target, field);
			} else if (fieldType == double.class) {
				return getDoubleFieldValue(target, field);
			} else if (fieldType == boolean.class) {
				return getBooleanFieldValue(target, field);
			} else if (fieldType == byte.class) {
				return getByteFieldValue(target, field);
			} else {
				return getCharacterFieldValue(target, field);
			}
		}
	}


	public void setFieldValue(Object origTarget, Field field, Object value) {
		if(value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
			throw new IllegalArgumentException("Value " + value + " is not assignable to " + field.getName());
		}
		Class<?> fieldType = field.getType();
		Object target = Modifier.isStatic(field.getModifiers())?
			field.getDeclaringClass() :
			origTarget;
		if (Modifier.isStatic(field.getModifiers())) {
			target = field.getDeclaringClass();
			if(!fieldType.isPrimitive()) {
				setStaticObjectFieldValue((Class<?>)target, field, value);
			} else if (fieldType == int.class) {
				setStaticIntegerFieldValue((Class<?>)target, field, (Integer)value);
			} else if (fieldType == long.class) {
				setStaticLongFieldValue((Class<?>)target, field, (Long)value);
			} else if (fieldType == float.class) {
				setStaticFloatFieldValue((Class<?>)target, field, (Float)value);
			} else if (fieldType == double.class) {
				setStaticDoubleFieldValue((Class<?>)target, field, (Double)value);
			} else if (fieldType == boolean.class) {
				setStaticBooleanFieldValue((Class<?>)target, field, (Boolean)value);
			} else if (fieldType == byte.class) {
				setStaticByteFieldValue((Class<?>)target, field, (Byte)value);
			} else {
				setStaticCharacterFieldValue((Class<?>)target, field, (Character)value);
			}
		} else {
			if(!fieldType.isPrimitive()) {
				setObjectFieldValue(target, field, value);
			} else if (fieldType == int.class) {
				setIntegerFieldValue(target, field, (Integer)value);
			} else if (fieldType == long.class) {
				setLongFieldValue(target, field, (Long)value);
			} else if (fieldType == float.class) {
				setFloatFieldValue(target, field, (Float)value);
			} else if (fieldType == double.class) {
				setDoubleFieldValue(target, field, (Double)value);
			} else if (fieldType == boolean.class) {
				setBooleanFieldValue(target, field, (Boolean)value);
			} else if (fieldType == byte.class) {
				setByteFieldValue(target, field, (Byte)value);
			} else {
				setCharacterFieldValue(target, field, (Character)value);
			}
		}
	}

	public Field getDeclaredField(Class<?> target, String name, String signature) {
		return getDeclaredField(
			check(target, "Target class is null"),
			check(name, "Field name is null"),
			check(signature, "Field signature is null"),
			false
		);
	}

	public Field getDeclaredStaticField(Class<?> target, String name, String signature) {
		return getDeclaredField(
			check(target, "Target class is null"),
			check(name, "Field name is null"),
			check(signature, "Field signature is null"),
			true
		);
	}

	private <T> T check(T input, String message) {
		if (input == null) {
			throw new IllegalArgumentException(message);
		}
		return input;
	}

	public native Object allocateInstance(Class<?> cls);

	public native void throwException(Throwable exc);

	private native Object getObjectFieldValue(Object target, Field field);

	private native Integer getIntegerFieldValue(Object target, Field field);

	private native Long getLongFieldValue(Object target, Field field);

	private native Float getFloatFieldValue(Object target, Field field);

	private native Double getDoubleFieldValue(Object target, Field field);

	private native Boolean getBooleanFieldValue(Object target, Field field);

	private native Byte getByteFieldValue(Object target, Field field);

	private native Character getCharacterFieldValue(Object target, Field field);


	private native Object getStaticObjectFieldValue(Class<?> target, Field field);

	private native Integer getStaticIntegerFieldValue(Class<?> target, Field field);

	private native Long getStaticLongFieldValue(Class<?> target, Field field);

	private native Float getStaticFloatFieldValue(Class<?> target, Field field);

	private native Double getStaticDoubleFieldValue(Class<?> target, Field field);

	private native Boolean getStaticBooleanFieldValue(Class<?> target, Field field);

	private native Byte getStaticByteFieldValue(Class<?> target, Field field);

	private native Character getStaticCharacterFieldValue(Class<?> target, Field field);


	private native void setObjectFieldValue(Object target, Field field, Object value);

	private native void setIntegerFieldValue(Object target, Field field, Integer value);

	private native void setLongFieldValue(Object target, Field field, Long value);

	private native void setFloatFieldValue(Object target, Field field, Float value);

	private native void setDoubleFieldValue(Object target, Field field, Double value);

	private native void setBooleanFieldValue(Object target, Field field, Boolean value);

	private native void setByteFieldValue(Object target, Field field, Byte value);

	private native void setCharacterFieldValue(Object target, Field field, Character value);


	private native void setStaticObjectFieldValue(Class<?> target, Field field, Object value);

	private native void setStaticIntegerFieldValue(Class<?> target, Field field, Integer value);

	private native void setStaticLongFieldValue(Class<?> target, Field field, Long value);

	private native void setStaticFloatFieldValue(Class<?> target, Field field, Float value);

	private native void setStaticDoubleFieldValue(Class<?> target, Field field, Double value);

	private native void setStaticBooleanFieldValue(Class<?> target, Field field, Boolean value);

	private native void setStaticByteFieldValue(Class<?> target, Field field, Byte value);

	private native void setStaticCharacterFieldValue(Class<?> target, Field field, Character value);

	private native Field getDeclaredField(Class<?> target, String name, String signature, boolean isStatic);

}

