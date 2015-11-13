package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
	
	public List<Payer> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT ss FROM Payer ss left join ss.sale s WHERE s.id = :saleId ";
			
			TypedQuery<Payer> q = em.createQuery(query, Payer.class);
			q.setParameter("saleId", saleId);
			
			List<Payer> payers = q.getResultList();
			return payers;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	

}
