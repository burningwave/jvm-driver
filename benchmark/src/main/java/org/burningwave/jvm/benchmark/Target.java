/*
 * This file is part of Burningwave JVM driver.
 *
 * Author: Roberto Gentili
 *
 * Hosted at: https://github.com/burningwave/jvm-driver
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Roberto Gentili
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
package org.burningwave.jvm.benchmark;


/**
 * The class whose private fields are read, written and looked up by the benchmarks, and whose instances are
 * allocated by {@link AllocateInstanceBenchmark}.
 * <p>
 * The {@code String} fields correspond to the {@code name} field of the benchmark published in 2022 in
 * <a href="https://github.com/toolfactory/narcissus/discussions/11">toolfactory/narcissus#11</a>. The {@code int}
 * values stay between -128 and 127, so the methods that return or accept them boxed use the {@link Integer} cache
 * instead of allocating.
 */
public class Target {
	static final int INITIAL_INT_VALUE = 42;
	static final String INITIAL_STRING_VALUE = "initial value";

	private static String staticStringValue = INITIAL_STRING_VALUE;
	private static int staticIntValue = INITIAL_INT_VALUE;

	private String stringValue = INITIAL_STRING_VALUE;
	private int intValue = INITIAL_INT_VALUE;

}
