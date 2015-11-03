package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.ProductService;

@Stateless
public class ProductServiceImpl implements ProductService {
	
	@EJB
	private ProductEao productEao; 
	
	
	public List<Product> getAll() throws ServiceException{
		try {
			
			return productEao.getProducts();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

}
