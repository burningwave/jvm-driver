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


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;

import org.burningwave.jvm.function.template.BiFunction;
import org.burningwave.jvm.util.ObjectProvider;


public interface GetPackageFunction extends BiFunction<ClassLoader, String, Package> {
	
	
	public static class ForJava7 implements GetPackageFunction{

		public ForJava7(Map<Object, Object> context) {}

		@Override
		public Package apply(ClassLoader inputOne, String packageName) {
			return Package.getPackage(packageName);
		}
		
	}
	
	public static class ForJava9 implements GetPackageFunction{
		MethodHandle methodHandle;
		ThrowExceptionFunction throwExceptionFunction;
		
		public ForJava9(Map<Object, Object> context) throws NoSuchMethodException, IllegalAccessException {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			ConsulterSupplyFunction<?> consulterSupplyFunction = functionProvider.getOrBuildObject(ConsulterSupplyFunction.class, context);
			MethodHandles.Lookup classLoaderConsulter =  consulterSupplyFunction.apply(ClassLoader.class);
			MethodType methodType = MethodType.methodType(Package.class, String.class);
			methodHandle = classLoaderConsulter.findSpecial(ClassLoader.class, "getDefinedPackage", methodType, ClassLoader.class);
			throwExceptionFunction =
				functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context); 
		}

		@Override
		public Package apply(ClassLoader classLoader, String packageName) {
			try {
				return (Package)methodHandle.invokeExact(classLoader, packageName);
			} catch (Throwable exc) {
				return throwExceptionFunction.apply(exc);
			}
		}
		
	}
	
	
}