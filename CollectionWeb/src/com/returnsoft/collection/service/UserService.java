package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;


public interface UserService {

	public User loginUser(String USERNAME, String PASSWORD)
			throws ServiceException;
	
	public List<User> find(String documentNumber,String name) throws ServiceException;
	
	public void add(User user) throws ServiceException;
	
	public User update(User user) throws ServiceException;
	
	public User findById(Integer userId) throws ServiceException;
	
	

}
