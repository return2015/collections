package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.exception.EaoException;


public interface CommerceEao {
	
	public List<Commerce> findByBankId(Short bankId) throws EaoException;
	
	public Commerce findById(Short id) throws EaoException;
	
	public Short findIdById(Short id) throws EaoException;

}
