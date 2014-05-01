package com.sandbox.resources;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.sandbox.dao.UserDao;
import com.sandbox.orm.User;

@Named
@Path("users")
public class UserResources extends AbstractResourcesBase
{
	@Inject
	private UserDao userDao;

	@Path("{id}")
	@GET
	public User getUser(
		@PathParam("id") Integer id
	) {
		return userDao.find(id);
	}

	@POST
	public User newUser(
		@FormParam("name") String name
	) {
		User user = new User();
		user.setName(name);

		userDao.saveNew(user);

		return user;
	}

}
