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


import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;


/**
 * Retrieves the fields of {@link Target} with the standard reflection API only, so that loading this class never
 * loads the native library of an engine.
 */
final class Fields {

	private Fields() {}

	/**
	 * Returns the field of {@link Target} with the given name, after calling {@code setAccessible(true)} on it.
	 */
	static Field accessible(String name) {
		try {
			Field field = Target.class.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (NoSuchFieldException exc) {
			throw new IllegalStateException(exc);
		}
	}

	/**
	 * Returns a {@link VarHandle} for the given field of {@link Target}, static or not.
	 */
	static VarHandle varHandle(Field field) {
		try {
			return MethodHandles.privateLookupIn(Target.class, MethodHandles.lookup()).unreflectVarHandle(field);
		} catch (IllegalAccessException exc) {
			throw new IllegalStateException(exc);
		}
	}

}
