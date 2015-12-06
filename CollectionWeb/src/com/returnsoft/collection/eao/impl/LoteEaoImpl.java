package com.returnsoft.collection.eao.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;
@Stateless
public class LoteEaoImpl implements LoteEao {

	@PersistenceContext
	private EntityManager em;
	
	public void add(Lote lote) throws EaoException{
		try {
			
			em.persist(lote);
			em.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
}
