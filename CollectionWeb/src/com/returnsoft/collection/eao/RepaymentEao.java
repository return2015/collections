package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.exception.EaoException;


public interface RepaymentEao {
	
	public void add(Repayment repayment) throws EaoException;
	
	public List<Repayment> findBySaleId(Long saleId) throws EaoException;
	
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws EaoException;

}
