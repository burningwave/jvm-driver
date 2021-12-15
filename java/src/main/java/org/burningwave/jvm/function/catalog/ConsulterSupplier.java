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


public interface ConsulterSupplier extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplier {

	public static interface Native extends ConsulterSupplier {

		public static class ForJava7 extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.Abst implements Native {

			public ForJava7(Map<Object, Object> context) throws NoSuchFieldException {
				super(context);
				org.burningwave.jvm.NativeExecutor.getInstance().setAllowedModes(consulter, -1);
			}

			public static class ForSemeru extends Abst implements ConsulterSupplier.Native {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException {
					super(context);
					org.burningwave.jvm.NativeExecutor.getInstance().setAllowedModes(
						consulter,
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED
					);
				}
			}
		}

		public static interface ForJava9 extends Native, ConsulterSupplier {

			public static class ForSemeru extends Abst implements Native.ForJava9 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
					super(context);
					org.burningwave.jvm.NativeExecutor.getInstance().setAllowedModes(
						consulter,
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava7.ForSemeru.INTERNAL_PRIVILEGED |
						io.github.toolfactory.jvm.function.catalog.ConsulterSupplier.ForJava9.ForSemeru.MODULE
					);
				}

			}
		}

		public static interface ForJava14 extends Native, ConsulterSupplier {

			public static class ForSemeru extends ConsulterSupplier.Native.ForJava7.ForSemeru implements Native.ForJava14 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
					super(context);
				}
			}
		}

		public static interface ForJava17 extends Native, ConsulterSupplier {

			public static class ForSemeru extends Native.ForJava7 implements Native.ForJava17 {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
					super(context);
				}
			}
		}

	}


	public static interface Hybrid extends ConsulterSupplier {

		public static class ForJava17 extends ConsulterSupplier.Native.ForJava7 implements Hybrid {

			public ForJava17(Map<Object, Object> context) throws NoSuchFieldException {
				super(context);
			}

			public static class ForSemeru extends ConsulterSupplier.Native.ForJava17.ForSemeru implements ConsulterSupplier.Hybrid {

				public ForSemeru(Map<Object, Object> context) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
					super(context);
				}
			}
		}
	}

}
