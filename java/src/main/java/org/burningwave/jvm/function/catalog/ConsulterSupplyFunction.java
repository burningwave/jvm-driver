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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;

import org.burningwave.jvm.function.template.Function;
import org.burningwave.jvm.util.FunctionAdapter;
import org.burningwave.jvm.util.ObjectProvider;
import org.burningwave.jvm.util.Resources;
import org.burningwave.jvm.util.Streams;


@SuppressWarnings("unchecked")
public abstract class ConsulterSupplyFunction<F> extends FunctionAdapter<F, Class<?>, MethodHandles.Lookup> {

	
	public static class ForJava7 extends ConsulterSupplyFunction<Function<Class<?>, MethodHandles.Lookup>> {
		public ForJava7(Map<Object, Object> context) {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			final MethodHandles.Lookup consulter = functionProvider.getOrBuildObject(ConsulterSupplier.class, context).get();
			final MethodHandle privateLookupInMethodHandle = functionProvider.getOrBuildObject(PrivateLookupInMethodHandleSupplier.class, context).get();
			final ThrowExceptionFunction throwExceptionFunction =
				functionProvider.getOrBuildObject(ThrowExceptionFunction.class, context); 
			setFunction(
				new Function<Class<?>, MethodHandles.Lookup>() { 
					@Override
					public MethodHandles.Lookup apply(Class<?> cls) {
						try {
							return (MethodHandles.Lookup) privateLookupInMethodHandle.invoke(consulter, cls);
						} catch (Throwable exc) {
							return throwExceptionFunction.apply(exc);
						}
					}
				}
			);
		}
		
		@Override
		public MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
	
	public static class ForJava9 extends ConsulterSupplyFunction<java.util.function.Function<Class<?>, MethodHandles.Lookup>> {
		
		public ForJava9(Map<Object, Object> context) throws IOException, NoSuchFieldException, SecurityException {
			ObjectProvider functionProvider = ObjectProvider.get(context);
			try (
				InputStream inputStream =
					Resources.getAsInputStream(this.getClass().getClassLoader(), this.getClass().getPackage().getName().replace(".", "/") + "/ConsulterRetrieverForJDK9.bwc"
				);
			) {
				MethodHandle privateLookupInMethodHandle = functionProvider.getOrBuildObject(PrivateLookupInMethodHandleSupplier.class, context).get();
				Class<?> methodHandleWrapperClass = functionProvider.getOrBuildObject(
					DefineHookClassFunction.class, context
				).apply(Class.class, Streams.toByteArray(inputStream));
				functionProvider.getOrBuildObject(SetFieldValueFunction.class, context).accept(
					methodHandleWrapperClass, methodHandleWrapperClass.getDeclaredField("consulterRetriever"),
					privateLookupInMethodHandle
				);
				setFunction((java.util.function.Function<Class<?>, MethodHandles.Lookup>)
					functionProvider.getOrBuildObject(AllocateInstanceFunction.class, context).apply(methodHandleWrapperClass));
			}
		}

		
		@Override
		public MethodHandles.Lookup apply(Class<?> input) {
			return function.apply(input);
		}
		
	}
}