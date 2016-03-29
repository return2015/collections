package com.returnsoft.collection.eao;

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

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class CollectionEao  {
	
	@PersistenceContext
	private EntityManager em;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void add(Collection collection) throws EaoException{
		try {
			
			Long newCorrelative = generateNewReceiptNumber(collection.getSale().getProduct().getId(),collection.getSale().getBank().getId());
			String receiptNumber = newCorrelative.toString();
			System.out.println("receiptNumber1:"+receiptNumber);
			while (receiptNumber.length() < 6) {
				receiptNumber = "0" + receiptNumber;
			}
			System.out.println("receiptNumber2:"+receiptNumber);
			receiptNumber = collection.getSale().getBank().getCode() + collection.getSale().getProduct().getCode() + receiptNumber;
			collection.setReceiptNumber(receiptNumber);
			em.persist(collection);
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	private Long generateNewReceiptNumber(Short productId, Short bankId) throws EaoException{
		try {
			
			String query = "SELECT max(cast(substring(c.receiptNumber,5) as signed)) "
					+ " FROM Collection c "
					+ " left join c.sale s "
					+ " left join s.product p "
					+ " left join s.bank b "
					+ "WHERE p.id=:productId and b.id=:bankId";
			
			Query q = em.createQuery(query);
			q.setParameter("productId", productId);
			q.setParameter("bankId", bankId);
			
			Long maxIdProduct= (Long)q.getSingleResult();
			
			Long newCorrelative;
			
//String receiptNumber = ""; 
			
			if (maxIdProduct!=null && maxIdProduct > 0) {
				newCorrelative =maxIdProduct+1;
			}else{
				newCorrelative = new Long(1);
			}
			
			
			return newCorrelative;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
	/*@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Integer findByResponseAndAuthorizationDay(CollectionResponseEnum responseMessage, Date authorizationDate, String saleCode) throws EaoException{
		
		try {
			
			String query = "SELECT count(c.id) FROM Collection c left join c.sale s "
					+ "WHERE s.code=:saleCode and c.responseMessage=:responseMessage "
					+ "and c.authorizationDate = :authorizationDate group by s.id";
			
			Query q = em.createQuery(query);
			q.setParameter("authorizationDate", authorizationDate);
			q.setParameter("responseMessage", responseMessage);
			q.setParameter("saleCode", saleCode);
			
			Long collectionsCount = (Long)q.getSingleResult();
			
			return collectionsCount.intValue();
			
		} catch (NoResultException e) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Integer findByResponseAndAuthorizationMonth(CollectionResponseEnum responseMessage,Date authorizationDate, String saleCode) throws EaoException{
		
		//System.out.println("findByResponseAndAuthorizationMonth.......................");
		
		try {
			
			String query = "SELECT count(c.id) FROM Collection c left join c.sale s "
					+ "WHERE s.code=:saleCode and c.responseMessage=:responseMessage "
					+ "and ( FUNC('MONTH',c.authorizationDate) = :authorizationDateMonth and FUNC('YEAR',c.authorizationDate) = :authorizationDateYear ) group by s.id";
			
			Query q = em.createQuery(query);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(authorizationDate);
			q.setParameter("authorizationDateMonth", calendar.get(Calendar.MONTH));
			q.setParameter("authorizationDateYear", calendar.get(Calendar.YEAR));
			q.setParameter("responseMessage", responseMessage);
			q.setParameter("saleCode", saleCode);
			
			Long collectionsCount = (Long)q.getSingleResult();
			
			return collectionsCount.intValue();
			
		} catch (NoResultException e) {
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}*/

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long checkExists(String saleCode, Date estimatedDate) throws EaoException{
		try {
			
			String query = "SELECT c.id FROM Collection c "
					+ "left join c.sale s "
					+ "WHERE c.estimatedDate = :estimatedDate  and s.code=:saleCode";
			
			Query q = em.createQuery(query);
			q.setParameter("saleCode", saleCode);
			q.setParameter("estimatedDate", estimatedDate);
			
			return (Long)q.getSingleResult();
			

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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
	
	public List<Collection> findList(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber) throws EaoException{
		try {
			
			
			
			String query = 
					"SELECT c FROM Collection c "
					+ " left join fetch c.paymentMethod pm "
					+ " left join fetch c.sale s "
					+ " left join fetch c.sale.payer pa "
					+ " left join fetch c.sale.saleState ss "
					+ " left join fetch c.sale.creditCard cc " 
					+ " left join fetch c.sale.bank b "
					+ " left join fetch c.sale.product p "
					+ " left join fetch c.createdBy u "
					+ " WHERE c.id>0 ";
			
			if (estimatedDate!=null ) {
				query+=" and c.estimatedDate =:estimatedDate ";
			}
			
			if (depositDate!=null ) {
				query+=" and c.depositDate =:depositDate ";
			}
			
			if (monthLiquidationDate!=null ) {
				query+=" and c.monthLiquidation =:monthLiquidationDate ";
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
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			
			if (estimatedDate!=null ) {
				q.setParameter("estimatedDate", estimatedDate);
			}
			
			if (depositDate!=null ) {
				q.setParameter("depositDate", depositDate);
			}
			
			if (monthLiquidationDate!=null ) {
				q.setParameter("monthLiquidationDate", monthLiquidationDate);
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
	
	
	public List<Collection> findLimit(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber, Integer first, Integer limit) throws EaoException{
		try {
			
			String query = 
					"SELECT c FROM Collection c "
							+ " left join fetch c.paymentMethod pm "
							+ " left join fetch c.sale s "
							+ " left join fetch c.sale.payer pa "
							+ " left join fetch c.sale.saleState ss "
							+ " left join fetch c.sale.creditCard cc " 
							+ " left join fetch c.sale.bank b "
							+ " left join fetch c.sale.product p "
							+ " left join fetch c.createdBy u "
							+ " left join fetch c.lote l "
					+ "WHERE c.id>0 ";
			
			if (estimatedDate!=null ) {
				query+=" and c.estimatedDate =:estimatedDate ";
			}
			
			if (depositDate!=null ) {
				query+=" and c.depositDate =:depositDate ";
			}
			
			if (monthLiquidationDate!=null ) {
				query+=" and c.monthLiquidation =:monthLiquidationDate ";
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
			
			TypedQuery<Collection> q = em.createQuery(query, Collection.class);
			
			if (estimatedDate!=null ) {
				q.setParameter("estimatedDate", estimatedDate);
			}
			
			if (depositDate!=null ) {
				q.setParameter("depositDate", depositDate);
			}
			
			if (monthLiquidationDate!=null ) {
				q.setParameter("monthLiquidationDate", monthLiquidationDate);
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
	
	public Long findCount(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber) throws EaoException {
		
		
		
		try {
			String query = 
					"SELECT count(c.id) FROM Collection c "
					+ " left join c.sale s "
							+ " left join s.bank b "
							+ " left join s.product p "
							+ " left join s.payer pa "
					+ "WHERE c.id>0 ";
			
			if (estimatedDate!=null ) {
				query+=" and c.estimatedDate =:estimatedDate ";
			}
			
			if (depositDate!=null ) {
				query+=" and c.depositDate =:depositDate ";
			}
			
			if (monthLiquidationDate!=null ) {
				query+=" and c.monthLiquidation =:monthLiquidationDate ";
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
			
			if (estimatedDate!=null ) {
				q.setParameter("estimatedDate", estimatedDate);
			}
			
			if (depositDate!=null ) {
				q.setParameter("depositDate", depositDate);
			}
			
			if (monthLiquidationDate!=null ) {
				q.setParameter("monthLiquidationDate", monthLiquidationDate);
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
