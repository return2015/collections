package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.exception.EaoException;
@Stateless
public class RepaymentEao  {
	
	@PersistenceContext
	private EntityManager em;
	
	
	public void add(Repayment repayment) throws EaoException{
		try {
			
			
			em.persist(repayment);
			
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		
			throw new EaoException(e);
		}
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Repayment> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT r FROM Repayment r left join r.sale s WHERE s.id = :saleId ";
			
			TypedQuery<Repayment> q = em.createQuery(query, Repayment.class);
			q.setParameter("saleId", saleId);
			
			List<Repayment> repayments = q.getResultList();
			return repayments;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws EaoException{
		try {
			
			String query = "SELECT r FROM Repayment r left join r.sale s WHERE s.id = :saleId and r.returnedDate=:returnedDate ";
			
			TypedQuery<Repayment> q = em.createQuery(query, Repayment.class);
			q.setParameter("saleId", saleId);
			q.setParameter("returnedDate", returnedDate);
			
			Repayment repayment = q.getSingleResult();
			return repayment;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	

}
