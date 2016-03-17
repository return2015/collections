package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.util.CollectionFile;

public interface CollectionService {
	
	public void add(Collection collection) throws ServiceException;
	
	public void addCollectionList(List<Collection> collections, CollectionFile headers, String filename, User createdBy);
	
	public Integer findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;
	
	public Integer findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws ServiceException;
	
	public List<Collection> findAllowsBySale(Long saleId) throws ServiceException;
	
	public List<Collection> findBySale(Long saleId) throws ServiceException;
	
	public List<Collection> findLimit(Date createdStartedAt, Date createdEndedAt, Integer first, Integer limit) throws ServiceException;
	
	public Long findCount(Date createdStartedAt, Date createdEndedAt) throws ServiceException;

}
