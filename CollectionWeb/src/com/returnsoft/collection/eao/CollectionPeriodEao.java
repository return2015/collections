package com.returnsoft.collection.eao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CollectionPeriodEao;
import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CollectionPeriodEao {
	
	@PersistenceContext
	private EntityManager em;
	
	public CollectionPeriod findById(Short collectionPeriodId) throws EaoException{
		try {
			
			CollectionPeriod collectionPeriod = em.find(CollectionPeriod.class, collectionPeriodId);
			
			return collectionPeriod;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}
	public List<CollectionPeriod> getAll() throws EaoException {
		try {

			TypedQuery<CollectionPeriod> q = em.createQuery(
					"SELECT cp FROM CollectionPeriod cp", CollectionPeriod.class);
			List<CollectionPeriod> collectionPeriods = q.getResultList();

			return collectionPeriods;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	public short ckeckIfExist(String name) throws EaoException {
		try {
			String query = "Select p.id from CollectionPeriod p where p.name=:name";
			Query q = em.createQuery(query);
			q.setParameter("name", name);
			return (short)q.getSingleResult();
		} catch (NoResultException e) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

}
