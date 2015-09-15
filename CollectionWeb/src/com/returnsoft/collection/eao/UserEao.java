package com.returnsoft.collection.eao;



import java.util.List;

import com.returnsoft.collection.entity.User;
//import com.returnsoft.collection.entity.UserType;
import com.returnsoft.collection.exception.EaoException;

public interface UserEao {
	
	public User findByUsername(String username) throws EaoException;
	
	public User findParent(Integer userId) throws EaoException;
	
	//public UserType findUserType(Integer userId) throws EaoException;
	
	public List<User> findByUserType(String userTypeCode) throws EaoException;
	
	public List<User> find(String documentNumber, String name) throws EaoException;
	
	public User findCoordinatorByArea(Integer areaId) throws EaoException;
	
	public void add(User user) throws EaoException;
	
	public User update(User user) throws EaoException;
	
	public User findById(Integer userId) throws EaoException;
	
	
	

}
