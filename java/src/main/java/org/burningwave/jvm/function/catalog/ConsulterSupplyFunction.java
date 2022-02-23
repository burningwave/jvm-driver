/*
 * This file is part of ToolFactory JVM driver.
 *
 * Hosted at: https://github.com/toolfactory/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2019-2022 Luke Hutchison, Roberto Gentili
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


import java.lang.invoke.MethodHandles;
import java.util.Map;


public interface ConsulterSupplyFunction extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction {

	public static interface Native extends ConsulterSupplyFunction {

		public static interface ForJava17 extends Native {

			public static class ForSemeru extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction.ForJava17.ForSemeru implements ForJava17 {
				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);

				}

				@Override
				protected void empowerMainConsulter(MethodHandles.Lookup consulter, Map<Object, Object> context) throws Throwable {
					org.burningwave.jvm.NativeExecutor nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();
					nativeExecutor.setFieldValue(
						consulter,
						nativeExecutor.getDeclaredField(MethodHandles.Lookup.class, "allowedModes", "I"),
						-1
					);
				}

			}

		}

	}

	public static interface Hybrid extends ConsulterSupplyFunction {

		public static interface ForJava17 extends Hybrid {

			public static class ForSemeru extends Native.ForJava17.ForSemeru implements Hybrid.ForJava17 {

				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);
				}
			}
		}
	}
}
