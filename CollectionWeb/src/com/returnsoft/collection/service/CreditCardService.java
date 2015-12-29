package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.exception.ServiceException;

public interface CreditCardService {
	

	public CreditCard update(CreditCard creditCard) throws ServiceException;
	
	public List<CreditCardHistory> findBySale(Long saleId) throws ServiceException;

}
