package com.returnsoft.collection.service;

import java.util.List;


import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.exception.ServiceException;

import com.returnsoft.collection.util.SaleStateFile;

public interface SaleStateService {
	
	//public void add(SaleState maintenance) throws ServiceException;
	
	public SaleState update(SaleState saleState) throws ServiceException;
	
	public List<SaleStateHistory> findBySale(Long saleId) throws ServiceException;
	
	public void updateSaleStateList(List<SaleState> saleStates, String filename, SaleStateFile headers, Integer userId, Short bankId);

}
