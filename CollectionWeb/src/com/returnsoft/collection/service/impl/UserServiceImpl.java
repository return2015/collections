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

	
	public User loginUser(String username, String password)
			throws ServiceException {

		//String[] USERTYPES = { "administrator", "collector", "agent" };

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

			/*UserType userType = userEao.findUserType(user.getId());
			if (userType == null) {
				throw new ServiceException("El tipo de usuario es nulo.");
			}

			if (!Arrays.asList(USERTYPES).contains(userType.getCode())) {
				throw new ServiceException("El tipo de usuario es inválido.");
			}

			if (userType.getCode().equals(USERTYPES[0])) {

			} else if (userType.getCode().equals(USERTYPES[1])) {

			} else if (userType.getCode().equals(USERTYPES[2])) {

			}*/

			return user;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}

	}

	

	public List<User> find(String documentNumber, String name)
			throws ServiceException {
		try {
			List<User> users = userEao.find(documentNumber, name);

			return users;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}


	public void add(User user) throws ServiceException {

		try {
			userEao.add(user);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public User update(User user) throws ServiceException {

		try {
			user = userEao.update(user);
			
			return user;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	

	public User findById(Integer userId) throws ServiceException {
		try {

			User user = userEao.findById(userId);

			return user;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

	

}
