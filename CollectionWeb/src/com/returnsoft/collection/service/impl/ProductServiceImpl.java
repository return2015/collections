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
	
	@Override
	public List<Product> getAll() throws ServiceException{
		try {
			return productEao.getProducts();
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}

	@Override
	public Product checkIfExist(String code){
		try {
			return productEao.ckeckIfExist(code);
		} catch (Exception e) {
			e.printStackTrace();
			return null;	
		}
	}

}
