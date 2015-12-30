package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.entity.CreditCardHistory;

import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CreditCardHistoryEaoImpl implements CreditCardHistoryEao{
	
	@PersistenceContext
	private EntityManager em;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<CreditCardHistory> findBySaleId(Long saleId) throws EaoException {
		try {
			
			String query = "SELECT cch FROM CreditCardHistory cch  WHERE cch.id = :saleId ";

			TypedQuery<CreditCardHistory> q = em.createQuery(query, CreditCardHistory.class);
			q.setParameter("saleId", saleId);

			List<CreditCardHistory> creditCards = q.getResultList();
			return creditCards;
			
		} catch (Exception e) {
			e.printStackTrace();

			throw new EaoException(e);
		}
	}

}
