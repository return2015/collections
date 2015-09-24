package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.exception.EaoException;

public interface PayerEao {
	
	public void add(Payer payer) throws EaoException;
	
	public List<Payer> findBySaleId(Long saleId) throws EaoException;

}
