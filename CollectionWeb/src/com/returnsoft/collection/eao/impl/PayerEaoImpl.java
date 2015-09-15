package com.returnsoft.collection.eao.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class PayerEaoImpl implements PayerEao{
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(Payer payer) throws EaoException{
		try {
			
			em.persist(payer);
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}

}
