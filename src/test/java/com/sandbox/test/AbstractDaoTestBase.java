package com.sandbox.test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testng.annotations.Test;

import com.sandbox.config.DatabaseConfig;

/**
 * Test base for DAO types.<p>
 *
 * @see AbstractDatabaseTestBase
 */
@ContextConfiguration(
    classes={DatabaseConfig.class, DaoTestConfig.class}
)
@TestExecutionListeners(listeners={TransactionalTestExecutionListener.class})
@Test(suiteName="DaoSuite")
public abstract class AbstractDaoTestBase extends AbstractDatabaseTestBase
{
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Utility method for getting entity manager
	 */
	final protected EntityManager getEntityManager()
	{
		return entityManager;
	}

	final protected void flushAndClearEntityManager()
	{
		getEntityManager().flush();
		getEntityManager().clear();
	}

}
