package com.returnsoft.collection.eao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.EaoException;

@Stateless
public class SaleEaoImpl implements SaleEao {
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(Sale sale) throws EaoException{
		try {
			
			Long newCorrelative = generateNewCorrelative(sale.getProduct().getId(),sale.getBank().getId());
			String code = newCorrelative.toString(); 
			while (code.length()<6) {
				code="0"+code;
			}
			code = sale.getBank().getCode()+sale.getProduct().getCode()+code;
			sale.setCode(code);
			em.persist(sale);
			em.flush();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
		}
	}
	
	//public Long getMaxSaleIdByProduct(Short productId) throws EaoException{
		public Long generateNewCorrelative(Short productId, Short bankId) throws EaoException{
		try {
			
			String query = "SELECT max(cast(substring(s.code,5) as signed)) "
					+ "FROM Sale s "
					+ "left join s.product p "
					+ "left join s.bank b "
					+ "WHERE p.id=:productId "
					+ " and b.id=:bankId";
			
			Query q = em.createQuery(query);
			q.setParameter("productId", productId);
			q.setParameter("bankId", bankId);

			Long maxCorrelative = (Long)q.getSingleResult();
			
			Long newCorrelative;
			
			if (maxCorrelative!=null && maxCorrelative>0) {
				newCorrelative = maxCorrelative+1;
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
	
	
	public Sale update(Sale sale) throws EaoException{
		try {
			
			sale = em.merge(sale);
			
			em.flush();
			
			return sale;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	public List<Sale> findForNotifications(Date saleDateStartedAt,Date saleDateEndedAt, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification)  throws EaoException {
		try {
			
			String query = "SELECT s FROM Sale s "
					+ "left join fetch s.saleState ss "
					+ "left join s.commerce c "
					+ "left join fetch s.payer p "
					+ "left join c.bank b "
					+ "left join s.notification n "
					+ "WHERE s.dateOfSale between :saleDateStartedAt and :saleDateEndedAt ";
			
			if (sendingDate!=null) {
				query+=" and n.sendingAt between :sendingDateStart and  :sendingDateEnd";
			}
			if (bankId!=null ) {
				query+=" and b.id = :bankId ";
			}
			
			if (saleState!=null) {
				query+=" and ss.state = :saleState ";
			}
			
			if (notificationType!=null) {
				query+=" and n.type = :notificationType ";
			}
			
			if (notificationStates!=null && notificationStates.size()>0) {
				query+=" and n.state in :notificationStates ";
			}
			
			if (withoutAddress) {
				query+=" and (p.address is not null and p.address <> '') ";
			}
			
			if (withoutMail) {
				query+=" and (p.mail is not null and p.mail <> '') ";
			}
			
			if (withoutNotification) {
				query+=" and n.id is null ";
			}
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("saleDateStartedAt", saleDateStartedAt);
			q.setParameter("saleDateEndedAt", saleDateEndedAt);
			
			if (sendingDate!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("sendingDateStart", sdf2.parse(sdf.format(sendingDate)+" 00:00:00"));
				q.setParameter("sendingDateEnd", sdf2.parse(sdf.format(sendingDate)+" 23:59:59"));
			}
			
			
			if (notificationStates!=null && notificationStates.size()>0) {
				q.setParameter("notificationStates", notificationStates);
			}
			
			if (saleState!=null) {
				q.setParameter("saleState", saleState);
			}
			
			if (notificationType!=null) {
				q.setParameter("notificationType", notificationType);
			}
			
			if (bankId!=null ) {
				q.setParameter("bankId", bankId);
			}
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	public List<Sale> findBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState)  throws EaoException {
		try {
			
			String query = "SELECT s FROM Sale s "
					+ "left join s.product p "
					+ "left join s.bank b "
					+ "left join fetch s.payer pa "
					+ "left join fetch s.creditCard cc "
					+ "left join fetch s.saleState ss "
					+ "WHERE s.date between :saleDateStartedAt and :saleDateEndedAt ";
			
			if (bankId!=null && bankId>0) {
				query+=" and b.id=:bankId ";
			}
			
			if (productId!=null && productId>0) {
				query+=" and p.id=:productId ";
			}
			
			if (saleState!=null) {
				query+=" and ss.state = :saleState ";
			}
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("saleDateStartedAt", saleDateStartedAt);
			q.setParameter("saleDateEndedAt", saleDateEndedAt);
			
			if (bankId!=null && bankId>0) {
				q.setParameter("bankId", bankId);
			}
			
			if (productId!=null && productId>0) {
				q.setParameter("productId", productId);
			}
			
			/*if (affiliationDate!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 00:00:00"));
				q.setParameter("affiliationDateStart", sdf2.parse(sdf.format(affiliationDate)+" 00:00:00"));
				System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				q.setParameter("affiliationDateEnd", sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				
			}*/
			
			if (saleState!=null) {
				q.setParameter("saleState", saleState);
			}
			
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public Sale findByCode(String code) throws EaoException{
		try {
			
			System.out.println("ingreso a findByCode EAOImpl");
			
			String query = "SELECT s FROM Sale s WHERE s.code = :code ";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("code", code);
			
			Sale sale = q.getSingleResult();
			return sale;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	/*public List<Sale> getNotConditioned() throws EaoException {
		
		try {
			
			String query = "SELECT s FROM Sale s "
					+ "left join s.notification n "
					+ "left join s.saleState ss "
					+ "WHERE (n.id is null or n.state = :notificationState) "
					+ " and s.virtualNotifications<3 and ss.state = :saleState";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("notificationState", NotificationStateEnum.SENDING);
			q.setParameter("saleState", SaleStateEnum.ACTIVE);
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}*/
	
	
	
	public Sale findById(Long id) throws EaoException{
		try {
			
			Sale sale = em.find(Sale.class, id);
			
			return sale;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	//public Long findIdByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws EaoException{
	public Boolean checkExistSale(Integer nuicInsured, Date dateOfSale, Short bankId, Short productId, Short collectionPeriodId) throws EaoException{
		
		try {
			
			String query = "SELECT s.id FROM "
					+ "Sale s "
					+ "left join s.bank b "
					+ "left join s.product p "
					+ "left join s.collectionPeriod cp "
					+ "WHERE s.nuicInsured = :nuicInsured "
					+ "and s.date = :dateOfSale "
					+ "and p.id  = :productId "
					+ "and b.id = :bankId "
					+ "and cp.id = :collectionPeriodId ";
			
			TypedQuery<Long> q = em.createQuery(query, Long.class);
			q.setParameter("nuicInsured", nuicInsured);
			q.setParameter("dateOfSale", dateOfSale);
			q.setParameter("productId", productId);
			q.setParameter("bankId", bankId);
			q.setParameter("collectionPeriodId", collectionPeriodId);
			
			Long saleId = (Long)q.getSingleResult();
			
			if (saleId!=null && saleId>0) {
				return true;
			}else{
				return false;
			}
			
		} catch (NoResultException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	
	
	public List<Sale> findByNuicResponsible(Long nuicResponsible) throws EaoException {
		try {
			
			String query = "SELECT s FROM Sale s left join s.payer p WHERE p.nuicResponsible = :nuicResponsible ";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("nuicResponsible", nuicResponsible);
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	public List<Sale> findByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws EaoException{
		try {
			
			System.out.println("Ingreso a buscar findByNamesResponsible");
			
			String query = "SELECT s FROM Sale s left join s.payer p WHERE s.id > 0 ";
			
			if (nuicResponsible!=null && nuicResponsible>0) {
				query+=" and p.nuicResponsible = :nuicResponsible ";
			}
			if (firstnameResponsible!=null && firstnameResponsible.length()>0) {
				query+=" and p.firstnameResponsible = :firstnameResponsible ";
			}
			if (lastnamePaternalResponsible!=null && lastnamePaternalResponsible.length()>0) {
				query+=" and p.lastnamePaternalResponsible = :lastnamePaternalResponsible ";
			}
			if (lastnameMaternalResponsible!=null && lastnameMaternalResponsible.length()>0) {
				query+=" and p.lastnameMaternalResponsible = :lastnameMaternalResponsible ";
			}
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			
			if (nuicResponsible!=null && nuicResponsible>0) {
				q.setParameter("nuicResponsible", nuicResponsible);
			}
			if (firstnameResponsible!=null && firstnameResponsible.length()>0) {
				q.setParameter("firstnameResponsible", firstnameResponsible);
			}
			if (lastnamePaternalResponsible!=null && lastnamePaternalResponsible.length()>0) {
				q.setParameter("lastnamePaternalResponsible", lastnamePaternalResponsible);
			}
			if (lastnameMaternalResponsible!=null && lastnameMaternalResponsible.length()>0) {
				q.setParameter("lastnameMaternalResponsible", lastnameMaternalResponsible);
			}
			
			List<Sale> sales = q.getResultList();
			
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	public List<Sale> findByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws EaoException{
		try {
			
			String query = "SELECT s FROM Sale s where s.id > 0 ";
			
			if (nuicInsured!=null && nuicInsured>0) {
				query+=" and s.nuicInsured = :nuicInsured ";
			}
			if (firstnameInsured!=null && firstnameInsured.length()>0) {
				query+=" and s.firstnameInsured = :firstnameInsured ";
			}
			if (lastnamePaternalInsured!=null && lastnamePaternalInsured.length()>0) {
				query+=" and s.lastnamePaternalInsured = :lastnamePaternalInsured ";
			}
			if (lastnameMaternalInsured!=null && lastnameMaternalInsured.length()>0) {
				query+=" and s.lastnameMaternalInsured = :lastnameMaternalInsured ";
			}
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			
			if (nuicInsured!=null && nuicInsured>0) {
				q.setParameter("nuicInsured", nuicInsured);
			}
			if (firstnameInsured!=null && firstnameInsured.length()>0) {
				q.setParameter("firstnameInsured", firstnameInsured);
			}
			if (lastnamePaternalInsured!=null && lastnamePaternalInsured.length()>0) {
				q.setParameter("lastnamePaternalInsured", lastnamePaternalInsured);
			}
			if (lastnameMaternalInsured!=null && lastnameMaternalInsured.length()>0) {
				q.setParameter("lastnameMaternalInsured", lastnameMaternalInsured);
			}
			
			List<Sale> sales = q.getResultList();
			
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	public List<Sale> findByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws EaoException{
		try {
			
			String query = "SELECT s FROM Sale s where s.id > 0 ";
			
			if (nuicContractor!=null && nuicContractor>0) {
				query+=" and s.nuicContractor = :nuicContractor ";
			}
			if (firstnameContractor!=null && firstnameContractor.length()>0) {
				query+=" and s.firstnameContractor = :firstnameContractor ";
			}
			if (lastnamePaternalContractor!=null && lastnamePaternalContractor.length()>0) {
				query+=" and s.lastnamePaternalContractor = :lastnamePaternalContractor ";
			}
			if (lastnameMaternalContractor!=null && lastnameMaternalContractor.length()>0) {
				query+=" and s.lastnameMaternalContractor = :lastnameMaternalContractor ";
			}
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			
			if (nuicContractor!=null && nuicContractor>0) {
				q.setParameter("nuicContractor", nuicContractor);
			}
			if (firstnameContractor!=null && firstnameContractor.length()>0) {
				q.setParameter("firstnameContractor", firstnameContractor);
			}
			if (lastnamePaternalContractor!=null && lastnamePaternalContractor.length()>0) {
				q.setParameter("lastnamePaternalContractor", lastnamePaternalContractor);
			}
			if (lastnameMaternalContractor!=null && lastnameMaternalContractor.length()>0) {
				q.setParameter("lastnameMaternalContractor", lastnameMaternalContractor);
			}
			
			List<Sale> sales = q.getResultList();
			
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	
	

}
