package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.exception.ServiceException;

public interface BankService {
	
	public List<Bank> getAll() throws ServiceException;
	
	public Bank findById(Short bankId) throws ServiceException;
	
	public List<Bank> findByUser(Integer userId) throws ServiceException;

}
