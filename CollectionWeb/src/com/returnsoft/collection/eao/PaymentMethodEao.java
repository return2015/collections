package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.PaymentMethodEao;
import com.returnsoft.collection.entity.PaymentMethod;
import com.returnsoft.collection.exception.EaoException;
@Stateless
public class PaymentMethodEao  {
	
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<PaymentMethod> getAll() throws EaoException {
		try {

			TypedQuery<PaymentMethod> q = em.createQuery(
					"SELECT pm FROM PaymentMethod pm", PaymentMethod.class);
			List<PaymentMethod> paymentMethods = q.getResultList();

			return paymentMethods;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	
	public PaymentMethod ckeckIfExist(String code) throws EaoException {
		try {
			String query = "Select p from PaymentMethod p where p.code=:code";
			TypedQuery<PaymentMethod> q = em.createQuery(query,PaymentMethod.class);
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
