package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.PaymentMethodEao;
import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.PaymentMethodService;

@Stateless
public class PaymentMethodServiceImpl implements PaymentMethodService {
	
	@EJB
	private PaymentMethodEao paymentMethodEao;
	
	@Override
	public List<PaymentMethod> getAll() throws ServiceException{
		try {
			return paymentMethodEao.getAll();
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public PaymentMethod checkIfExist(String code){
		try {
			return paymentMethodEao.ckeckIfExist(code);
		} catch (Exception e) {
			e.printStackTrace();
			return null;	
		}
	}

}
