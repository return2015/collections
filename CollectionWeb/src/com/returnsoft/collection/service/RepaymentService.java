package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.exception.ServiceException;

public interface RepaymentService {
	
	public List<Repayment> findBySale(Long saleId) throws ServiceException;
	
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException;
	
	public void add(Repayment repayment) throws ServiceException;

}
