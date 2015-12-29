package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.exception.EaoException;

public interface SaleStateHistoryEao {
	
	public List<SaleStateHistory> findBySaleId(Long saleId) throws EaoException;

}
