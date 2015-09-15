package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.EaoException;


public interface CreditCardEao {
	
	public void add(CreditCard creditCardUpdate) throws EaoException;
	
	public List<CreditCard> findBySaleId(Long saleId) throws EaoException;
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber) throws EaoException;

}
