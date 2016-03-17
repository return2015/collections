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
	
	@Override
	public List<CollectionPeriod> getAll() throws ServiceException{
		try {
			return collectionPeriodEao.getAll();
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	
	@Override
	public CollectionPeriod findById(Short collectionPeriodId) throws ServiceException {
		try {
			return collectionPeriodEao.findById(collectionPeriodId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}

	@Override
	public short checkIfExist(String name){
		try {
			return collectionPeriodEao.ckeckIfExist(name);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;	
		}
	}
	

}
