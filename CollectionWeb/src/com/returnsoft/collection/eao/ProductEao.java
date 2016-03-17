package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class ProductEao {
	
	@PersistenceContext
	private EntityManager em;
	
	
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

	
	public Product ckeckIfExist(String code) throws EaoException {
		try {
			String query = "Select p from Product p where p.code=:code";
			TypedQuery<Product> q = em.createQuery(query,Product.class);
			q.setParameter("code", code);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

}
