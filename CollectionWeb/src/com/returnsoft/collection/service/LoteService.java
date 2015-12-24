package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.controller.SaleFile;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;

public interface LoteService {
	
	public void update(Lote lote);
	
	public Lote create(String name, Integer total) throws ServiceException;
	
	public List<Lote> findByDate(Date date) throws ServiceException;
	
	public void add(List<Sale> sales, String filename, SaleFile headers, Integer userId, Short bankId) ;

}
