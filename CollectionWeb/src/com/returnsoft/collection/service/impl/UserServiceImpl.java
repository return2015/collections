package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.UserEao;
//import com.returnsoft.collection.eao.UserTypeEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserInactiveException;
import com.returnsoft.collection.exception.UserNotFoundException;
import com.returnsoft.collection.exception.UserWrongPasswordException;
import com.returnsoft.collection.service.UserService;
//import com.returnsoft.collection.entity.UserType;

@Stateless
public class UserServiceImpl implements UserService {

	@EJB
	private UserEao userEao;

	@EJB
	private BankEao bankEao;

	/*@EJB
	private UserTypeEao userTypeEao;*/

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

	/*public boolean logoutUser(String USERNAME) throws ServiceException {

		// String USERNAME = "agente1";
		// String[] USERTYPES = { "administrator", "coordinator", "recruiter" };

		try {

			return true;

		} catch (Exception e) {
			throw new ServiceException("ServiceException:" + e.getMessage());
		}

	}*/

	/*public User currentUser(String USERNAME) throws ServiceException {

		try {

			User user = userEao.findByUsername(USERNAME);
			if (user == null) {
				throw new ServiceException("El usuario es nulo.");
			}

			UserType userType = userEao.findUserType(user.getId());
			user.setUserType(userType);

			return user;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}

	}*/

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

	/*public List<UserType> getUserTypes() throws ServiceException {
		try {

			List<UserType> userTypes = userTypeEao.getUserTypes();

			return userTypes;

		} catch (EaoException e) {
			throw new ServiceException("EaoException:" + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("ServiceException:" + e.getMessage());
		}
	}*/

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

	public List<Bank> findBanksByUserId(Integer userId) throws ServiceException {
		try {

			List<Bank> banks = bankEao.findByUserId(userId);

			return banks;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

	public Bank findBankById(Short bankId) throws ServiceException {
		try {

			Bank bank = bankEao.findById(bankId);

			return bank;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

	public List<Bank> getBanks() throws ServiceException {
		try {

			List<Bank> banks = bankEao.getBanks();

			return banks;

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
