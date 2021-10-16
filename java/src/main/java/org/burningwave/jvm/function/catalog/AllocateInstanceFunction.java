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


import java.util.Map;


public interface AllocateInstanceFunction extends io.github.toolfactory.jvm.function.catalog.AllocateInstanceFunction {
	
	public static interface Native extends AllocateInstanceFunction {
		
		public static class ForJava7 implements Native {
			org.burningwave.jvm.NativeExecutor nativeExecutor;
			
			public ForJava7(Map<Object, Object> context) {
				nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();
			}
			
			@Override
			public Object apply(Class<?> cls) {
				return nativeExecutor.allocateInstance(cls);
			}
			
		}
	}
	
}
