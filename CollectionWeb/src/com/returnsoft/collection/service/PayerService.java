package com.returnsoft.collection.service;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface PayerService {
	
	public void add(Payer payer) throws ServiceException;
	
	public Sale findSaleById(Long id) throws ServiceException;

}
