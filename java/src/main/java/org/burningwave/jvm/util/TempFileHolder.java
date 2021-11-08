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
 * Copyright (c) 2019-2021 Roberto Gentili
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
import java.util.UUID;

import io.github.toolfactory.jvm.util.Strings;

public class TempFileHolder implements Closeable {
	private static File mainTemporaryFolder;
	private File file = null;
	boolean isPosix = false;
	
	static {
		File toDelete = null;
		try {
			toDelete = File.createTempFile("_BW_TEMP_", "_temp");
		} catch (IOException exception) {
			throw new RuntimeException("Unable to create temporary folder", exception);
		}
		File tempFolder = toDelete.getParentFile();
		File folder = new File(tempFolder.getAbsolutePath() + "/" + "Burningwave" +"/"+ UUID.randomUUID().toString() + "_" + System.currentTimeMillis());
		if (!folder.exists()) {
			folder.mkdirs();
			folder.deleteOnExit();
		}
		toDelete.delete();
		mainTemporaryFolder =  folder;
	}
	
	public TempFileHolder(String fileName) throws IOException {
		file = new File(mainTemporaryFolder.getAbsolutePath() + "/" + fileName);
		if (file.exists()) {
			throw new IllegalArgumentException(Strings.compile("Temporary file {} already exists", file.getAbsolutePath()));
		}
		file.createNewFile();
		try {
			isPosix = file.toPath().getFileSystem().supportedFileAttributeViews().contains("posix");
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
		if (file != null) {
			boolean deleted = false;
			if (isPosix) {
				deleted = file.delete();
			}
			if (!deleted) {
				file.deleteOnExit();
			}
		}
	}
}