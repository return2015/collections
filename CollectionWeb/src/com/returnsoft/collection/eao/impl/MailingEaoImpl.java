package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.MailingEao;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class MailingEaoImpl  implements MailingEao{
	
	@PersistenceContext
	private EntityManager em;
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Notification> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT m FROM Mailing m left join m.sale s WHERE s.id = :saleId ";
			
			TypedQuery<Notification> q = em.createQuery(query, Notification.class);
			q.setParameter("saleId", saleId);
			
			List<Notification> mailings = q.getResultList();
			return mailings;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	

}
