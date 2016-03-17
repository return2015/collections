package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class PayerEao {
	
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void add(Payer payer) throws EaoException{
		try {
			
			em.persist(payer);
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	public Payer update(Payer payer) throws EaoException{
		try {
			
			payer = em.merge(payer);
			em.flush();
			return payer;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
