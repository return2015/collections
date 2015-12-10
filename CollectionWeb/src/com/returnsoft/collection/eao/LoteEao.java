package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;

public interface LoteEao {
	
	public void add(Lote lote) throws EaoException;
	
	public Lote update(Lote lote) throws EaoException;
	
	public List<Lote> findByDate(Date date) throws EaoException;

}
