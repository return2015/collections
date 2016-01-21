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
	
	public List<PaymentMethod> getAll() throws ServiceException{
		try {
			
			return paymentMethodEao.getAll();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

}
