package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.exception.EaoException;

public interface PaymentMethodEao {
	
	public List<PaymentMethod> getAll() throws EaoException;

}
