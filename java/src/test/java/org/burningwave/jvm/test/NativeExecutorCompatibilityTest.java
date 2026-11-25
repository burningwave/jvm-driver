package org.burningwave.jvm.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.burningwave.jvm.NativeExecutor;
import org.junit.Test;


/**
 * Checks that the deprecated {@link NativeExecutor} class still works by passing the calls to
 * {@link org.burningwave.jni.Executor}.
 */
@SuppressWarnings("deprecation")
public class NativeExecutorCompatibilityTest {

	private static class Bean {
		private static int staticValue;
		private String value = "initialized";
	}

	@Test
	public void passesTheCallsToExecutor() {
		NativeExecutor nativeExecutor = NativeExecutor.getInstance();
		assertSame(nativeExecutor, NativeExecutor.getInstance());

		Bean bean = (Bean)nativeExecutor.allocateInstance(Bean.class);
		//The instance is allocated without running its constructor
		assertNull(bean.value);

		Field valueField = nativeExecutor.getDeclaredField(Bean.class, "value", "Ljava/lang/String;");
		nativeExecutor.setFieldValue(bean, valueField, "set");
		assertEquals("set", bean.value);
		assertEquals("set", nativeExecutor.getObjectFieldValue(bean, valueField));

		Field staticValueField = nativeExecutor.getDeclaredStaticField(Bean.class, "staticValue", "I");
		nativeExecutor.setStaticIntFieldValue(staticValueField, 42);
		assertEquals(42, Bean.staticValue);
		assertEquals(42, nativeExecutor.getFieldValue(null, staticValueField));

		Exception exception = new Exception("Thrown by the engine");
		try {
			nativeExecutor.throwException(exception);
			fail("The exception has not been thrown");
		} catch (Exception exc) {
			assertSame(exception, exc);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void initializerRejectsALibraryWhenTheEngineIsAlreadyInitialized() {
		NativeExecutor.getInstance();
		NativeExecutor.Initializer.setNativeLibraryNameAndInit("Executor");
	}

}
