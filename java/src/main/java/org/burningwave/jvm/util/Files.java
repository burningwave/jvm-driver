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
 * Copyright (c) 2019-2023 Luke Hutchison, Roberto Gentili
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.github.toolfactory.jvm.function.template.Consumer;
import io.github.toolfactory.jvm.util.Strings;


public class Files {


	public static void extractAndExecute(Class<?> callerClass, String resourcePath, Consumer<File> extractedFileConsumer) {

        try (
        	InputStream inputStream = callerClass.getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath);
        ) {
            if (inputStream == null) {
                throw new FileNotFoundException("Could not find file: " + resourcePath);
            }
            String filename = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            try (TempFileHolder tempFileHandler = new TempFileHolder(filename)) {
	            byte[] buffer = new byte[1024];
	            try (OutputStream os = new FileOutputStream(tempFileHandler.getFile())) {
	                for (int readBytes; (readBytes = inputStream.read(buffer)) != -1;) {
	                    os.write(buffer, 0, readBytes);
	                }
	            }
	            extractedFileConsumer.accept(tempFileHandler.getFile());
            }
        } catch (Throwable exc) {
        	throw new NotLoadedException(Strings.compile("Unable to load file {}", resourcePath), exc);
        }
    }

	public static class NotLoadedException extends RuntimeException {

		private static final long serialVersionUID = -461698261422622020L;

		public NotLoadedException(String message, Throwable cause) {
	        super(message, cause);
	    }

		public NotLoadedException(String message) {
	        super(message);
	    }

	}
}
