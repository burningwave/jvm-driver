package org.burningwave.jni;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;


public class LibrariesTest {

	@Test
	public void windowsLibraryNames() {
		assertLibraryResourcePath("Windows 11", "amd64", "64", "Executor-x64.dll");
		assertLibraryResourcePath("Windows 10", "x86", "32", "Executor-x32.dll");
		assertLibraryResourcePath("Windows 11", "aarch64", "64", "Executor-aarch64.dll");
	}

	@Test
	public void linuxLibraryNames() {
		assertLibraryResourcePath("Linux", "amd64", "64", "libExecutor-x64.so");
		assertLibraryResourcePath("Linux", "i386", "32", "libExecutor-x32.so");
		assertLibraryResourcePath("Linux", "aarch64", "64", "libExecutor-aarch64.so");
		assertLibraryResourcePath("Linux", "arm", "32", "libExecutor-arm32.so");
	}

	@Test
	public void macOSLibraryNames() {
		assertLibraryResourcePath("Mac OS X", "x86_64", "64", "libExecutor-x64.dylib");
		assertLibraryResourcePath("Mac OS X", "aarch64", "64", "libExecutor-aarch64.dylib");
	}

	@Test
	public void otherOperatingSystemsHaveTheirNameInTheLibraryName() {
		assertLibraryResourcePath("FreeBSD", "amd64", "64", "libExecutor-freebsd-x64.so");
		assertLibraryResourcePath("SunOS", "amd64", "64", "libExecutor-solaris-x64.so");
		assertLibraryResourcePath("AIX", "ppc64", "64", "libExecutor-aix-ppc64.so");
	}

	@Test
	public void dataModelIsUsedWhenTheArchitectureIsNotAvailable() {
		assertLibraryResourcePath("Linux", null, "64", "libExecutor-x64.so");
		assertLibraryResourcePath("Windows 7", null, "32", "Executor-x32.dll");
	}

	@Test
	public void unknownOperatingSystemIsRejected() {
		try {
			new Libraries("Plan 9", "amd64", "64");
			fail("An unknown operating system must be rejected");
		} catch (Libraries.InitializeException exc) {
			//Expected
		}
	}

	@Test
	public void bundledLibrariesMatchTheLoaderNames() {
		assertBundled(new Libraries("Windows 10", "amd64", "64"));
		assertBundled(new Libraries("Windows 10", "x86", "32"));
		assertBundled(new Libraries("Linux", "amd64", "64"));
		assertBundled(new Libraries("Linux", "i386", "32"));
		assertBundled(new Libraries("Linux", "aarch64", "64"));
		assertBundled(new Libraries("Mac OS X", "x86_64", "64"));
		assertBundled(new Libraries("Mac OS X", "aarch64", "64"));
	}

	@Test
	public void libraryOfTheCurrentPlatformIsBundled() {
		assertBundled(Libraries.getInstance());
	}

	private static void assertLibraryResourcePath(String osName, String osArch, String dataModel, String expectedFileName) {
		assertEquals(
			"org/burningwave/jni/" + expectedFileName,
			new Libraries(osName, osArch, dataModel).getLibraryResourcePath(Executor.class)
		);
	}

	private static void assertBundled(Libraries libraries) {
		String libraryResourcePath = libraries.getLibraryResourcePath(Executor.class);
		assertNotNull(libraryResourcePath + " is not bundled", Executor.class.getResource("/" + libraryResourcePath));
	}

}
