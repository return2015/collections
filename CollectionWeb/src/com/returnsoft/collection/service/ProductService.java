package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.exception.ServiceException;

public interface ProductService {
	
	public List<Product> getAll() throws ServiceException;

}
