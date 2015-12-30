package com.returnsoft.collection.eao.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class BankEaoImpl implements BankEao {
	
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Bank> findByUserId(Integer userId) throws EaoException{
		try {
			TypedQuery<Bank> q = em.createQuery(
					"SELECT b FROM Bank b left join b.users u where u.id=:userId",
					Bank.class);
			q.setParameter("userId", userId);
			List<Bank> banks = q.getResultList();
			return banks;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Bank findById(Short bankId) throws EaoException{
		try {
			
			Bank bank = em.find(Bank.class, bankId);
			
			return bank;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Bank> getBanks() throws EaoException {
		try {

			TypedQuery<Bank> q = em.createQuery(
					"SELECT b FROM Bank b", Bank.class);
			List<Bank> banks = q.getResultList();

			return banks;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

}
