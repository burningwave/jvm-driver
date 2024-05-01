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
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Map;

import io.github.toolfactory.jvm.function.template.ThrowingBiConsumer;


public interface SetAccessibleFunction extends io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction {

	public interface Native extends SetAccessibleFunction {

		public static class ForJava7 extends io.github.toolfactory.jvm.function.catalog.SetAccessibleFunction.Abst<ThrowingBiConsumer<AccessibleObject, Boolean, Throwable>> {
			public ForJava7(Map<Object, Object> context) {
				super(context);
				final org.burningwave.jvm.NativeExecutor nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();
				final Field overrideField = nativeExecutor.getDeclaredField(AccessibleObject.class, "override", "Z");
				setFunction(new ThrowingBiConsumer<AccessibleObject, Boolean, Throwable>() {
					@Override
					public void accept(AccessibleObject accessibleObject, Boolean flag) {
						nativeExecutor.setFieldValue(accessibleObject, overrideField, flag);
					}
				});


			}

			@Override
			public void accept(AccessibleObject accessibleObject, Boolean flag) throws Throwable {
				function.accept(accessibleObject, flag);
			}
		}
	}

}
