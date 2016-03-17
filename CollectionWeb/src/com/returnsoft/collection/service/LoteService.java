package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;

public interface LoteService {
	
	public List<Lote> findByDate(Date date) throws ServiceException;
	
	//public void addTypeCollection(List<Collection> collections, String filename, CollectionFile headers, Integer userId) ;
	
	public List<Lote> findLimit(Date date, LoteTypeEnum loteType, Integer first, Integer limit) throws ServiceException;
	
	public Long findCount(Date date, LoteTypeEnum loteType) throws ServiceException;

}
