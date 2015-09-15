package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface RepaymentService {
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException;
	
	public Sale findSaleByCode(String code) throws ServiceException;
	
	//public Collection findCollectionByReceiptNumber(String receiptNumber) throws ServiceException;
	
	public List<Collection> findCollectionsAllowBySaleId(Long saleId) throws ServiceException;
	
	public List<Repayment> findRepaymentsBySaleId(Long saleId) throws ServiceException;
	
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException;
	
	public void add(Repayment repayment) throws ServiceException;

}
