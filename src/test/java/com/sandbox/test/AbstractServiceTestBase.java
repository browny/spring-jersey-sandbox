package com.sandbox.test;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

/**
 * Base class for writing code to test services in system.<p>
 */
@ContextConfiguration(
    classes={ServiceTestConfig.class}
)
@Test(suiteName="ServiceSuite")
public abstract class AbstractServiceTestBase extends AbstractBasicSpringBase
{

}
