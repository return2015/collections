package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.MaintenanceEao;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class MaintenanceEaoImpl implements MaintenanceEao {
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(SaleState maintenance) throws EaoException{
		try {
			
			em.persist(maintenance);
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	
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
