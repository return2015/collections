package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;


import com.returnsoft.collection.eao.CollectionPeriodEao;

import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CollectionPeriodService;

@Stateless
public class CollectionPeriodServiceImpl implements CollectionPeriodService {
	
	
	@EJB
	private CollectionPeriodEao collectionPeriodEao;
	
	public List<CollectionPeriod> getAll() throws ServiceException{
		try {
			
			return collectionPeriodEao.getAll();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public CollectionPeriod findById(Short collectionPeriodId) throws ServiceException {
		try {

			CollectionPeriod collectionPeriod = collectionPeriodEao.findById(collectionPeriodId);

			return collectionPeriod;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	

}
