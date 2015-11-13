package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CommerceEaoImpl implements CommerceEao {
	
	@PersistenceContext
	private EntityManager em;
	
	public Commerce findById(Short id) throws EaoException{
		try {
			
			Commerce commerce = em.find(Commerce.class, id);
			
			return commerce;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public Short findIdById(Short commerceId) throws EaoException{
		try {
			TypedQuery<Short> q = em.createQuery("SELECT c.id FROM Commerce c where c.id=:commerceId",Short.class);
			q.setParameter("commerceId", commerceId);
			
			return q.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	
	}
	
	
	
	public List<Commerce> findByBankId(Short bankId) throws EaoException{
		try {
			TypedQuery<Commerce> q = em.createQuery(
					"SELECT c FROM Commerce c left join c.bank b left join c.product p left join c.paymentMethod pm where b.id=:bankId",
					Commerce.class);
			q.setParameter("bankId", bankId);
			List<Commerce> commerces = q.getResultList();
			return commerces;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	
	}

}
