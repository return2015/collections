package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.ServiceException;

public interface CollectionService {
	
	public void add(Collection collection) throws ServiceException;
	
	public Integer findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;
	
	public Integer findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;
	
	public List<Collection> findAllowsBySale(Long saleId) throws ServiceException;
	
	public List<Collection> findBySale(Long saleId) throws ServiceException;

}
