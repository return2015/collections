package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CommerceEaoImpl implements CommerceEao {
	
	@PersistenceContext
	private EntityManager em;
	
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
