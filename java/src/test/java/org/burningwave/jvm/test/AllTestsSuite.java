package org.burningwave.jvm.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
    DefaultDriverTest.class,
    DynamicDriverTest.class,
    HybridDriverTest.class,
    NativeDriverTest.class
})
public class AllTestsSuite {

}
