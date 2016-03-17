package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.SaleStateHistoryEao;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class SaleStateHistoryEao {
	
	@PersistenceContext
	private EntityManager em;

	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<SaleStateHistory> findBySaleId(Long saleId) throws EaoException {
		try {
			String query = "SELECT ssh FROM SaleStateHistory ssh  WHERE ssh.id = :saleId ";

			TypedQuery<SaleStateHistory> q = em.createQuery(query, SaleStateHistory.class);
			q.setParameter("saleId", saleId);

			List<SaleStateHistory> saleStates = q.getResultList();
			return saleStates;
		} catch (Exception e) {

			e.printStackTrace();

			throw new EaoException(e);
		}
	}
	
	

}
