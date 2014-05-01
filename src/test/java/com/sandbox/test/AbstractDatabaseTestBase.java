package com.sandbox.test;

import java.lang.reflect.Method;

import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.no_ip.mikelue.jpa.test.dbunit.DbUnitBuilder;
import org.no_ip.mikelue.jpa.test.dbunit.YamlDataSet;
import org.no_ip.mikelue.jpa.test.dbunit.annotation.ReflectAnnotationDbUnitContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Test base for environment of database(without JPA).<p>
 *
 * This base class would upgrade schema and update default data for testing before the test suite.<p>
 */
@ContextConfiguration(
    classes={DatabaseTestConfig.class}
)
public abstract class AbstractDatabaseTestBase extends AbstractBasicSpringBase
{
	private static final Logger baseLogger = LoggerFactory.getLogger(AbstractDatabaseTestBase.class);

    /**
     * Executes Spring's processing for "before method" after
     * {@link #processDataSetAfterMethod} has get called.<p>
     */
	@Override @BeforeMethod(dependsOnMethods="processDataSetBeforeMethod")
	protected void springTestContextBeforeTestMethod(Method testMethod) throws Exception
	{
		super.springTestContextBeforeTestMethod(testMethod);
	}

    /**
     * Executes Spring's processing for "before method" after
     * this method has get called.<p>
     */
    @BeforeMethod(firstTimeOnly=true)
    protected void processDataSetBeforeMethod(Method method) throws Exception
    {
        try {
            applicationContext.getBean(ReflectAnnotationDbUnitContext.class).beforeOperation(method);
        } catch (Exception e) {
            baseLogger.error("@BeforeMethod exception: {}", e.getCause());
            throw e;
        }
    }
    /**
     * Executes Spring's processing for "after method" before
     * this method has get called.<p>
     */
    @AfterMethod(lastTimeOnly=true, dependsOnMethods="springTestContextAfterTestMethod")
    protected void processDataSetAfterMethod(Method method)
    {
        applicationContext.getBean(ReflectAnnotationDbUnitContext.class).afterOperation(method);
    }

	/**
	 * Execute the operation on {@link YamlDataSet}.<p>
	 *
	 * @param dataset The dataset to be operated
	 * @param dbOperation The operation to be executed
	 */
	protected void operateDataSet(IDataSet dataset, DatabaseOperation dbOperation)
	{
        DbUnitBuilder builder = applicationContext.getBean(DbUnitBuilder.class);

        builder.runDatabaseOperation(
            dataset,
			dbOperation
        );
	}

}
