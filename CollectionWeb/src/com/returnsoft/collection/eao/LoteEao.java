package com.returnsoft.collection.eao;

import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;

public interface LoteEao {
	
	public void add(Lote lote) throws EaoException;

}
