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
package org.burningwave.jvm;


import org.burningwave.jvm.function.catalog.AllocateInstanceFunction;
import org.burningwave.jvm.function.catalog.ConsulterSupplier;
import org.burningwave.jvm.function.catalog.ConsulterSupplyFunction;
import org.burningwave.jvm.function.catalog.GetFieldValueFunction;
import org.burningwave.jvm.function.catalog.GetLoadedClassesRetrieverFunction;
import org.burningwave.jvm.function.catalog.GetLoadedPackagesFunction;
import org.burningwave.jvm.function.catalog.SetAccessibleFunction;
import org.burningwave.jvm.function.catalog.SetFieldValueFunction;


public class NativeDriver extends io.github.toolfactory.jvm.NativeDriver {

	@Override
	protected Class<? extends ConsulterSupplier> getConsulterSupplierFunctionClass() {
		return ConsulterSupplier.Native.class;
	}

	@Override
	protected Class<? extends io.github.toolfactory.jvm.function.catalog.ConsulterSupplyFunction> getConsulterSupplyFunctionClass() {
		return ConsulterSupplyFunction.Native.class;
	}


	@Override
	protected Class<? extends GetLoadedPackagesFunction> getGetLoadedPackagesFunctionClass() {
		return GetLoadedPackagesFunction.Native.class;
	}

	@Override
	protected Class<? extends GetLoadedClassesRetrieverFunction> getGetLoadedClassesRetrieverFunctionClass() {
		return GetLoadedClassesRetrieverFunction.Native.class;
	}


	@Override
	protected Class<? extends SetFieldValueFunction> getSetFieldValueFunctionClass() {
		return SetFieldValueFunction.Native.class;
	}


	@Override
	protected Class<? extends GetFieldValueFunction> getGetFieldValueFunctionClass() {
		return GetFieldValueFunction.Native.class;
	}


	@Override
	protected Class<? extends AllocateInstanceFunction> getAllocateInstanceFunctionClass() {
		return AllocateInstanceFunction.Native.class;
	}


	@Override
	protected Class<? extends SetAccessibleFunction> getSetAccessibleFunctionClass() {
		return SetAccessibleFunction.Native.class;
	}

}
