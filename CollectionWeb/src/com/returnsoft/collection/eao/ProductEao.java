package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.exception.EaoException;


public interface ProductEao {
	
	public List<Product> getProducts() throws EaoException;

}
