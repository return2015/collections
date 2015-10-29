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
//import org.eclipse.persistence.exceptions.IntegrityException;

@Stateless
public class SaleEaoImpl implements SaleEao {
	
	@PersistenceContext
	private EntityManager em;
	
	public void add(Sale sale) throws EaoException{
		try {
			
			Long maxIdByProduct = getMaxSaleIdByProduct(sale.getCommerce().getProduct().getId());
			
			String code = sale.getCommerce().getCode()+""+sale.getCommerce().getProduct().getCode()+""+sale.getCommerce().getPaymentMethod().getCode(); 
			
			if (maxIdByProduct!=null && maxIdByProduct>0) {
				//Long maxIdByProductLong = Long.parseLong(maxIdByProduct);
				code += (maxIdByProduct+1);
			}else{
				code += 1; 
			}
			
			sale.setCode(code);

			em.persist(sale);
			
			em.flush();
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			throw new EaoException(e);
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
	
	public List<Sale> findBySaleData2(Date saleDateStartedAt,Date saleDateEndedAt, Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType)  throws EaoException {
		try {
			
			String query = "SELECT s FROM Sale s "
					+ "left join s.saleState ss "
					+ "left join s.commerce c "
					+ "left join c.bank b "
					+ "left join s.notification n "
					+ "WHERE s.dateOfSale between :saleDateStartedAt and :saleDateEndedAt ";
			
			if (affiliationDate!=null) {
				query+=" and s.affiliationDate between :affiliationDateStart and  :affiliationDateEnd";
			}
			
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
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("saleDateStartedAt", saleDateStartedAt);
			q.setParameter("saleDateEndedAt", saleDateEndedAt);
			
			if (sendingDate!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("sendingDateStart", sdf2.parse(sdf.format(sendingDate)+" 00:00:00"));
				q.setParameter("sendingDateEnd", sdf2.parse(sdf.format(sendingDate)+" 23:59:59"));
			}
			
			if (affiliationDate!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("affiliationDateStart", sdf2.parse(sdf.format(affiliationDate)+" 00:00:00"));
				q.setParameter("affiliationDateEnd", sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
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
	
	
	public List<Sale> findBySaleData(Date saleDateStartedAt,Date saleDateEndedAt, Date affiliationDate,Short bankId, Short productId, SaleStateEnum saleState)  throws EaoException {
		try {
			
			String query = "SELECT s FROM Sale s "
					+ "left join s.commerce c "
					+ "left join c.product p "
					+ "left join c.bank b "
					+ "left join s.saleState ss "
					+ "WHERE s.dateOfSale between :saleDateStartedAt and :saleDateEndedAt ";
			
			if (bankId!=null && bankId>0) {
				query+=" and b.id=:bankId ";
			}
			
			if (productId!=null && productId>0) {
				query+=" and p.id=:productId ";
			}
			
			if (affiliationDate!=null) {
				query+=" and s.affiliationDate between :affiliationDateStart and  :affiliationDateEnd";
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
			
			if (affiliationDate!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 00:00:00"));
				q.setParameter("affiliationDateStart", sdf2.parse(sdf.format(affiliationDate)+" 00:00:00"));
				System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				q.setParameter("affiliationDateEnd", sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				
			}
			
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
	
	public List<Sale> getNotConditioned() throws EaoException {
		
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

	}
	
	
	
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
	
	
	public Sale findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws EaoException{
		
		try {
			
			String query = "SELECT s FROM Sale s WHERE s.nuicInsured = :nuicInsured and s.dateOfSale = :dateOfSale ";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("nuicInsured", nuicInsured);
			q.setParameter("dateOfSale", dateOfSale);
			
			Sale sale = q.getSingleResult();
			
			return sale;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	
	/*public List<Sale> findByCreditCardNumber(Long creditCardNumber) throws EaoException{
		try {
			
			String query = "SELECT s FROM Sale s "
					+" left join s.creditCard cd "
					+ "WHERE cd.number = :creditCardNumber ";
			
			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			q.setParameter("creditCardNumber", creditCardNumber);
			
			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}*/
	
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
			
			String query = "SELECT s FROM Sale s WHERE s.id > 0 ";
			
			if (nuicResponsible!=null && nuicResponsible>0) {
				query+=" and s.nuicResponsible = :nuicResponsible ";
			}
			if (firstnameResponsible!=null && firstnameResponsible.length()>0) {
				query+=" and s.firstnameResponsible = :firstnameResponsible ";
			}
			if (lastnamePaternalResponsible!=null && lastnamePaternalResponsible.length()>0) {
				query+=" and s.lastnamePaternalResponsible = :lastnamePaternalResponsible ";
			}
			if (lastnameMaternalResponsible!=null && lastnameMaternalResponsible.length()>0) {
				query+=" and s.lastnameMaternalResponsible = :lastnameMaternalResponsible ";
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
	
	public Long getMaxSaleIdByProduct(Short productId) throws EaoException{
		try {
			
			String query = "SELECT max(cast(substring(s.code,15) as signed)) FROM Sale s "
					+ "left join s.commerce c "
					+ "left join c.product p "
					+ "WHERE p.id=:productId ";
			
			Query q = em.createQuery(query);
			q.setParameter("productId", productId);
			
			Long maxIdProduct= (Long)q.getSingleResult();
			
			return maxIdProduct;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	

}
