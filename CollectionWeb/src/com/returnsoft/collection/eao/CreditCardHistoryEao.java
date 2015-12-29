package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.exception.EaoException;

public interface CreditCardHistoryEao {
	
	public List<CreditCardHistory> findBySaleId(Long saleId) throws EaoException;

}
