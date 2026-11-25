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
 * Copyright (c) 2021 Luke Hutchison, Roberto Gentili
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
package org.burningwave.jni;


import java.io.File;
import java.util.Locale;


class Libraries {

	String operatingSystem;
	String architecture;
	String prefix;
	String extension;
	boolean operatingSystemInLibraryName;


	private Libraries() {
		this(
			getSystemProperty("os.name"),
			getSystemProperty("os.arch"),
			getSystemProperty("sun.arch.data.model")
		);
	}

	Libraries(String operatingSystemName, String osArch, String dataModel) {
		operatingSystem = toOperatingSystem(operatingSystemName);
		if (operatingSystem == null) {
			throw new InitializeException(
				"Unable to initialize " + this + ": unsupported operating system ('" + operatingSystemName + "-" + osArch + "')"
			);
		}
		architecture = toArchitecture(osArch, dataModel);
		if (architecture == null) {
			throw new InitializeException(
				"Unable to initialize " + this + ": unknown architecture ('" + operatingSystemName + "-" + osArch + "')"
			);
		}
		prefix = "lib";
		extension = "so";
		if (operatingSystem.equals("windows")) {
			prefix = null;
			extension = "dll";
		} else if (operatingSystem.equals("macos")) {
			extension = "dylib";
		} else if (!operatingSystem.equals("linux")) {
			//These operating systems use the .so extension like Linux, so their name is needed to tell the libraries apart
			operatingSystemInLibraryName = true;
		}
	}

	static String toOperatingSystem(String operatingSystemName) {
		if (operatingSystemName == null) {
			return null;
		}
		String name = operatingSystemName.toLowerCase(Locale.ENGLISH);
		if (name.contains("mac") || name.contains("darwin")) {
			return "macos";
		} else if (name.contains("win")) {
			return "windows";
		} else if (name.contains("nux")) {
			return "linux";
		} else if (name.contains("freebsd")) {
			return "freebsd";
		} else if (name.contains("openbsd")) {
			return "openbsd";
		} else if (name.contains("netbsd")) {
			return "netbsd";
		} else if (name.contains("sunos") || name.contains("solaris")) {
			return "solaris";
		} else if (name.contains("aix")) {
			return "aix";
		}
		return null;
	}

	static String toArchitecture(String osArch, String dataModel) {
		if (osArch == null || osArch.isEmpty()) {
			if (dataModel != null && dataModel.contains("32")) {
				return "x32";
			} else if (dataModel != null && dataModel.contains("64")) {
				return "x64";
			}
			return null;
		}
		String arch = osArch.toLowerCase(Locale.ENGLISH);
		if (arch.equals("amd64") || arch.equals("x86_64") || arch.equals("x64")) {
			return "x64";
		} else if (arch.equals("x86") || arch.equals("i386") || arch.equals("i486") || arch.equals("i586") || arch.equals("i686")) {
			return "x32";
		} else if (arch.equals("aarch64") || arch.equals("arm64")) {
			return "aarch64";
		} else if (arch.startsWith("arm")) {
			return "arm32";
		}
		return arch;
	}

	private static String getSystemProperty(String name) {
		try {
			return System.getProperty(name);
		} catch (SecurityException exc) {
			return null;
		}
	}

	public static Libraries getInstance() {
		return Holder.getWithinInstance();
	}

	static Libraries create() {
		return new Libraries();
	}

	String getLibraryName(Class<?> clazz) {
		return clazz.getSimpleName() + "-" + (operatingSystemInLibraryName ? operatingSystem + "-" : "") + architecture;
	}

	String getLibraryResourcePath(Class<?> clazz) {
		Package pckg = clazz.getPackage();
		String libraryResourcePath = pckg != null ? pckg.getName().replace(".", "/") + "/" : "";
		if (prefix != null) {
			libraryResourcePath += prefix;
		}
		return libraryResourcePath + getLibraryName(clazz) + "." + extension;
	}

	public String loadFor(Class<?> clazz) {
		String libraryResourcePath = getLibraryResourcePath(clazz);
		if (Executor.class.getResource("/" + libraryResourcePath) == null) {
			throw new InitializeException(
				"Unable to load the native library for " + operatingSystem + " " + architecture + ": " +
				libraryResourcePath + " is not available"
			);
		}
		Files.extractAndExecute(
			Executor.class,
			libraryResourcePath,
			new Files.Consumer<File>() {
				@Override
				public void accept(File libraryFile) {
					System.load(libraryFile.getAbsolutePath());
				}
			}
		);
		return getLibraryName(clazz);
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
