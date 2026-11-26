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
package org.burningwave.jni;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;



/**
 * The JNI engine of Burningwave: it finds declared fields, reads and writes the values of fields, allocates
 * instances without calling their constructors and throws exceptions without declaring them, ignoring the access
 * modifiers of the fields and the encapsulation of the modules.
 * <p>
 * The class loads the native library of the current platform, which is in its jar, when it is initialized, unless a
 * library is chosen first with {@link Initializer#setNativeLibraryNameAndInit(String)}.
 */
@SuppressWarnings("all")
public class Executor {
	private final static Executor INSTANCE;

	private Executor() {}

	static {
		try {
			if (Initializer.nativeLibraryName == null) {
				Initializer.setNativeLibraryName(Libraries.getInstance().loadFor(Executor.class));
			} else {
				System.loadLibrary(Initializer.nativeLibraryName);
			}
		} catch (Throwable exc) {
			throw new ExceptionInInitializerError(exc);
		}
		INSTANCE = new Executor();
	}

	/**
	 * Returns the single instance of this class, whose native library was loaded when the class was initialized.
	 *
	 * @return the instance of the engine
	 */
	public final static Executor getInstance() {
		return INSTANCE;
	}

	/**
	 * Returns the value of a field of any type, whatever its access modifiers.
	 * <p>
	 * If the field is static, {@code target} is ignored and the value is read from the class that declares the field.
	 * The value of a primitive field is returned in its wrapper class.
	 *
	 * @param target the object whose field is read, ignored if the field is static
	 * @param field the field to read
	 * @return the value of the field, in its wrapper class if the field is primitive
	 * @throws IllegalArgumentException if {@code field} is {@code null}, or if the field is not static and
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
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
 			if (!isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
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


	/**
	 * Sets the value of a field of any type, whatever its access modifiers.
	 * <p>
	 * If the field is static, {@code origTarget} is ignored and the value is written in the class that declares the
	 * field. The value of a primitive field must be passed in its wrapper class.
	 *
	 * @param origTarget the object whose field is written, ignored if the field is static
	 * @param field the field to write
	 * @param value the new value, in its wrapper class if the field is primitive; it can be {@code null} only if the
	 * type of the field is not primitive
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if {@code value} is not assignable to the type
	 * of the field, or if the field is not static and {@code origTarget} is {@code null} or is not an instance of the
	 * class that declares the field
	 * @throws NullPointerException if {@code value} is {@code null} and the type of the field is primitive
	 */
	public void setFieldValue(Object origTarget, Field field, Object value) {
		check(field, "Field is null");
		if (value != null && !isAssignableFrom(field.getType(), value.getClass())) {
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
 			if (!isAssignableFrom(fieldDeclaringClass, targetObjectClass)) {
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

	/**
	 * Finds a non-static field by name and JNI type signature, whatever its access modifiers.
	 * <p>
	 * The field is looked up as the JNI function {@code GetFieldID} does, so it can also be inherited from a superclass
	 * of {@code target}, and the lookup initializes {@code target} if it is not initialized yet.
	 *
	 * @param target the class in which the field is looked up
	 * @param name the name of the field
	 * @param signature the JNI type signature of the field, for example {@code I} for {@code int} or
	 * {@code Ljava/lang/String;} for {@link String}
	 * @return the field
	 * @throws IllegalArgumentException if one of the parameters is {@code null}
	 * @throws NoSuchFieldError if no non-static field with that name and signature is found
	 */
	public Field getDeclaredField(Class<?> target, String name, String signature) {
		return getDeclaredField0(
			check(target, "Target class is null"),
			check(name, "Field name is null"),
			check(signature, "Field signature is null")
		);
	}

	/**
	 * Finds a static field by name and JNI type signature, whatever its access modifiers.
	 * <p>
	 * The field is looked up as the JNI function {@code GetStaticFieldID} does, so it can also be inherited from a
	 * superclass or an interface of {@code target}, and the lookup initializes {@code target} if it is not initialized
	 * yet.
	 *
	 * @param target the class in which the field is looked up
	 * @param name the name of the field
	 * @param signature the JNI type signature of the field, for example {@code I} for {@code int} or
	 * {@code Ljava/lang/String;} for {@link String}
	 * @return the field
	 * @throws IllegalArgumentException if one of the parameters is {@code null}
	 * @throws NoSuchFieldError if no static field with that name and signature is found
	 */
	public Field getDeclaredStaticField(Class<?> target, String name, String signature) {
		return getDeclaredStaticField0(
			check(target, "Target class is null"),
			check(name, "Field name is null"),
			check(signature, "Field signature is null")
		);
	}

	private static <T> T check(T input, String message) {
		if (input == null) {
			throw new IllegalArgumentException(message);
		}
		return input;
	}

	private static boolean isAssignableFrom(Class<?> type, Class<?> otherType) {
		return getClassOrWrapper(type).isAssignableFrom(getClassOrWrapper(otherType));
	}

	private static Class<?> getClassOrWrapper(Class<?> cls) {
		if (cls == short.class) {
			return Short.class;
		} else if (cls == int.class) {
			return Integer.class;
		} else if (cls == long.class) {
			return Long.class;
		} else if (cls == float.class) {
			return Float.class;
		} else if (cls == double.class) {
			return Double.class;
		} else if (cls == boolean.class) {
			return Boolean.class;
		} else if (cls == byte.class) {
			return Byte.class;
		} else if (cls == char.class) {
			return Character.class;
		}
		return cls;
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

	/**
	 * Returns the value of a non-static field of a reference type, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of a reference type
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is primitive, or if {@code target}
	 * is {@code null} or is not an instance of the class that declares the field
	 */
	public Object getObjectFieldValue(Object target, Field field) {
		checkGet(Object.class, target, field);
		return getObjectFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code short}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code short}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code short}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public short getShortFieldValue(Object target, Field field) {
		checkGet(short.class, target, field);
		return getShortFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code int}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code int}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code int}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public int getIntFieldValue(Object target, Field field) {
		checkGet(int.class, target, field);
		return getIntegerFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code long}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code long}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code long}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public long getLongFieldValue(Object target, Field field) {
		checkGet(long.class, target, field);
		return getLongFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code float}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code float}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code float}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public float getFloatFieldValue(Object target, Field field) {
		checkGet(float.class, target, field);
		return getFloatFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code double}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code double}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code double}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public double getDoubleFieldValue(Object target, Field field) {
		checkGet(double.class, target, field);
		return getDoubleFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code boolean}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code boolean}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code boolean}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public boolean getBooleanFieldValue(Object target, Field field) {
		checkGet(boolean.class, target, field);
		return getBooleanFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code byte}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code byte}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code byte}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public byte getByteFieldValue(Object target, Field field) {
		checkGet(byte.class, target, field);
		return getByteFieldValue0(target, field);
	}

	/**
	 * Returns the value of a non-static field of type {@code char}, whatever its access modifiers.
	 *
	 * @param target the object whose field is read
	 * @param field a non-static field of type {@code char}
	 * @return the value of the field
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code char}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public char getCharFieldValue(Object target, Field field) {
		checkGet(char.class, target, field);
		return getCharacterFieldValue0(target, field);
	}


	private static void checkStaticGet(Class<?> returnType, Field field) {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field " + field + " is not static");
		}
		checkGet0(returnType, field);
	}

	/**
	 * Returns the value of a static field of a reference type, whatever its access modifiers.
	 *
	 * @param field a static field of a reference type
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is primitive
	 */
	public Object getStaticObjectFieldValue(Field field) {
		checkStaticGet(Object.class, field);
		return getStaticObjectFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code short}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code short}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code short}
	 */
	public short getStaticShortFieldValue(Field field) {
		checkStaticGet(short.class, field);
		return getStaticShortFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code int}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code int}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code int}
	 */
	public int getStaticIntFieldValue(Field field) {
		checkStaticGet(int.class, field);
		return getStaticIntegerFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code long}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code long}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code long}
	 */
	public long getStaticLongFieldValue(Field field) {
		checkStaticGet(long.class, field);
		return getStaticLongFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code float}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code float}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code float}
	 */
	public float getStaticFloatFieldValue(Field field) {
		checkStaticGet(float.class, field);
		return getStaticFloatFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code double}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code double}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code double}
	 */
	public double getStaticDoubleFieldValue(Field field) {
		checkStaticGet(double.class, field);
		return getStaticDoubleFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code boolean}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code boolean}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code boolean}
	 */
	public boolean getStaticBooleanFieldValue(Field field) {
		checkStaticGet(boolean.class, field);
		return getStaticBooleanFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code byte}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code byte}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code byte}
	 */
	public byte getStaticByteFieldValue(Field field) {
		checkStaticGet(byte.class, field);
		return getStaticByteFieldValue0(field.getDeclaringClass(), field);
	}

	/**
	 * Returns the value of a static field of type {@code char}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code char}
	 * @return the value of the field
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code char}
	 */
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
		checkSet0(valueType, field);
		checkTargetAndField(target, field);
	}

	/**
	 * Sets the value of a non-static field of a reference type, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of a reference type
	 * @param value the new value, {@code null} or an object assignable to the type of the field
	 * @throws IllegalArgumentException if the type of the field is primitive, if {@code value} is not assignable to it,
	 * or if {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
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

	/**
	 * Sets the value of a non-static field of type {@code short}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code short}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code short}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setShortFieldValue(Object target, Field field, short value) {
		checkSet(short.class, target, field);
		setShortFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code int}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code int}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code int}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setIntFieldValue(Object target, Field field, int value) {
		checkSet(int.class, target, field);
		setIntegerFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code long}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code long}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code long}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setLongFieldValue(Object target, Field field, long value) {
		checkSet(long.class, target, field);
		setLongFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code float}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code float}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code float}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setFloatFieldValue(Object target, Field field, float value) {
		checkSet(float.class, target, field);
		setFloatFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code double}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code double}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code double}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setDoubleFieldValue(Object target, Field field, double value) {
		checkSet(double.class, target, field);
		setDoubleFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code boolean}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code boolean}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code boolean}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setBooleanFieldValue(Object target, Field field, boolean value) {
		checkSet(boolean.class, target, field);
		setBooleanFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code byte}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code byte}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code byte}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setByteFieldValue(Object target, Field field, byte value) {
		checkSet(byte.class, target, field);
		setByteFieldValue0(target, field, value);
	}

	/**
	 * Sets the value of a non-static field of type {@code char}, whatever its access modifiers.
	 *
	 * @param target the object whose field is written
	 * @param field a non-static field of type {@code char}
	 * @param value the new value
	 * @throws IllegalArgumentException if {@code field} is {@code null}, if its type is not {@code char}, or if
	 * {@code target} is {@code null} or is not an instance of the class that declares the field
	 */
	public void setCharFieldValue(Object target, Field field, char value) {
		checkSet(char.class, target, field);
		setCharacterFieldValue0(target, field, value);
	}


	private static void checkStaticSet(Class<?> valueType, Field field) {
		if (!Modifier.isStatic(field.getModifiers())) {
			throw new IllegalArgumentException("Field " + field + " is not static");
		}
		checkSet0(valueType, field);
	}

	/**
	 * Sets the value of a static field of a reference type, whatever its access modifiers.
	 *
	 * @param field a static field of a reference type
	 * @param value the new value, {@code null} or an object assignable to the type of the field
	 * @throws IllegalArgumentException if the type of the field is primitive, if {@code value} is not assignable to it
	 * or if the field is not static
	 */
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

	/**
	 * Sets the value of a static field of type {@code short}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code short}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code short}
	 */
	public void setStaticShortFieldValue(Field field, short value) {
		checkStaticSet(short.class, field);
		setStaticShortFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code int}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code int}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code int}
	 */
	public void setStaticIntFieldValue(Field field, int value) {
		checkStaticSet(int.class, field);
		setStaticIntegerFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code long}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code long}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code long}
	 */
	public void setStaticLongFieldValue(Field field, long value) {
		checkStaticSet(long.class, field);
		setStaticLongFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code float}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code float}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code float}
	 */
	public void setStaticFloatFieldValue(Field field, float value) {
		checkStaticSet(float.class, field);
		setStaticFloatFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code double}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code double}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code double}
	 */
	public void setStaticDoubleFieldValue(Field field, double value) {
		checkStaticSet(double.class, field);
		setStaticDoubleFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code boolean}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code boolean}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code boolean}
	 */
	public void setStaticBooleanFieldValue(Field field, boolean value) {
		checkStaticSet(boolean.class, field);
		setStaticBooleanFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code byte}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code byte}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code byte}
	 */
	public void setStaticByteFieldValue(Field field, byte value) {
		checkStaticSet(byte.class, field);
		setStaticByteFieldValue0(field.getDeclaringClass(), field, value);
	}

	/**
	 * Sets the value of a static field of type {@code char}, whatever its access modifiers.
	 *
	 * @param field a static field of type {@code char}
	 * @param value the new value
	 * @throws IllegalArgumentException if the field is not static or if its type is not {@code char}
	 */
	public void setStaticCharFieldValue(Field field, char value) {
		checkStaticSet(char.class, field);
		setStaticCharacterFieldValue0(field.getDeclaringClass(), field, value);
	}


	/**
	 * Creates an instance of a class without calling any of its constructors: its fields keep their default values.
	 * <p>
	 * If {@code cls} is an interface or an abstract class, the method throws {@link InstantiationException}, although
	 * it does not declare it.
	 *
	 * @param cls the class to instantiate, which cannot be an interface, an abstract class or an array class
	 * @return the new instance
	 */
	public native Object allocateInstance(Class<?> cls);

	/**
	 * Throws an exception or an error, even a checked exception that the calling method does not declare: the method
	 * never returns normally.
	 *
	 * @param exc the exception or the error to throw, not {@code null}
	 */
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

	private native Field getDeclaredField0(Class<?> target, String name, String signature);

	private native Field getDeclaredStaticField0(Class<?> target, String name, String signature);


	/**
	 * Chooses the native library of {@link Executor} before the class is initialized, instead of the library of the
	 * current platform that the jar contains.
	 */
	public static class Initializer {

		private static String nativeLibraryName;

		private static void setNativeLibraryName(String name) {
			if (nativeLibraryName != null) {
				throw new IllegalStateException("Executor is already initialized");
			}
			nativeLibraryName = name;
		}

		/**
		 * Initializes {@link Executor} with the native library that has the given name and returns its instance.
		 * <p>
		 * The library is loaded with {@link System#loadLibrary(String)}, so it must be in {@code java.library.path}.
		 * The method must be called before {@link Executor} is initialized, which happens the first time the class is
		 * used.
		 *
		 * @param name the name of the native library, in the form expected by {@link System#loadLibrary(String)}
		 * @return the instance of {@link Executor}
		 * @throws IllegalStateException if {@link Executor} is already initialized
		 */
		public static Executor setNativeLibraryNameAndInit(String name) {
			setNativeLibraryName(name);
			return Executor.getInstance();
		}
	}
}

