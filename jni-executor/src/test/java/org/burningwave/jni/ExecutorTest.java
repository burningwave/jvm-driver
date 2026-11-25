package org.burningwave.jni;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.function.ThrowingRunnable;
import org.junit.Test;

public class ExecutorTest {

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
        final Executor executor = Executor.getInstance();
        final Class<?> cls = TestBean.class;
        TestBean bean = (TestBean) executor.allocateInstance(cls);
        printBean(bean);

        assertThrows(TestException.class, new ThrowingRunnable () {
        	@Override
        	public void run() {
        		executor.throwException(new TestException());
        	}
        });

        Field f_sta_obj = executor.getDeclaredStaticField(cls, "sta_obj", "Ljava/lang/Object;");
        assertNotNull(f_sta_obj);
        Field f_sta_byte = executor.getDeclaredStaticField(cls, "sta_byte", "B");
        assertNotNull(f_sta_byte);
        Field f_sta_short = executor.getDeclaredStaticField(cls, "sta_short", "S");
        assertNotNull(f_sta_short);
        Field f_sta_int = executor.getDeclaredStaticField(cls, "sta_int", "I");
        assertNotNull(f_sta_int);
        Field f_sta_long = executor.getDeclaredStaticField(cls, "sta_long", "J");
        assertNotNull(f_sta_long);
        Field f_sta_float = executor.getDeclaredStaticField(cls, "sta_float", "F");
        assertNotNull(f_sta_float);
        Field f_sta_double = executor.getDeclaredStaticField(cls, "sta_double", "D");
        assertNotNull(f_sta_double);
        Field f_sta_char = executor.getDeclaredStaticField(cls, "sta_char", "C");
        assertNotNull(f_sta_char);
        Field f_sta_boolean = executor.getDeclaredStaticField(cls, "sta_boolean", "Z");
        assertNotNull(f_sta_boolean);


        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		executor.getDeclaredStaticField(cls, "sta_int", "Ljava/lang/Object;");
        	}
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		executor.getDeclaredStaticField(cls, "sta_i", "I");
        	}
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
        	public void run() throws Throwable {
        		executor.getDeclaredStaticField(cls, "sta_i", "Ljava/lang/Object;");
        	}
        });


        Field f_ins_obj = executor.getDeclaredField(cls, "ins_obj", "Ljava/lang/Object;");
        assertNotNull(f_ins_obj);
        Field f_ins_byte = executor.getDeclaredField(cls, "ins_byte", "B");
        assertNotNull(f_ins_byte);
        Field f_ins_short = executor.getDeclaredField(cls, "ins_short", "S");
        assertNotNull(f_ins_short);
        Field f_ins_int = executor.getDeclaredField(cls, "ins_int", "I");
        assertNotNull(f_ins_int);
        Field f_ins_long = executor.getDeclaredField(cls, "ins_long", "J");
        assertNotNull(f_ins_long);
        Field f_ins_float = executor.getDeclaredField(cls, "ins_float", "F");
        assertNotNull(f_ins_float);
        Field f_ins_double = executor.getDeclaredField(cls, "ins_double", "D");
        assertNotNull(f_ins_double);
        Field f_ins_char = executor.getDeclaredField(cls, "ins_char", "C");
        assertNotNull(f_ins_char);
        Field f_ins_boolean = executor.getDeclaredField(cls, "ins_boolean", "Z");
        assertNotNull(f_ins_boolean);

        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		executor.getDeclaredField(cls, "ins_int", "Ljava/lang/Object;");
        	};
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		executor.getDeclaredField(cls, "ins_i", "I");
        	};
        });
        assertThrows(NoSuchFieldError.class, new ThrowingRunnable () {
        	@Override
			public void run() throws Throwable {
        		executor.getDeclaredField(cls, "ins_i", "Ljava/lang/Object;");
        	};
        });

        executor.setStaticObjectFieldValue(f_sta_obj, "test");
        assertEquals("test", TestBean.sta_obj);
        assertEquals("test", executor.getStaticObjectFieldValue(f_sta_obj));

        executor.setStaticByteFieldValue(f_sta_byte, (byte) 1);
        assertEquals((byte) 1, TestBean.sta_byte);
        assertEquals((byte) 1, executor.getStaticByteFieldValue(f_sta_byte));

        executor.setStaticShortFieldValue(f_sta_short, (short) 1);
        assertEquals((short) 1, TestBean.sta_short);
        assertEquals((short) 1, executor.getStaticShortFieldValue(f_sta_short));

        executor.setStaticIntFieldValue(f_sta_int, 1);
        assertEquals(1, TestBean.sta_int);
        assertEquals(1, executor.getStaticIntFieldValue(f_sta_int));

        executor.setStaticLongFieldValue(f_sta_long, 1L);
        assertEquals(1L, TestBean.sta_long);
        assertEquals(1L, executor.getStaticLongFieldValue(f_sta_long));

        executor.setStaticFloatFieldValue(f_sta_float, 1.0f);
        assertEquals(1.0f, TestBean.sta_float, 0.0f);
        assertEquals(1.0f, executor.getStaticFloatFieldValue(f_sta_float), 0.0f);

        executor.setStaticDoubleFieldValue(f_sta_double, 1.0);
        assertEquals(1.0, TestBean.sta_double, 0.0f);
        assertEquals(1.0, executor.getStaticDoubleFieldValue(f_sta_double), 0.0);

        executor.setStaticCharFieldValue(f_sta_char, 'a');
        assertEquals('a', TestBean.sta_char);
        assertEquals('a', executor.getStaticCharFieldValue(f_sta_char));

        executor.setStaticBooleanFieldValue(f_sta_boolean, true);
        assertTrue(TestBean.sta_boolean);
        assertTrue(executor.getStaticBooleanFieldValue(f_sta_boolean));

        executor.setObjectFieldValue(bean, f_ins_obj, "test");
        assertEquals("test", bean.ins_obj);
        assertEquals("test", executor.getObjectFieldValue(bean, f_ins_obj));

        executor.setByteFieldValue(bean, f_ins_byte, (byte) 1);
        assertEquals((byte) 1, bean.ins_byte);
        assertEquals((byte) 1, executor.getByteFieldValue(bean, f_ins_byte));

        executor.setShortFieldValue(bean, f_ins_short, (short) 1);
        assertEquals((short) 1, bean.ins_short);
        assertEquals((short) 1, executor.getShortFieldValue(bean, f_ins_short));

        executor.setIntFieldValue(bean, f_ins_int, 1);
        assertEquals(1, bean.ins_int);
        assertEquals(1, executor.getIntFieldValue(bean, f_ins_int));

        executor.setLongFieldValue(bean, f_ins_long, 1L);
        assertEquals(1L, bean.ins_long);
        assertEquals(1L, executor.getLongFieldValue(bean, f_ins_long));

        executor.setFloatFieldValue(bean, f_ins_float, 1.0f);

        assertEquals(1.0f, bean.ins_float, 0.0f);
        assertEquals(1.0f, executor.getFloatFieldValue(bean, f_ins_float), 0.0f);

        executor.setDoubleFieldValue(bean, f_ins_double, 1.0);
        assertEquals(1.0, bean.ins_double, 0.0f);
        assertEquals(1.0, executor.getDoubleFieldValue(bean, f_ins_double), 0.0);

        executor.setCharFieldValue(bean, f_ins_char, 'a');
        assertEquals('a', bean.ins_char);
        assertEquals('a', executor.getCharFieldValue(bean, f_ins_char));

        executor.setBooleanFieldValue(bean, f_ins_boolean, true);
        assertTrue(bean.ins_boolean);
        assertTrue(executor.getBooleanFieldValue(bean, f_ins_boolean));

        printBean(bean);


        executor.setFieldValue(bean, f_sta_obj, "test2");
        assertEquals("test2", TestBean.sta_obj);
        assertEquals("test2", executor.getFieldValue(bean, f_sta_obj));

        executor.setFieldValue(bean, f_sta_byte, (byte) 2);
        assertEquals((byte) 2, TestBean.sta_byte);
        assertEquals((byte) 2, executor.getFieldValue(bean, f_sta_byte));

        executor.setFieldValue(bean, f_sta_short, (short) 2);
        assertEquals((short) 2, TestBean.sta_short);
        assertEquals((short) 2, executor.getFieldValue(bean, f_sta_short));

        executor.setFieldValue(bean, f_sta_int, 2);
        assertEquals(2, TestBean.sta_int);
        assertEquals(2, executor.getFieldValue(bean, f_sta_int));

        executor.setFieldValue(bean, f_sta_long, 2L);
        assertEquals(2L, TestBean.sta_long);
        assertEquals(2L, executor.getFieldValue(bean, f_sta_long));

        executor.setFieldValue(bean, f_sta_float, 2.0f);
        assertEquals(2.0f, TestBean.sta_float, 0.0f);
        assertEquals(2.0f, executor.getFieldValue(bean, f_sta_float));

        executor.setFieldValue(bean, f_sta_double, 2.0);
        assertEquals(2.0, TestBean.sta_double, 0.0f);
        assertEquals(2.0, executor.getFieldValue(bean, f_sta_double));

        executor.setFieldValue(bean, f_sta_char, 'b');
        assertEquals('b', TestBean.sta_char);
        assertEquals('b', executor.getFieldValue(bean, f_sta_char));

        executor.setFieldValue(bean, f_sta_boolean, false);
        assertFalse(TestBean.sta_boolean);
        assertFalse((Boolean) executor.getFieldValue(bean, f_sta_boolean));

        executor.setFieldValue(bean, f_ins_obj, "test2");
        assertEquals("test2", bean.ins_obj);
        assertEquals("test2", executor.getFieldValue(bean, f_ins_obj));

        executor.setFieldValue(bean, f_ins_byte, (byte) 2);
        assertEquals((byte) 2, bean.ins_byte);
        assertEquals((byte) 2, executor.getFieldValue(bean, f_ins_byte));

        executor.setFieldValue(bean, f_ins_short, (short) 2);
        assertEquals((short) 2, bean.ins_short);
        assertEquals((short) 2, executor.getFieldValue(bean, f_ins_short));

        executor.setFieldValue(bean, f_ins_int, 2);
        assertEquals(2, bean.ins_int);
        assertEquals(2, executor.getFieldValue(bean, f_ins_int));

        executor.setFieldValue(bean, f_ins_long, 2L);
        assertEquals(2L, bean.ins_long);
        assertEquals(2L, executor.getFieldValue(bean, f_ins_long));

        executor.setFieldValue(bean, f_ins_float, 2.0f);
        assertEquals(2.0f, bean.ins_float, 0.0f);
        assertEquals(2.0f, executor.getFieldValue(bean, f_ins_float));

        executor.setFieldValue(bean, f_ins_double, 2.0);
        assertEquals(2.0, bean.ins_double, 0.0f);
        assertEquals(2.0, executor.getFieldValue(bean, f_ins_double));

        executor.setFieldValue(bean, f_ins_char, 'b');
        assertEquals('b', bean.ins_char);
        assertEquals('b', executor.getFieldValue(bean, f_ins_char));

        executor.setFieldValue(bean, f_ins_boolean, false);
        assertFalse(bean.ins_boolean);
        assertFalse((Boolean) executor.getFieldValue(bean, f_ins_boolean));

        printBean(bean);
    }
}
