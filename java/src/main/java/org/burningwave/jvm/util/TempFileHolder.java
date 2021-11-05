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
package org.burningwave.jvm.util;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;


public class TempFileHolder implements Closeable {
	File file = null;
	boolean isPosix = false;
	
	public TempFileHolder(String prefix, String suffix) throws IOException {
		file = File.createTempFile(prefix, suffix);
        try {
            if (file.toPath().getFileSystem().supportedFileAttributeViews().contains("posix")) {
            	isPosix = true;
            }
        } catch (Throwable e) {}
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean isPosix() {
		return isPosix;
	}
	
	@Override
	public void close() throws IOException {
		boolean deleted = false;
        if (isPosix) {
            deleted = file.delete();
        }
        if (!deleted) {
        	file.deleteOnExit();
        }				
	};
}