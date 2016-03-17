package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.PayerHistoryEao;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.PayerHistory;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.PayerService;

@Stateless
public class PayerServiceImpl implements PayerService{
	
	@EJB
	private PayerEao payerEao;
	
	@EJB
	private PayerHistoryEao payerHistoryEao;
	
	@Override
	public Payer update(Payer payer) throws ServiceException {
		try {
			return payerEao.update(payer);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<PayerHistory> findBySale(Long saleId) throws ServiceException{
		try {
			return payerHistoryEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	

}
