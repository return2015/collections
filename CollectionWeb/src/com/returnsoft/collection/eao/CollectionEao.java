package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.EaoException;


public interface CollectionEao {
	
	public void add(Collection collection) throws EaoException;
	
	public List<Collection> findBySaleId(Long saleId) throws EaoException;
	
	public List<Collection> findAllowsBySaleId(Long saleId) throws EaoException;
	
	public List<Collection> findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws EaoException;
	
	public List<Collection> findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode) throws EaoException;
	
	public Collection findByReceiptNumber(String receiptNumber) throws EaoException;

}
