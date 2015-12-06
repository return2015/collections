package com.returnsoft.collection.eao.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CollectionEaoImpl implements CollectionEao {
	
	@PersistenceContext
	private EntityManager em;
	
	
	public void add(Collection collection) throws EaoException{
		try {
			
			String maxIdByProduct = getMaxCollectionIdByProduct(collection.getSale().getProduct().getId());
			
			String receiptNumber = ""; 
			
			if (maxIdByProduct!=null && maxIdByProduct.length() > 0) {
				Long maxIdByProductLong = Long.parseLong(maxIdByProduct);
				receiptNumber += (maxIdByProductLong+1);
			}else{
				receiptNumber += 1; 
			}
			
			collection.setReceiptNumber(receiptNumber);
			em.persist(collection);
			
			em.flush();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	
	
	
	public String getMaxCollectionIdByProduct(Short productId) throws EaoException{
		try {
			
			String query = "SELECT max(cast(c.receiptNumber as signed)) FROM Collection c "
					+ " left join c.sale s "
					+ " left join s.product p "
					+ "WHERE p.id=:productId ";
			
			Query q = em.createQuery(query);
			q.setParameter("productId", productId);
			
			String maxIdProduct= (String)q.getSingleResult();
			
			return maxIdProduct;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	
	public List<Collection> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT c FROM Collection c left join c.sale s WHERE s.id = :saleId ";
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			q.setParameter("saleId", saleId);
			
			List<Collection> collections = q.getResultList();
			return collections;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public List<Collection> findAllowsBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT c FROM Collection c left join c.sale s "
					+ "WHERE c.responseMessage=:responseMessage s.id = :saleId ";
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			q.setParameter("saleId", saleId);
			q.setParameter("responseMessage", CollectionResponseEnum.ALLOW);
			
			List<Collection> collections = q.getResultList();
			return collections;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	
	public List<Collection> findByResponseAndAuthorizationDay(CollectionResponseEnum responseMessage, Date authorizationDate, String saleCode) throws EaoException{
		
		try {
			
			String query = "SELECT c FROM Collection c left join c.sale s "
					+ "WHERE s.code=:saleCode and c.responseMessage=:responseMessage "
					+ "and c.authorizationDate = :authorizationDate ";
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			q.setParameter("authorizationDate", authorizationDate);
			q.setParameter("responseMessage", responseMessage);
			q.setParameter("saleCode", saleCode);
			
			List<Collection> collections = q.getResultList();
			
			return collections;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	public List<Collection> findByResponseAndAuthorizationMonth(CollectionResponseEnum responseMessage,Date authorizationDate, String saleCode) throws EaoException{
		
		//System.out.println("findByResponseAndAuthorizationMonth.......................");
		
		try {
			
			String query = "SELECT c FROM Collection c left join c.sale s "
					+ "WHERE s.code=:saleCode and c.responseMessage=:responseMessage "
					+ "and ( FUNC('MONTH',c.authorizationDate) = :authorizationDateMonth and FUNC('YEAR',c.authorizationDate) = :authorizationDateYear ) ";
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(authorizationDate);
			q.setParameter("authorizationDateMonth", calendar.get(Calendar.MONTH));
			q.setParameter("authorizationDateYear", calendar.get(Calendar.YEAR));
			q.setParameter("responseMessage", responseMessage);
			q.setParameter("saleCode", saleCode);
			
			List<Collection> collections = q.getResultList();
			
			return collections;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}

	
	
	public Collection findByReceiptNumber(String receiptNumber) throws EaoException{
		try {
			
			String query = "SELECT c FROM Collection c WHERE c.receiptNumber = :receiptNumber ";
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			q.setParameter("receiptNumber", receiptNumber);
			
			Collection collection = q.getSingleResult();
			return collection;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}

}
