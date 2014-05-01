package com.sandbox.dao;

import org.no_ip.mikelue.jpa.springframework.SpringInjectedTypedDaoFacade;
import org.springframework.stereotype.Repository;

import com.sandbox.orm.User;

@Repository
public class UserDao extends SpringInjectedTypedDaoFacade<User, Integer>
{

}
