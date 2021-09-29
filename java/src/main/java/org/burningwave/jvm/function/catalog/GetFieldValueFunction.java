/*
 * This file is derived from ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 * 
 * Modified by: Roberto Gentili
 *
 * Modifications hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2021 Luke Hutchison, Roberto Gentili
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
package org.burningwave.jvm.function.catalog;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.burningwave.jvm.function.template.BiFunction;
import org.burningwave.jvm.util.ObjectProvider;


@SuppressWarnings("restriction")
public interface GetFieldValueFunction extends BiFunction<Object, Field, Object> {
	
	
	public static class ForJava7 implements GetFieldValueFunction {
		final sun.misc.Unsafe unsafe;
		
		public ForJava7(Map<Object, Object> context) {
			unsafe = ObjectProvider.get(context).getOrBuildObject(UnsafeSupplier.class, context).get();
		}

		@Override
		public Object apply(Object target, Field field) {
			target = Modifier.isStatic(field.getModifiers())?
				field.getDeclaringClass() :
				target;
			long fieldOffset = Modifier.isStatic(field.getModifiers())?
				unsafe.staticFieldOffset(field) :
				unsafe.objectFieldOffset(field);
			Class<?> cls = field.getType();
			if(!cls.isPrimitive()) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return unsafe.getObject(target, fieldOffset);
				} else {
					return unsafe.getObjectVolatile(target, fieldOffset);
				}
			} else if (cls == int.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Integer.valueOf(unsafe.getInt(target, fieldOffset));
				} else {
					return Integer.valueOf(unsafe.getIntVolatile(target, fieldOffset));
				}
			} else if (cls == long.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Long.valueOf(unsafe.getLong(target, fieldOffset));
				} else {
					return Long.valueOf(unsafe.getLongVolatile(target, fieldOffset));
				}
			} else if (cls == float.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Float.valueOf(unsafe.getFloat(target, fieldOffset));
				} else {
					return Float.valueOf(unsafe.getFloatVolatile(target, fieldOffset));
				}
			} else if (cls == double.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Double.valueOf(unsafe.getDouble(target, fieldOffset));
				} else {
					return Double.valueOf(unsafe.getDoubleVolatile(target, fieldOffset));
				}
			} else if (cls == boolean.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Boolean.valueOf(unsafe.getBoolean(target, fieldOffset));
				} else {
					return Boolean.valueOf(unsafe.getBooleanVolatile(target, fieldOffset));
				}
			} else if (cls == byte.class) {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Byte.valueOf(unsafe.getByte(target, fieldOffset));
				} else {
					return Byte.valueOf(unsafe.getByteVolatile(target, fieldOffset));
				}
			} else {
				if (!Modifier.isVolatile(field.getModifiers())) {
					return Character.valueOf(unsafe.getChar(target, fieldOffset));
				} else {
					return Character.valueOf(unsafe.getCharVolatile(target, fieldOffset));
				}
			}
		}
	}
	
	public static interface Native extends GetFieldValueFunction {
		
		public static class ForJava7 implements Native {
			org.burningwave.jvm.NativeExecutor nativeExecutor;
			
			public ForJava7(Map<Object, Object> context) {
				nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();				
			}

			@Override
			public Object apply(Object target, Field field) {
				Class<?> fieldType = field.getType();
				if (Modifier.isStatic(field.getModifiers())) {
					target = field.getDeclaringClass();
					if(!fieldType.isPrimitive()) {
						return nativeExecutor.getStaticFieldValue((Class<?>)target, field);
					} else if (fieldType == int.class) {
						return nativeExecutor.getStaticIntegerFieldValue((Class<?>)target, field);
					} else if (fieldType == long.class) {
						return nativeExecutor.getStaticLongFieldValue((Class<?>)target, field);
					} else if (fieldType == float.class) {
						return nativeExecutor.getStaticFloatFieldValue((Class<?>)target, field);
					} else if (fieldType == double.class) {
						return nativeExecutor.getStaticDoubleFieldValue((Class<?>)target, field);
					} else if (fieldType == boolean.class) {
						return nativeExecutor.getStaticBooleanFieldValue((Class<?>)target, field);
					} else if (fieldType == byte.class) {
						return nativeExecutor.getStaticByteFieldValue((Class<?>)target, field);
					} else {
						return nativeExecutor.getStaticCharacterFieldValue((Class<?>)target, field);
					}
				} else {
					if(!fieldType.isPrimitive()) {
						return nativeExecutor.getFieldValue(target, field);
					} else if (fieldType == int.class) {
						return nativeExecutor.getIntegerFieldValue(target, field);
					} else if (fieldType == long.class) {
						return nativeExecutor.getLongFieldValue(target, field);
					} else if (fieldType == float.class) {
						return nativeExecutor.getFloatFieldValue(target, field);
					} else if (fieldType == double.class) {
						return nativeExecutor.getDoubleFieldValue(target, field);
					} else if (fieldType == boolean.class) {
						return nativeExecutor.getBooleanFieldValue(target, field);
					} else if (fieldType == byte.class) {
						return nativeExecutor.getByteFieldValue(target, field);
					} else {
						return nativeExecutor.getCharacterFieldValue(target, field);
					}
				}
			}
		}
		
	}
}
