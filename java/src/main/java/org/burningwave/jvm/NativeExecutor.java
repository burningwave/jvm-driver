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
 * Copyright (c) 2019-2022 Roberto Gentili
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
		check(field, "Field is null");
		Class<?> fieldType = field.getType();
		if (Modifier.isStatic(field.getModifiers())) {
			target = field.getDeclaringClass();
			if (!fieldType.isPrimitive()) {
				return getStaticObjectFieldValue0((Class<?>)target, field);
			} else if (fieldType == short.class) {
				return getStaticShortFieldValue0((Class<?>)target, field);
			} else if (fieldType == int.class) {
				return getStaticIntegerFieldValue0((Class<?>)target, field);
			} else if (fieldType == long.class) {
				return getStaticLongFieldValue0((Class<?>)target, field);
			} else if (fieldType == float.class) {
				return getStaticFloatFieldValue0((Class<?>)target, field);
			} else if (fieldType == double.class) {
				return getStaticDoubleFieldValue0((Class<?>)target, field);
			} else if (fieldType == boolean.class) {
				return getStaticBooleanFieldValue0((Class<?>)target, field);
			} else if (fieldType == byte.class) {
				return getStaticByteFieldValue0((Class<?>)target, field);
			} else {
				return getStaticCharacterFieldValue0((Class<?>)target, field);
			}
		} else {
			check(target, "Target object is null");
			Class<?> fieldDeclaringClass = field.getDeclaringClass();
			Class<?> targetObjectClass = target.getClass();
 			if (!Classes.isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
				throw new IllegalArgumentException("Target object class " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
			}
			if (!fieldType.isPrimitive()) {
				return getObjectFieldValue0(target, field);
			} else if (fieldType == short.class) {
				return getShortFieldValue0(target, field);
			} else if (fieldType == int.class) {
				return getIntegerFieldValue0(target, field);
			} else if (fieldType == long.class) {
				return getLongFieldValue0(target, field);
			} else if (fieldType == float.class) {
				return getFloatFieldValue0(target, field);
			} else if (fieldType == double.class) {
				return getDoubleFieldValue0(target, field);
			} else if (fieldType == boolean.class) {
				return getBooleanFieldValue0(target, field);
			} else if (fieldType == byte.class) {
				return getByteFieldValue0(target, field);
			} else {
				return getCharacterFieldValue0(target, field);
			}
		}
	}


	public void setFieldValue(Object origTarget, Field field, Object value) {
		check(field, "Field is null");
		if (value != null && !Classes.isAssignableFrom(field.getType(), value.getClass())) {
			throw new IllegalArgumentException("Value " + value + " is not assignable to " + field.getName());
		}
		Class<?> fieldType = field.getType();
		Object target = Modifier.isStatic(field.getModifiers())?
			field.getDeclaringClass() :
			origTarget;
		if (Modifier.isStatic(field.getModifiers())) {
			target = field.getDeclaringClass();
			if (!fieldType.isPrimitive()) {
				setStaticObjectFieldValue0((Class<?>)target, field, value);
			} else if (fieldType == short.class) {
				setStaticShortFieldValue0((Class<?>)target, field, (Short)value);
			} else if (fieldType == int.class) {
				setStaticIntegerFieldValue0((Class<?>)target, field, (Integer)value);
			} else if (fieldType == long.class) {
				setStaticLongFieldValue0((Class<?>)target, field, (Long)value);
			} else if (fieldType == float.class) {
				setStaticFloatFieldValue0((Class<?>)target, field, (Float)value);
			} else if (fieldType == double.class) {
				setStaticDoubleFieldValue0((Class<?>)target, field, (Double)value);
			} else if (fieldType == boolean.class) {
				setStaticBooleanFieldValue0((Class<?>)target, field, (Boolean)value);
			} else if (fieldType == byte.class) {
				setStaticByteFieldValue0((Class<?>)target, field, (Byte)value);
			} else {
				setStaticCharacterFieldValue0((Class<?>)target, field, (Character)value);
			}
		} else {
			check(target, "Target object is null");
			Class<?> fieldDeclaringClass = field.getDeclaringClass();
			Class<?> targetObjectClass = target.getClass();
 			if (!Classes.isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
				throw new IllegalArgumentException("Target object class " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
			}
			if (!fieldType.isPrimitive()) {
				setObjectFieldValue0(target, field, value);
			} else if (fieldType == short.class) {
				setShortFieldValue0(target, field, (Short)value);
			} else if (fieldType == int.class) {
				setIntegerFieldValue0(target, field, (Integer)value);
			} else if (fieldType == long.class) {
				setLongFieldValue0(target, field, (Long)value);
			} else if (fieldType == float.class) {
				setFloatFieldValue0(target, field, (Float)value);
			} else if (fieldType == double.class) {
				setDoubleFieldValue0(target, field, (Double)value);
			} else if (fieldType == boolean.class) {
				setBooleanFieldValue0(target, field, (Boolean)value);
			} else if (fieldType == byte.class) {
				setByteFieldValue0(target, field, (Byte)value);
			} else {
				setCharacterFieldValue0(target, field, (Character)value);
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

	private static <T> T check(T input, String message) {
		if (input == null) {
			throw new IllegalArgumentException(message);
		}
		return input;
	}
	
	private static void checkGet0(Class<?> returnType, Field field) {
		check(field, "Field is null");
		Class<?> fieldType = field.getType();
		if (!returnType.isAssignableFrom(fieldType)) {
			throw new IllegalArgumentException("Return type " + returnType + " is not assignable from " + fieldType);
		}
	}

	private static void checkTargetAndField(Object target, Field field) {
		check(target, "Target object is null");
		Class<?> fieldDeclaringClass = field.getDeclaringClass();
		Class<?> targetObjectClass = target.getClass();
		if (!fieldDeclaringClass.isAssignableFrom(targetObjectClass)) {
			throw new IllegalArgumentException("Target " + targetObjectClass + " is not assignable to " + fieldDeclaringClass);
		}
	}
	
	private static void checkGet(Class<?> returnType, Object target, Field field) {
		checkGet0(returnType, field);
		checkTargetAndField(target, field);
	}

	public Object getObjectFieldValue(Object target, Field field) {
		checkGet(Object.class, target, field);
		return getObjectFieldValue0(target, field);
	}

	public short getShortFieldValue(Object target, Field field) {
		checkGet(short.class, target, field);
		return getShortFieldValue0(target, field);
	}

	public int getIntFieldValue(Object target, Field field) {
		checkGet(int.class, target, field);
		return getIntegerFieldValue0(target, field);
	}

	public long getLongFieldValue(Object target, Field field) {
		checkGet(long.class, target, field);
		return getLongFieldValue0(target, field);
	}

	public float getFloatFieldValue(Object target, Field field) {
		checkGet(float.class, target, field);
		return getFloatFieldValue0(target, field);
	}

	public double getDoubleFieldValue(Object target, Field field) {
		checkGet(double.class, target, field);
		return getDoubleFieldValue0(target, field);
	}

	public boolean getBooleanFieldValue(Object target, Field field) {
		checkGet(boolean.class, target, field);
		return getBooleanFieldValue0(target, field);
	}

	public byte getByteFieldValue(Object target, Field field) {
		checkGet(byte.class, target, field);
		return getByteFieldValue0(target, field);
	}

	public char getCharFieldValue(Object target, Field field) {
		checkGet(char.class, target, field);
		return getCharacterFieldValue0(target, field);
	}


	private static void checkStaticGet(Class<?> returnType, Field field) {
		if (Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field " + field + " is not static");
		}
		checkGet0(returnType, field);
	}

	public Object getStaticObjectFieldValue(Field field) {
		checkStaticGet(Object.class, field);
		return getStaticObjectFieldValue0(field.getDeclaringClass(), field);
	}

	public short getStaticShortFieldValue(Field field) {
		checkStaticGet(short.class, field);
		return getStaticShortFieldValue0(field.getDeclaringClass(), field);
	}

	public int getStaticIntFieldValue(Field field) {
		checkStaticGet(int.class, field);
		return getStaticIntegerFieldValue0(field.getDeclaringClass(), field);
	}

	public long getStaticLongFieldValue(Field field) {
		checkStaticGet(long.class, field);
		return getStaticLongFieldValue0(field.getDeclaringClass(), field);
	}

	public float getStaticFloatFieldValue(Field field) {
		checkStaticGet(float.class, field);
		return getStaticFloatFieldValue0(field.getDeclaringClass(), field);
	}

	public double getStaticDoubleFieldValue(Field field) {
		checkStaticGet(double.class, field);
		return getStaticDoubleFieldValue0(field.getDeclaringClass(), field);
	}

	public boolean getStaticBooleanFieldValue(Field field) {
		checkStaticGet(boolean.class, field);
		return getStaticBooleanFieldValue0(field.getDeclaringClass(), field);
	}

	public byte getStaticByteFieldValue(Field field) {
		checkStaticGet(byte.class, field);
		return getStaticByteFieldValue0(field.getDeclaringClass(), field);
	}

	public char getStaticCharFieldValue(Field field) {
		checkStaticGet(char.class, field);
		return getStaticCharacterFieldValue0(field.getDeclaringClass(), field);
	}
	
	
	private static void checkSet0(Class<?> valueType, Field field) {
		check(field, "Field is null");
		Class<?> fieldType = field.getType();
		if (!fieldType.isAssignableFrom(valueType)) {
			throw new IllegalArgumentException("Field type " + fieldType + " is not assignable from " + valueType);
		}
	}
	
	private static void checkSet(Class<?> valueType, Object target, Field field) {
		checkGet0(valueType, field);
		checkTargetAndField(target, field);
	}

	public void setObjectFieldValue(Object target, Field field, Object value) {
		if (null == value) {
			if (field.getType().isPrimitive()) {
				throw new IllegalArgumentException("Field type " + field.getType() + " is not assignable from " + Object.class);
			}
			checkTargetAndField(target, field);
		} else {
			checkSet(value.getClass(), target, field);
		}

		setObjectFieldValue0(target, field, value);
	}

	public void setShortFieldValue(Object target, Field field, short value) {
		checkSet(short.class, target, field);
		setShortFieldValue0(target, field, value);
	}

	public void setIntFieldValue(Object target, Field field, int value) {
		checkSet(int.class, target, field);
		setIntegerFieldValue0(target, field, value);
	}

	public void setLongFieldValue(Object target, Field field, long value) {
		checkSet(long.class, target, field);
		setLongFieldValue0(target, field, value);
	}

	public void setFloatFieldValue(Object target, Field field, float value) {
		checkSet(float.class, target, field);
		setFloatFieldValue0(target, field, value);
	}

	public void setDoubleFieldValue(Object target, Field field, double value) {
		checkSet(double.class, target, field);
		setDoubleFieldValue0(target, field, value);
	}

	public void setBooleanFieldValue(Object target, Field field, boolean value) {
		checkSet(boolean.class, target, field);
		setBooleanFieldValue0(target, field, value);
	}

	public void setByteFieldValue(Object target, Field field, byte value) {
		checkSet(byte.class, target, field);
		setByteFieldValue0(target, field, value);
	}

	public void setCharFieldValue(Object target, Field field, char value) {
		checkSet(char.class, target, field);
		setCharacterFieldValue0(target, field, value);
	}

	
	private static void checkStaticSet(Class<?> valueType, Field field) {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field " + field + " is not static");
		}
		checkGet0(valueType, field);
	}

	public void setStaticObjectFieldValue(Field field, Object value) {
		if (null == value) {
			if (field.getType().isPrimitive()) {
				throw new IllegalArgumentException("Field type " + field.getType() + " is not assignable from " + Object.class);
			}
		} else {
			checkStaticSet(value.getClass(), field);
		}

		setStaticObjectFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticShortFieldValue(Field field, short value) {
		checkStaticSet(short.class, field);
		setStaticShortFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticIntFieldValue(Field field, int value) {
		checkStaticSet(int.class, field);
		setStaticIntegerFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticLongFieldValue(Field field, long value) {
		checkStaticSet(long.class, field);
		setStaticLongFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticFloatFieldValue(Field field, float value) {
		checkStaticSet(float.class, field);
		setStaticFloatFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticDoubleFieldValue(Field field, double value) {
		checkStaticSet(double.class, field);
		setStaticDoubleFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticBooleanFieldValue(Field field, boolean value) {
		checkStaticSet(boolean.class, field);
		setStaticBooleanFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticByteFieldValue(Field field, byte value) {
		checkStaticSet(byte.class, field);
		setStaticByteFieldValue0(field.getDeclaringClass(), field, value);
	}

	public void setStaticCharFieldValue(Field field, char value) {
		checkStaticSet(char.class, field);
		setStaticCharacterFieldValue0(field.getDeclaringClass(), field, value);
	}


	public native Object allocateInstance(Class<?> cls);

	public native void throwException(Throwable exc);

	private native Object getObjectFieldValue0(Object target, Field field);

	private native short getShortFieldValue0(Object target, Field field);

	private native int getIntegerFieldValue0(Object target, Field field);

	private native long getLongFieldValue0(Object target, Field field);

	private native float getFloatFieldValue0(Object target, Field field);

	private native double getDoubleFieldValue0(Object target, Field field);

	private native boolean getBooleanFieldValue0(Object target, Field field);

	private native byte getByteFieldValue0(Object target, Field field);

	private native char getCharacterFieldValue0(Object target, Field field);


	private native Object getStaticObjectFieldValue0(Class<?> target, Field field);

	private native short getStaticShortFieldValue0(Class<?> target, Field field);

	private native int getStaticIntegerFieldValue0(Class<?> target, Field field);

	private native long getStaticLongFieldValue0(Class<?> target, Field field);

	private native float getStaticFloatFieldValue0(Class<?> target, Field field);

	private native double getStaticDoubleFieldValue0(Class<?> target, Field field);

	private native boolean getStaticBooleanFieldValue0(Class<?> target, Field field);

	private native byte getStaticByteFieldValue0(Class<?> target, Field field);

	private native char getStaticCharacterFieldValue0(Class<?> target, Field field);


	private native void setObjectFieldValue0(Object target, Field field, Object value);

	private native void setShortFieldValue0(Object target, Field field, short value);

	private native void setIntegerFieldValue0(Object target, Field field, int value);

	private native void setLongFieldValue0(Object target, Field field, long value);

	private native void setFloatFieldValue0(Object target, Field field, float value);

	private native void setDoubleFieldValue0(Object target, Field field, double value);

	private native void setBooleanFieldValue0(Object target, Field field, boolean value);

	private native void setByteFieldValue0(Object target, Field field, byte value);

	private native void setCharacterFieldValue0(Object target, Field field, char value);


	private native void setStaticObjectFieldValue0(Class<?> target, Field field, Object value);

	private native void setStaticShortFieldValue0(Class<?> target, Field field, short value);

	private native void setStaticIntegerFieldValue0(Class<?> target, Field field, int value);

	private native void setStaticLongFieldValue0(Class<?> target, Field field, long value);

	private native void setStaticFloatFieldValue0(Class<?> target, Field field, float value);

	private native void setStaticDoubleFieldValue0(Class<?> target, Field field, double value);

	private native void setStaticBooleanFieldValue0(Class<?> target, Field field, boolean value);

	private native void setStaticByteFieldValue0(Class<?> target, Field field, byte value);

	private native void setStaticCharacterFieldValue0(Class<?> target, Field field, char value);

	private native Field getDeclaredField(Class<?> target, String name, String signature, boolean isStatic);

}

