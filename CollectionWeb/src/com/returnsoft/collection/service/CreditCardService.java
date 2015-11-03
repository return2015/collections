package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.exception.ServiceException;

public interface CreditCardService {
	

	public CreditCard add(CreditCard creditCardUpdate) throws ServiceException;
	
	public List<CreditCard> findBySale(Long saleId) throws ServiceException;

}
