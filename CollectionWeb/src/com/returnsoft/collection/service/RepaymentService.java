package com.returnsoft.collection.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.vo.RepaymentFile;

public interface RepaymentService {
	
	public List<Repayment> findBySale(Long saleId) throws ServiceException;
	
	public long checkIfExist(String saleCode, BigDecimal returnedAmount);
	
	//public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException;
	
	//public void add(Repayment repayment) throws ServiceException;
	
	public void addRepaymentList(List<Repayment> repayments, RepaymentFile headers, String filename, User createdBy);
	
public List<Repayment> findList(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber) throws ServiceException;
	
	public List<Repayment> findLimit(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber, Integer first, Integer limit) throws ServiceException;
	
	public Long findCount(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber) throws ServiceException;

}
