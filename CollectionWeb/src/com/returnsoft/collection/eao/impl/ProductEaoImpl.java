package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class ProductEaoImpl implements ProductEao {
	
	@PersistenceContext
	private EntityManager em;
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Product> getProducts() throws EaoException {
		try {

			TypedQuery<Product> q = em.createQuery(
					"SELECT p FROM Product p", Product.class);
			List<Product> products = q.getResultList();

			return products;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

}
