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


import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.burningwave.jvm.function.template.Supplier;
import org.burningwave.jvm.util.Classes;
import org.burningwave.jvm.util.ObjectProvider;
import org.burningwave.jvm.util.Resources;
import org.burningwave.jvm.util.Streams;


public interface ClassLoaderDelegateClassSupplier extends Supplier<Class<?>> {
	
	public static class ForJava7 implements ClassLoaderDelegateClassSupplier{
		
		public ForJava7(Map<Object, Object> context) {}
		
		@Override
		public Class<?> get() {
			return null;
		}
		
	}
	
	public static class ForJava9 implements ClassLoaderDelegateClassSupplier{
		Class<?> cls;
		
		public ForJava9(Map<Object, Object> context) throws ClassNotFoundException, IOException {
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), Classes.class.getPackage().getName().replace(".", "/") + "/ClassLoaderDelegateForJDK9.bwc"
				);
			) {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				cls = functionProvider.getOrBuildObject(
					DefineHookClassFunction.class, context
				).apply(
					functionProvider.getOrBuildObject(BuiltinClassLoaderClassSupplier.class, context).get(), 
					Streams.toByteArray(inputStream)
				);
			}
		}
		
		@Override
		public Class<?> get() {
			return cls;
		}
		
	}
	
}
