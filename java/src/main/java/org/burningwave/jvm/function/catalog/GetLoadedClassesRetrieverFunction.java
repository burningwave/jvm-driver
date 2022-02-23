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


import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import io.github.toolfactory.jvm.function.catalog.GetDeclaredFieldFunction;
import io.github.toolfactory.jvm.function.catalog.ThrowExceptionFunction;
import io.github.toolfactory.jvm.util.CleanableSupplier;
import io.github.toolfactory.jvm.util.ObjectProvider;


@SuppressWarnings("all")
public interface GetLoadedClassesRetrieverFunction extends io.github.toolfactory.jvm.function.catalog.GetLoadedClassesRetrieverFunction {

	public static interface Native extends GetLoadedClassesRetrieverFunction {

		public static class ForJava7 implements Native {
			protected Field classesField;
			protected org.burningwave.jvm.NativeExecutor nativeExecutor;

			public ForJava7(Map<Object, Object> context) throws Throwable {
				ObjectProvider functionProvider = ObjectProvider.get(context);
				GetDeclaredFieldFunction getDeclaredFieldFunction = functionProvider.getOrBuildObject(GetDeclaredFieldFunction.class, context);
				classesField = getDeclaredFieldFunction.apply(ClassLoader.class, "classes");
				nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();
			}

			@Override
			public CleanableSupplier<Collection<Class<?>>> apply(final ClassLoader classLoader) {
				if (classLoader == null) {
					throw new NullPointerException("Input classLoader parameter can't be null");
				}
				return new CleanableSupplier<Collection<Class<?>>>() {
					Collection<Class<?>> classes;

					@Override
					public Collection<Class<?>> get() {
						if (classes != null) {
							return classes;
						}
						return classes = (Collection<Class<?>>)nativeExecutor.getFieldValue(classLoader, classesField);
					}

					@Override
					public void clear() {
						get();
						if (classes != null) {
							classes.clear();
						}
					}

				};
			}

			public static class ForSemeru extends GetLoadedClassesRetrieverFunction.ForJava7.ForSemeru {
				protected org.burningwave.jvm.NativeExecutor nativeExecutor;


				public ForSemeru(Map<Object, Object> context) throws Throwable {
					super(context);
					nativeExecutor = org.burningwave.jvm.NativeExecutor.getInstance();
				}

				@Override
				protected ClassNameBasedLockSupplier buildClassNameBasedLockSupplier(final Map<Object, Object> context) {
					return new ClassNameBasedLockSupplier() {
						protected ThrowExceptionFunction throwExceptionFunction =
								ObjectProvider.get(context).getOrBuildObject(ThrowExceptionFunction.class, context);

						@Override
						public Hashtable<String, Object> get(ClassLoader classLoader) {
							return (Hashtable<String, Object>)nativeExecutor.getFieldValue(classLoader, classNameBasedLockField);
						}

						@Override
						protected ClassLoader getClassLoader(Class<?> cls) {
							return (ClassLoader)nativeExecutor.getFieldValue(cls, classLoaderField);
						}

					};

				}

			}
		}

	}

}
