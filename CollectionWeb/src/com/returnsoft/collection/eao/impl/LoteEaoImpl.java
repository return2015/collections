package com.returnsoft.collection.eao.impl;

//import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class LoteEaoImpl implements LoteEao {

	@PersistenceContext
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void add(Lote lote) throws EaoException {
		try {

			em.persist(lote);
			em.flush();

		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Lote update(Lote lote) throws EaoException {
		try {

			lote = em.merge(lote);
			em.flush();
			return lote;

		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
}
