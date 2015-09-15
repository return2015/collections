package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface MaintenanceService {

	public List<Commerce> findCommercesByBankId(Short bankId)
			throws ServiceException;

	public Sale findSaleById(Long id) throws ServiceException;
	
	public Sale findSaleByCode(String code) throws ServiceException;
	
	public void add(SaleState maintenance) throws ServiceException;

}
