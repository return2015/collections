package com.returnsoft.collection.service;

import java.util.List;


import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.exception.ServiceException;

public interface CollectionPeriodService {
	
	
	public List<CollectionPeriod> getAll() throws ServiceException;
	
	public CollectionPeriod findById(Short collectionPeriodId) throws ServiceException;
	
	public short checkIfExist(String name);

}
