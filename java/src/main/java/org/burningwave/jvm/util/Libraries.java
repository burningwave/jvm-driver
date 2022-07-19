/*
 * This file is derived from ToolFactory Narcissus.
 *
 * Hosted at: https://github.com/toolfactory/narcissus
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
package org.burningwave.jvm.util;


import java.io.File;
import java.util.Locale;

import org.burningwave.jvm.NativeExecutor;

import io.github.toolfactory.jvm.InfoImpl;
import io.github.toolfactory.jvm.function.template.Consumer;
import io.github.toolfactory.jvm.util.Strings;


public class Libraries {

	String conventionedSuffix;
	String extension;
	String prefix;


	private Libraries() {
		io.github.toolfactory.jvm.InfoImpl jVMInfo = (InfoImpl)io.github.toolfactory.jvm.Info.Provider.getInfoInstance();
		if (jVMInfo.is32Bit()) {
			conventionedSuffix = "x32";
		} else if (jVMInfo.is64Bit()) {
			conventionedSuffix = "x64";
		}
		String osArch = jVMInfo.osArch != null ? jVMInfo.osArch : "generic";
		String operatingSystemName = jVMInfo.operatingSystemName != null ? jVMInfo.operatingSystemName.toLowerCase(Locale.ENGLISH) : "generic";
		prefix = "lib";
		if (osArch.equals("aarch64")) {
			conventionedSuffix = osArch;
			extension = "so";
		} else if ((operatingSystemName.indexOf("mac") >= 0) || (operatingSystemName.indexOf("darwin") >= 0)) {
			extension = "dylib";
		} else if (operatingSystemName.indexOf("win") >= 0) {
			prefix = null;
			extension = "dll";
		} else if (operatingSystemName.indexOf("nux") >= 0) {
			extension = "so";
		} else {
			throw new InitializeException(
				Strings.compile(
					"Unable to initialize {}: unsupported operating system ('{}-{}')",
					this, operatingSystemName, osArch
				)
			);
		}
	}

    public static Libraries getInstance() {
    	return Holder.getWithinInstance();
    }

    static Libraries create() {
    	return new Libraries();
    }

    public void loadFor(Class<?> clazz) {
    	String libName = clazz.getPackage().getName();
    	if (libName != null) {
    		libName = libName.replace(".", "/") + "/";
    	} else {
    		libName = "";
    	}
    	if (prefix != null) {
    		libName += prefix;
    	}
    	libName += clazz.getSimpleName() + "-" + conventionedSuffix + "." + extension;
		Files.extractAndExecute(
			NativeExecutor.class,
			libName,
			new Consumer<File>() {
				@Override
				public void accept(File libraryFile) {
					System.load(libraryFile.getAbsolutePath());
				}
			}

		);
	}

	private static class Holder {
		private static final Libraries INSTANCE = Libraries.create();

		private static Libraries getWithinInstance() {
			return INSTANCE;
		}
	}


	public static class InitializeException extends RuntimeException {

		private static final long serialVersionUID = 424757273075028708L;

		public InitializeException(String message, Throwable cause) {
	        super(message, cause);
	    }

		public InitializeException(String message) {
	        super(message);
	    }

	}
}
