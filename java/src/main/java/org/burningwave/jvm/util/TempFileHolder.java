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