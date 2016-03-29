package com.returnsoft.collection.eao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
	public Long checkExists(String saleCode, BigDecimal returnedAmount) throws EaoException{
		try {
			
			String query = "SELECT r.id FROM Repayment r "
					+ "left join r.sale s "
					+ "WHERE r.returnedAmount = :returnedAmount and s.code=:saleCode";
			
			Query q = em.createQuery(query);
			q.setParameter("saleCode", saleCode);
			q.setParameter("returnedAmount", returnedAmount);
			
			Long repaymentId = (Long)q.getSingleResult();
			return repaymentId;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	public List<Repayment> findList(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber) throws EaoException{
		try {
			
			
			
			String query = 
					"SELECT c FROM Repayment c "
					+ " left join fetch c.sale s "
					+ " left join fetch c.sale.payer pa "
					+ " left join fetch c.sale.saleState ss "
					+ " left join fetch c.sale.creditCard cc " 
					+ " left join fetch c.sale.bank b "
					+ " left join fetch c.sale.product p "
					+ " left join fetch c.createdBy u "
					+ " WHERE c.id>0 ";
			
			if (paymentDate!=null ) {
				query+=" and c.paymentDate =:paymentDate ";
			}
			
			if (returnedDate!=null ) {
				query+=" and c.returnedDate =:returnedDate ";
			}
			
			if (bankId!=null ) {
				query+=" and b.id =:bankId ";
			}
			if (productId!=null ) {
				query+=" and p.id =:productId ";
			}
			if (documentNumber!=null && documentNumber>0) {
				query+=" and pa.nuicResponsible =:documentNumber ";
			}
			
			//System.out.println("QUERY1:"+query);
			
			TypedQuery<Repayment> q = em.createQuery(query, Repayment.class);
			
			if (paymentDate!=null ) {
				q.setParameter("paymentDate", paymentDate);
			}
			
			if (returnedDate!=null ) {
				q.setParameter("returnedDate", returnedDate);
			}
			
			if (bankId!=null ) {
				q.setParameter("bankId", bankId);
			}
			if (productId!=null ) {
				q.setParameter("productId", productId);
			}
			if (documentNumber!=null && documentNumber>0) {
				q.setParameter("documentNumber", documentNumber);
			}
			
			//System.out.println("QUERY2:"+query);
			
			return q.getResultList();
			
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	
	public List<Repayment> findLimit(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber, Integer first, Integer limit) throws EaoException{
		try {
			
			String query = 
					"SELECT c FROM Repayment c "
							+ " left join fetch c.sale s "
							+ " left join fetch c.sale.payer pa "
							+ " left join fetch c.sale.saleState ss "
							+ " left join fetch c.sale.creditCard cc " 
							+ " left join fetch c.sale.bank b "
							+ " left join fetch c.sale.product p "
							+ " left join fetch c.createdBy u "
					+ "WHERE c.id>0 ";
			
			if (paymentDate!=null ) {
				query+=" and c.paymentDate =:paymentDate ";
			}
			
			if (returnedDate!=null ) {
				query+=" and c.returnedDate =:returnedDate ";
			}
			
			if (bankId!=null ) {
				query+=" and b.id =:bankId ";
			}
			if (productId!=null ) {
				query+=" and p.id =:productId ";
			}
			if (documentNumber!=null && documentNumber>0) {
				query+=" and pa.nuicResponsible =:documentNumber ";
			}
			
			TypedQuery<Repayment> q = em.createQuery(query, Repayment.class);
			
			if (paymentDate!=null ) {
				q.setParameter("paymentDate", paymentDate);
			}
			
			if (returnedDate!=null ) {
				q.setParameter("returnedDate", returnedDate);
			}
			
			if (bankId!=null ) {
				q.setParameter("bankId", bankId);
			}
			if (productId!=null ) {
				q.setParameter("productId", productId);
			}
			if (documentNumber!=null && documentNumber>0) {
				q.setParameter("documentNumber", documentNumber);
			}
			
			q.setFirstResult(first);
			q.setMaxResults(limit);

			return q.getResultList();
			
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	public Long findCount(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber) throws EaoException {
		
		
		
		try {
			String query = 
					"SELECT count(c.id) FROM Repayment c "
					+ " left join c.sale s "
							+ " left join s.bank b "
							+ " left join s.product p "
							+ " left join s.payer pa "
					+ "WHERE c.id>0 ";
			
			if (paymentDate!=null ) {
				query+=" and c.paymentDate =:paymentDate ";
			}
			
			if (returnedDate!=null ) {
				query+=" and c.returnedDate =:returnedDate ";
			}
			
			if (bankId!=null ) {
				query+=" and b.id =:bankId ";
			}
			if (productId!=null ) {
				query+=" and p.id =:productId ";
			}
			if (documentNumber!=null && documentNumber>0) {
				query+=" and pa.nuicResponsible =:documentNumber ";
			}
			
			Query q = em.createQuery(query);
			
			if (paymentDate!=null ) {
				q.setParameter("paymentDate", paymentDate);
			}
			
			if (returnedDate!=null ) {
				q.setParameter("returnedDate", returnedDate);
			}
			
			if (bankId!=null ) {
				q.setParameter("bankId", bankId);
			}
			if (productId!=null ) {
				q.setParameter("productId", productId);
			}
			if (documentNumber!=null && documentNumber>0) {
				q.setParameter("documentNumber", documentNumber);
			}
			
			return (Long)q.getSingleResult();
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}
	

}
