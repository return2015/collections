package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.ServiceException;

public interface MaintenanceService {
	
	public void add(SaleState maintenance) throws ServiceException;
	
	public List<SaleState> findBySale(Long saleId) throws ServiceException;

}
