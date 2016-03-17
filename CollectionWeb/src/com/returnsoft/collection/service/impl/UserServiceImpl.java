package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserInactiveException;
import com.returnsoft.collection.exception.UserNotFoundException;
import com.returnsoft.collection.exception.UserWrongPasswordException;
import com.returnsoft.collection.service.UserService;


@Stateless
public class UserServiceImpl implements UserService {

	@EJB
	private UserEao userEao;

	@EJB
	private BankEao bankEao;

	@Override
	public User loginUser(String username, String password)
			throws ServiceException {
		try {
			User user = userEao.findByUsername(username);
			if (user==null) {
				throw new UserNotFoundException();
			}
			if (!user.getPassword().equals(password)) {
				throw new UserWrongPasswordException();
			}
			if (user.getIsActive().equals(false)) {
				throw new UserInactiveException();
			}
			return user;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}

	}

	@Override
	public List<User> find(String name)
			throws ServiceException {
		try {
			return userEao.find(name);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void add(User user) throws ServiceException {
		try {
			userEao.add(user);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public User update(User user) throws ServiceException {
		try {
			return userEao.update(user);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public User findById(Integer userId) throws ServiceException {
		try {
			return userEao.findById(userId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
