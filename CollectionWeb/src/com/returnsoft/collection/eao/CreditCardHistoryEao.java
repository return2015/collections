package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CreditCardHistoryEao {
	
	@PersistenceContext
	private EntityManager em;

	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<CreditCardHistory> findBySaleId(Long saleId) throws EaoException {
		try {
			
			String query = "SELECT cch FROM CreditCardHistory cch left join cch.sale s "
					+ "WHERE s.id = :saleId ";

			TypedQuery<CreditCardHistory> q = em.createQuery(query, CreditCardHistory.class);
			q.setParameter("saleId", saleId);

			List<CreditCardHistory> creditCards = q.getResultList();
			return creditCards;
			
		} catch (Exception e) {
			e.printStackTrace();

			throw new EaoException(e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Sale> findByCreditCardNumber(Long creditCardNumber) throws EaoException {
		try {
			
			String query = "SELECT s "
					+ "FROM CreditCardHistory cch "
					+ "left join fetch cch.sale s "
					+ "left join fetch cch.sale.product p " 
					+ "left join fetch cch.sale.bank b "
					+ "left join fetch cch.sale.lote l "
					+ "left join fetch cch.sale.collectionPeriod cp "
					+ "left join fetch cch.sale.createdBy cb "
					+ "left join fetch cch.sale.updatedBy ub "
					+ "left join fetch cch.sale.payer pa "
					+ "left join fetch cch.sale.creditCard cc "
					+ "left join fetch cch.sale.saleState ss "
					+ "WHERE cch.number = :creditCardNumber ";

			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("creditCardNumber", creditCardNumber);

			List<Sale> sales = q.getResultList();
			return sales;
			
		} catch (Exception e) {
			e.printStackTrace();

			throw new EaoException(e);
		}
	}

}
