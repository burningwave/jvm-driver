package org.burningwave.jvm;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
    DefaultDriverTest.class,
    HybridDriverTest.class,
    NativeDriverTest.class,
    DynamicDriverTest.class
})
public class AllTestsSuite {

}
