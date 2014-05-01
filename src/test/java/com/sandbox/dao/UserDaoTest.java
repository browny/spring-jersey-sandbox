package com.sandbox.dao;

import javax.inject.Inject;

import org.no_ip.mikelue.jpa.query.QueryUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.sandbox.orm.User;
import com.sandbox.test.AbstractDaoTestBase;

public class UserDaoTest extends AbstractDaoTestBase
{
	@Inject
	private UserDao testedDao;

	@Test
	public void testSaveNew() throws Exception
	{
		User newEntity = new User();
		newEntity.setName("joe");
		testedDao.saveNew(newEntity);

		String sql =
				" SELECT * FROM user " +
				" WHERE id = :id";

		Object obj = QueryUtil.getSingleResult(
				getEntityManager().createNativeQuery(sql)
				.setParameter("id", newEntity.getId()));

		Assert.assertNotNull(obj);
	}

}
