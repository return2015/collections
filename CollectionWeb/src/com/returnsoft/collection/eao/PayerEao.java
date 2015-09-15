package com.returnsoft.collection.eao;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.exception.EaoException;

public interface PayerEao {
	
	public void add(Payer payer) throws EaoException;

}
