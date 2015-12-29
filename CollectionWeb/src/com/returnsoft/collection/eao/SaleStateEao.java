package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.EaoException;


public interface SaleStateEao {
	
	public SaleState update(SaleState saleState) throws EaoException;
	
	//public List<SaleState> findBySaleId(Long saleId) throws EaoException;

}
