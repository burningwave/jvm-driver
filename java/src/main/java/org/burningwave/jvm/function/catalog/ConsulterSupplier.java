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


import java.lang.invoke.MethodHandles;
import java.util.Map;


public abstract class ConsulterSupplier extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplier {
	
	public static class Hybrid extends	ConsulterSupplier {
		
		public static class ForJava17 extends Hybrid {
			
			public ForJava17(Map<Object, Object> context) {
				consulter = MethodHandles.lookup();
				org.burningwave.jvm.NativeExecutor.getInstance().setAllowedModes(consulter, -1);
			}
			
		}
	}
	
	static class Native extends	ConsulterSupplier {
		
		static class ForJava7 extends Native {
			
			ForJava7(Map<Object, Object> context) {
				consulter = MethodHandles.lookup();
				org.burningwave.jvm.NativeExecutor.getInstance().setAllowedModes(consulter, -1);
			}
			
		}
	}
	
}
