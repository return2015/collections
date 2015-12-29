package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.PayerHistory;
import com.returnsoft.collection.exception.ServiceException;

public interface PayerService {
	
	//public void add(Payer payer) throws ServiceException;
	
	public Payer update(Payer payer) throws ServiceException;
	
	public List<PayerHistory> findBySale(Long saleId) throws ServiceException;

}
