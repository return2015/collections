package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.sun.mail.iap.Response;

public interface CollectionService {
	
	public void add(Collection collection) throws ServiceException;
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException;
	
	public Sale findByCode(String code) throws ServiceException;
	
	public List<Collection> findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;
	
	public List<Collection> findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;

}
