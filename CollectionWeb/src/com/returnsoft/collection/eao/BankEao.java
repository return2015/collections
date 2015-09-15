package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.exception.EaoException;


public interface BankEao {
	
	public List<Bank> findByUserId(Integer userId) throws EaoException;
	
	public Bank findById(Short bankId) throws EaoException;
	
	public List<Bank> getBanks() throws EaoException;

}
