package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.RepaymentService;

@Stateless
public class RepaymentServiceImpl implements RepaymentService{
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private RepaymentEao repaymentEao;
	
	@Override
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException{
		try {
			return repaymentEao.findBySaleIdAndReturnedDate(saleId, returnedDate);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		
	}
	
	@Override
	public void add(Repayment repayment) throws ServiceException {
		try {
			Sale sale = saleEao.findByCode(repayment.getCode());
			repayment.setSale(sale);
			repaymentEao.add(repayment);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Repayment> findBySale(Long saleId) throws ServiceException{
		try {
			return repaymentEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
}
