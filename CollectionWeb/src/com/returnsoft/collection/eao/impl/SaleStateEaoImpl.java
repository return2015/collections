package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class SaleStateEaoImpl implements SaleStateEao {
	
	@PersistenceContext
	private EntityManager em;
	
	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SaleState update(SaleState saleState) throws EaoException{
		try {
			
//			System.out.println("-----------------------");
//			System.out.println(maintenance.getChannel());
//			System.out.println(maintenance.getObservation());
//			System.out.println(maintenance.getReason());
//			System.out.println(maintenance.getDate());
//			System.out.println(maintenance.getState().getName());
//			System.out.println(maintenance.getUser());
//			System.out.println("-----------------------");
			
			saleState = em.merge(saleState);
			em.flush();
			return saleState;
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<SaleState> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT ss FROM SaleState ss left join ss.sale s WHERE s.id = :saleId ";
			
			TypedQuery<SaleState> q = em.createQuery(query, SaleState.class);
			q.setParameter("saleId", saleId);
			
			List<SaleState> maintenances = q.getResultList();
			return maintenances;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	

}
