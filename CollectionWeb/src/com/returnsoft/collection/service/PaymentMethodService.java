package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.exception.ServiceException;

public interface PaymentMethodService {
	
	public List<PaymentMethod> getAll() throws ServiceException;

}
