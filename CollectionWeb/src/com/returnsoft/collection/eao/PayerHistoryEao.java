package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.PayerHistoryEao;
import com.returnsoft.collection.entity.PayerHistory;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class PayerHistoryEao{
	
	@PersistenceContext
	private EntityManager em;

	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PayerHistory> findBySaleId(Long saleId) throws EaoException {
		try {
			String query = "SELECT ph FROM PayerHistory ph left join ph.sale s  WHERE s.id = :saleId ";

			TypedQuery<PayerHistory> q = em.createQuery(query, PayerHistory.class);
			q.setParameter("saleId", saleId);

			List<PayerHistory> payers = q.getResultList();
			return payers;
		} catch (Exception e) {

			e.printStackTrace();

			throw new EaoException(e);
		}
	}

}
