package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CommerceService;

@Stateless
public class CommerceServiceImpl implements CommerceService{
	
	@EJB
	private CommerceEao commerceEao;
	
	public List<Commerce> findByBank(Short bankId) throws ServiceException{
		try {
			
			return commerceEao.findByBankId(bankId);
			
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
