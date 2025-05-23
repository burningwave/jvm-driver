package org.burningwave.jvm.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.burningwave.jvm.NativeExecutor;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.Test;

public class NativeExecutorTest {

    private static class TestBean {
        private static Object  sta_obj;
        private static byte    sta_byte;
        private static short   sta_short;
        private static int     sta_int;
        private static long    sta_long;
        private static float   sta_float;
        private static double  sta_double;
        private static char    sta_char;
        private static boolean sta_boolean;

        private Object  ins_obj;
        private byte    ins_byte;
        private short   ins_short;
        private int     ins_int;
        private long    ins_long;
        private float   ins_float;
        private double  ins_double;
        private char    ins_char;
        private boolean ins_boolean;

        @Override
        public String toString() {
            return "TestBean (\n  sta_obj=" + sta_obj
                    + ",  sta_byte=" + sta_byte
                    + ",  sta_short=" + sta_short
                    + ",  sta_int=" + sta_int
                    + ",  sta_long=" + sta_long
                    + ",  sta_float=" + sta_float
                    + ",  sta_double=" + sta_double
                    + ",  sta_char=" + sta_char
                    + ",  sta_boolean=" + sta_boolean
                    + ",\n  ins_obj=" + ins_obj
                    + ",  ins_byte=" + ins_byte
                    + ",  ins_short=" + ins_short
                    + ",  ins_int=" + ins_int
                    + ",  ins_long=" + ins_long
                    + ",  ins_float=" + ins_float
                    + ",  ins_double=" + ins_double
                    + ",  ins_char=" + ins_char
                    + ",  ins_boolean=" + ins_boolean
                    + "\n)";
        }
    }

    private static class TestException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    private static void printBean(TestBean bean) {
        System.out.println(bean);
    }


    @Test
    public void test() {
        final NativeExecutor nativeExecutor = NativeExecutor.getInstance();
        final Class<?> cls = TestBean.class;
        TestBean bean = (TestBean) nativeExecutor.allocateInstance(cls);
        printBean(bean);

        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() {
        		nativeExecutor.throwException(new TestException());
        	}
        });

        Field f_sta_obj = nativeExecutor.getDeclaredStaticField(cls, "sta_obj", "Ljava/lang/Object;");
        assertNotNull(f_sta_obj);
        Field f_sta_byte = nativeExecutor.getDeclaredStaticField(cls, "sta_byte", "B");
        assertNotNull(f_sta_byte);
        Field f_sta_short = nativeExecutor.getDeclaredStaticField(cls, "sta_short", "S");
        assertNotNull(f_sta_short);
        Field f_sta_int = nativeExecutor.getDeclaredStaticField(cls, "sta_int", "I");
        assertNotNull(f_sta_int);
        Field f_sta_long = nativeExecutor.getDeclaredStaticField(cls, "sta_long", "J");
        assertNotNull(f_sta_long);
        Field f_sta_float = nativeExecutor.getDeclaredStaticField(cls, "sta_float", "F");
        assertNotNull(f_sta_float);
        Field f_sta_double = nativeExecutor.getDeclaredStaticField(cls, "sta_double", "D");
        assertNotNull(f_sta_double);
        Field f_sta_char = nativeExecutor.getDeclaredStaticField(cls, "sta_char", "C");
        assertNotNull(f_sta_char);
        Field f_sta_boolean = nativeExecutor.getDeclaredStaticField(cls, "sta_boolean", "Z");
        assertNotNull(f_sta_boolean);


        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		nativeExecutor.getDeclaredStaticField(cls, "sta_int", "Ljava/lang/Object;");
        	}
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		nativeExecutor.getDeclaredStaticField(cls, "sta_i", "I");
        	}
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		nativeExecutor.getDeclaredStaticField(cls, "sta_i", "Ljava/lang/Object;");
        	}
        });


        Field f_ins_obj = nativeExecutor.getDeclaredField(cls, "ins_obj", "Ljava/lang/Object;");
        assertNotNull(f_ins_obj);
        Field f_ins_byte = nativeExecutor.getDeclaredField(cls, "ins_byte", "B");
        assertNotNull(f_ins_byte);
        Field f_ins_short = nativeExecutor.getDeclaredField(cls, "ins_short", "S");
        assertNotNull(f_ins_short);
        Field f_ins_int = nativeExecutor.getDeclaredField(cls, "ins_int", "I");
        assertNotNull(f_ins_int);
        Field f_ins_long = nativeExecutor.getDeclaredField(cls, "ins_long", "J");
        assertNotNull(f_ins_long);
        Field f_ins_float = nativeExecutor.getDeclaredField(cls, "ins_float", "F");
        assertNotNull(f_ins_float);
        Field f_ins_double = nativeExecutor.getDeclaredField(cls, "ins_double", "D");
        assertNotNull(f_ins_double);
        Field f_ins_char = nativeExecutor.getDeclaredField(cls, "ins_char", "C");
        assertNotNull(f_ins_char);
        Field f_ins_boolean = nativeExecutor.getDeclaredField(cls, "ins_boolean", "Z");
        assertNotNull(f_ins_boolean);

        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		nativeExecutor.getDeclaredField(cls, "ins_int", "Ljava/lang/Object;");
        	};
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		nativeExecutor.getDeclaredField(cls, "ins_i", "I");
        	};
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		nativeExecutor.getDeclaredField(cls, "ins_i", "Ljava/lang/Object;");
        	};
        });

        nativeExecutor.setStaticObjectFieldValue(f_sta_obj, "test");
        assertEquals("test", TestBean.sta_obj);
        assertEquals("test", nativeExecutor.getStaticObjectFieldValue(f_sta_obj));

        nativeExecutor.setStaticByteFieldValue(f_sta_byte, (byte) 1);
        assertEquals((byte) 1, TestBean.sta_byte);
        assertEquals((byte) 1, nativeExecutor.getStaticByteFieldValue(f_sta_byte));

        nativeExecutor.setStaticShortFieldValue(f_sta_short, (short) 1);
        assertEquals((short) 1, TestBean.sta_short);
        assertEquals((short) 1, nativeExecutor.getStaticShortFieldValue(f_sta_short));

        nativeExecutor.setStaticIntFieldValue(f_sta_int, 1);
        assertEquals(1, TestBean.sta_int);
        assertEquals(1, nativeExecutor.getStaticIntFieldValue(f_sta_int));

        nativeExecutor.setStaticLongFieldValue(f_sta_long, 1L);
        assertEquals(1L, TestBean.sta_long);
        assertEquals(1L, nativeExecutor.getStaticLongFieldValue(f_sta_long));

        nativeExecutor.setStaticFloatFieldValue(f_sta_float, 1.0f);
        assertEquals(1.0f, TestBean.sta_float, 0.0f);
        assertEquals(1.0f, nativeExecutor.getStaticFloatFieldValue(f_sta_float));

        nativeExecutor.setStaticDoubleFieldValue(f_sta_double, 1.0);
        assertEquals(1.0, TestBean.sta_double, 0.0f);
        assertEquals(1.0, nativeExecutor.getStaticDoubleFieldValue(f_sta_double));

        nativeExecutor.setStaticCharFieldValue(f_sta_char, 'a');
        assertEquals('a', TestBean.sta_char);
        assertEquals('a', nativeExecutor.getStaticCharFieldValue(f_sta_char));

        nativeExecutor.setStaticBooleanFieldValue(f_sta_boolean, true);
        assertTrue(TestBean.sta_boolean);
        assertTrue(nativeExecutor.getStaticBooleanFieldValue(f_sta_boolean));

        nativeExecutor.setObjectFieldValue(bean, f_ins_obj, "test");
        assertEquals("test", bean.ins_obj);
        assertEquals("test", nativeExecutor.getObjectFieldValue(bean, f_ins_obj));

        nativeExecutor.setByteFieldValue(bean, f_ins_byte, (byte) 1);
        assertEquals((byte) 1, bean.ins_byte);
        assertEquals((byte) 1, nativeExecutor.getByteFieldValue(bean, f_ins_byte));

        nativeExecutor.setShortFieldValue(bean, f_ins_short, (short) 1);
        assertEquals((short) 1, bean.ins_short);
        assertEquals((short) 1, nativeExecutor.getShortFieldValue(bean, f_ins_short));

        nativeExecutor.setIntFieldValue(bean, f_ins_int, 1);
        assertEquals(1, bean.ins_int);
        assertEquals(1, nativeExecutor.getIntFieldValue(bean, f_ins_int));

        nativeExecutor.setLongFieldValue(bean, f_ins_long, 1L);
        assertEquals(1L, bean.ins_long);
        assertEquals(1L, nativeExecutor.getLongFieldValue(bean, f_ins_long));

        nativeExecutor.setFloatFieldValue(bean, f_ins_float, 1.0f);

        assertEquals(1.0f, bean.ins_float, 0.0f);
        assertEquals(1.0f, nativeExecutor.getFloatFieldValue(bean, f_ins_float));

        nativeExecutor.setDoubleFieldValue(bean, f_ins_double, 1.0);
        assertEquals(1.0, bean.ins_double, 0.0f);
        assertEquals(1.0, nativeExecutor.getDoubleFieldValue(bean, f_ins_double));

        nativeExecutor.setCharFieldValue(bean, f_ins_char, 'a');
        assertEquals('a', bean.ins_char);
        assertEquals('a', nativeExecutor.getCharFieldValue(bean, f_ins_char));

        nativeExecutor.setBooleanFieldValue(bean, f_ins_boolean, true);
        assertTrue(TestBean.sta_boolean);
        assertTrue(nativeExecutor.getBooleanFieldValue(bean, f_ins_boolean));

        printBean(bean);


        nativeExecutor.setFieldValue(bean, f_sta_obj, "test2");
        assertEquals("test2", TestBean.sta_obj);
        assertEquals("test2", nativeExecutor.getFieldValue(bean, f_sta_obj));

        nativeExecutor.setFieldValue(bean, f_sta_byte, (byte) 2);
        assertEquals((byte) 2, TestBean.sta_byte);
        assertEquals((byte) 2, nativeExecutor.getFieldValue(bean, f_sta_byte));

        nativeExecutor.setFieldValue(bean, f_sta_short, (short) 2);
        assertEquals((short) 2, TestBean.sta_short);
        assertEquals((short) 2, nativeExecutor.getFieldValue(bean, f_sta_short));

        nativeExecutor.setFieldValue(bean, f_sta_int, 2);
        assertEquals(2, TestBean.sta_int);
        assertEquals(2, nativeExecutor.getFieldValue(bean, f_sta_int));

        nativeExecutor.setFieldValue(bean, f_sta_long, 2L);
        assertEquals(2L, TestBean.sta_long);
        assertEquals(2L, nativeExecutor.getFieldValue(bean, f_sta_long));

        nativeExecutor.setFieldValue(bean, f_sta_float, 2.0f);
        assertEquals(2.0f, TestBean.sta_float, 0.0f);
        assertEquals(2.0f, nativeExecutor.getFieldValue(bean, f_sta_float));

        nativeExecutor.setFieldValue(bean, f_sta_double, 2.0);
        assertEquals(2.0, TestBean.sta_double, 0.0f);
        assertEquals(2.0, nativeExecutor.getFieldValue(bean, f_sta_double));

        nativeExecutor.setFieldValue(bean, f_sta_char, 'b');
        assertEquals('b', TestBean.sta_char);
        assertEquals('b', nativeExecutor.getFieldValue(bean, f_sta_char));

        nativeExecutor.setFieldValue(bean, f_sta_boolean, false);
        assertFalse(TestBean.sta_boolean);
        assertFalse((Boolean) nativeExecutor.getFieldValue(bean, f_sta_boolean));

        nativeExecutor.setFieldValue(bean, f_ins_obj, "test2");
        assertEquals("test2", bean.ins_obj);
        assertEquals("test2", nativeExecutor.getFieldValue(bean, f_ins_obj));

        nativeExecutor.setFieldValue(bean, f_ins_byte, (byte) 2);
        assertEquals((byte) 2, bean.ins_byte);
        assertEquals((byte) 2, nativeExecutor.getFieldValue(bean, f_ins_byte));

        nativeExecutor.setFieldValue(bean, f_ins_short, (short) 2);
        assertEquals((short) 2, bean.ins_short);
        assertEquals((short) 2, nativeExecutor.getFieldValue(bean, f_ins_short));

        nativeExecutor.setFieldValue(bean, f_ins_int, 2);
        assertEquals(2, bean.ins_int);
        assertEquals(2, nativeExecutor.getFieldValue(bean, f_ins_int));

        nativeExecutor.setFieldValue(bean, f_ins_long, 2L);
        assertEquals(2L, bean.ins_long);
        assertEquals(2L, nativeExecutor.getFieldValue(bean, f_ins_long));

        nativeExecutor.setFieldValue(bean, f_ins_float, 2.0f);
        assertEquals(2.0f, bean.ins_float, 0.0f);
        assertEquals(2.0f, nativeExecutor.getFieldValue(bean, f_ins_float));

        nativeExecutor.setFieldValue(bean, f_ins_double, 2.0);
        assertEquals(2.0, bean.ins_double, 0.0f);
        assertEquals(2.0, nativeExecutor.getFieldValue(bean, f_ins_double));

        nativeExecutor.setFieldValue(bean, f_ins_char, 'b');
        assertEquals('b', bean.ins_char);
        assertEquals('b', nativeExecutor.getFieldValue(bean, f_ins_char));

        nativeExecutor.setFieldValue(bean, f_ins_boolean, false);
        assertFalse(TestBean.sta_boolean);
        assertFalse((Boolean) nativeExecutor.getFieldValue(bean, f_ins_boolean));

        printBean(bean);
    }
}
