package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.PayerHistory;
import com.returnsoft.collection.exception.EaoException;

public interface PayerHistoryEao {
	
	public List<PayerHistory> findBySaleId(Long saleId) throws EaoException;

}
