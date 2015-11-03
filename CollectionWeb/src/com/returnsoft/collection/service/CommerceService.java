package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.exception.ServiceException;

public interface CommerceService {
	
	public List<Commerce> findByBank(Short bankId) throws ServiceException;

}
