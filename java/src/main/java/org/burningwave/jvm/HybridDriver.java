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
package org.burningwave.jvm;


import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class HybridDriver extends DefaultDriver {

	@Override
	Initializer newInitializerForJava17() {
		return new ForJava17(this);
	}

	static class ForJava17 extends DefaultDriver.Initializer.ForJava17 {
		private DriverFunctionSupplierNative driverFunctionSupplierNative;

		ForJava17(DefaultDriver driver) {
			super(driver);
			driverFunctionSupplierNative = new DriverFunctionSupplierNative();
		}

		@Override
		void initNativeFunctionSupplier() {
			DriverFunctionSupplierNative driverFunctionSupplierNative = new DriverFunctionSupplierNative();
			this.driverFunctionSupplier = new DriverFunctionSupplierUnsafe.ForJava17(this.driver) {

				@Override
				Supplier<MethodHandles.Lookup> getMethodHandlesLookupSupplyingFunction() {
					return driverFunctionSupplierNative.getMethodHandlesLookupSupplyingFunction();
				}

				@Override
				BiFunction<Object, Field, Object> getFieldValueFunction() {
					return driverFunctionSupplierNative.getFieldValueFunction();
				}

				@Override
				Function<Object, BiConsumer<Field, Object>> getSetFieldValueFunction() {
					return driverFunctionSupplierNative.getSetFieldValueFunction();
				}

				@Override
				Function<Class<?>, Object> getAllocateInstanceFunction() {
					return driverFunctionSupplierNative.getAllocateInstanceFunction();
				}

			};
		}

		@Override
		void initAccessibleSetter() {
			driver.accessibleSetter = driverFunctionSupplierNative.getSetAccessibleFunction();
		}

		@Override
		public void close() {
			driverFunctionSupplierNative = null;
			super.close();
		}

	}

}