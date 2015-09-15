package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface AffiliationService {
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException;
	
	public Sale findByCode(String code) throws ServiceException;
	
	public Sale affiliate(String code, int userId, Date affiliateDate) throws ServiceException;

}
