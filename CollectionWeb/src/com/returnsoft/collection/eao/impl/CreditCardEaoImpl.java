package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CreditCardEaoImpl implements CreditCardEao{
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(CreditCard creditCard) throws EaoException{
		try {

			em.persist(creditCard);
			em.flush();

			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	
	
	public List<CreditCard> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT cc FROM CreditCard cc left join cc.sale s WHERE s.id = :saleId ";
			
			TypedQuery<CreditCard> q = em.createQuery(query, CreditCard.class);
			q.setParameter("saleId", saleId);
			
			List<CreditCard> creditCardUpdates = q.getResultList();
			return creditCardUpdates;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber) throws EaoException{
		try {
			
			String query = "SELECT s FROM CreditCard cc left join cc.sale s "
					+ "WHERE cc.number = :creditCardNumber ";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("creditCardNumber", creditCardNumber);
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	

}
