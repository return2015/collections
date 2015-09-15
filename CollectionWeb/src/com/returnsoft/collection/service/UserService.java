package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.User;
//import com.returnsoft.collection.entity.UserType;
import com.returnsoft.collection.exception.ServiceException;


public interface UserService {

	public User loginUser(String USERNAME, String PASSWORD)
			throws ServiceException;

	//public boolean logoutUser(String USERNAME) throws ServiceException;

	//public User currentUser(String USERNAME) throws ServiceException;
	
	public List<User> find(String documentNumber,String name) throws ServiceException;

	
	//public List<UserType> getUserTypes() throws ServiceException;
	
	public void add(User user) throws ServiceException;
	
	public User update(User user) throws ServiceException;
	
	public User findById(Integer userId) throws ServiceException;
	
	public List<Bank> findBanksByUserId(Integer userId) throws ServiceException;
	
	public Bank findBankById(Short bankId) throws ServiceException;
	
	public List<Bank> getBanks() throws ServiceException;

	

}
