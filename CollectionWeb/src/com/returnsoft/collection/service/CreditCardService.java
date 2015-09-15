package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface CreditCardService {
	
	//public List<SaleState> getSaleStates() throws ServiceException;
	
	public CreditCard add(CreditCard creditCardUpdate) throws ServiceException;
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException;
	
	public Sale findByCode(String code) throws ServiceException;

}
