package com.returnsoft.collection.eao.impl;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
//import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//import javax.transaction.UserTransaction;
import javax.transaction.UserTransaction;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;
@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class LoteEaoImpl implements LoteEao {

	@PersistenceContext
	private EntityManager em;
	
	//@Resource
	//private UserTransaction ut;
	
	//@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void add(Lote lote) throws EaoException{
		try {
			
			//ut.begin();
			em.persist(lote);
			em.flush();
			//ut.commit();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	//@Asynchronous
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Lote update(Lote lote) throws EaoException{
		try {
			
			
			System.out.println("EN EL UPDATE LOTE"+lote.getProcess());
			System.out.println("EN EL UPDATE LOTE"+lote.getProcess());
			System.out.println("EN EL UPDATE LOTE"+lote.getProcess());
			System.out.println("EN EL UPDATE LOTE"+lote.getProcess());
			System.out.println("EN EL UPDATE LOTE"+lote.getProcess());
			
			//ut.begin();
			lote = em.merge(lote);
			em.flush();
			//ut.commit();
			return lote;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
}
