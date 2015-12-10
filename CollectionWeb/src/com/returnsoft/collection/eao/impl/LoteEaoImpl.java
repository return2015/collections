package com.returnsoft.collection.eao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
	
	public List<Lote> findByDate(Date date) throws EaoException{
		
		try {
			
			Calendar started = Calendar.getInstance();
			started.setTime(date);
			started.set(Calendar.HOUR, 0);
			started.set(Calendar.MINUTE, 0);
			started.set(Calendar.SECOND, 0);
			
			Calendar ended = Calendar.getInstance();
			ended.setTime(date);
			ended.set(Calendar.HOUR, 23);
			ended.set(Calendar.MINUTE, 59);
			ended.set(Calendar.SECOND, 59);
			
			String query = "SELECT l FROM "
					+ "Lote l "
					+ "WHERE l.date between :startDate and  :endDate";
					
			
			TypedQuery<Lote> q = em.createQuery(query, Lote.class);
			q.setParameter("startDate", started.getTime());
			q.setParameter("endDate", ended.getTime());
			
			List<Lote> lotes = q.getResultList();
			
			return lotes;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

}
