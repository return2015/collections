package com.returnsoft.collection.eao;

import java.text.SimpleDateFormat;
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

import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.EaoException;

@Stateless
//@TransactionManagement(TransactionManagementType.CONTAINER)
public class NotificationEao {
	
	@PersistenceContext
	private EntityManager em;
	
	//@TransactionAttribute(TransactionAttributeType.MANDATORY)
	//@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void add(Notification notification) throws EaoException{
		
		try {
			
			em.persist(notification);
			em.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Notification> findBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT n FROM Notification n left join n.sale s WHERE s.id = :saleId ";
			
			TypedQuery<Notification> q = em.createQuery(query, Notification.class);
			q.setParameter("saleId", saleId);
			
			List<Notification> notifications = q.getResultList();
			return notifications;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Notification> findByData(Date sendingAt,Date createdAt, NotificationTypeEnum notificationType, NotificationStateEnum notificationState) throws EaoException{
		try {
			
			String query = "SELECT n FROM Notification n "
					+ "WHERE n.id>0 ";
			
			if (sendingAt!=null) {
				query+=" and n.sendingAt between :sendingAtStart and  :sendingAtEnd";
			}
			
			if (createdAt!=null) {
				query+=" and n.createdAt between :createdAtStart and  :createdAtEnd";
			}
			
			if (notificationState!=null) {
				query+=" and n.notificationState = :notificationState ";
			}
			
			if (notificationType!=null) {
				query+=" and n.notificationType = :notificationType ";
			}
			
			TypedQuery<Notification> q = em.createQuery(query, Notification.class);
			
			if (sendingAt!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//System.out.println(sdf2.parse(sdf.format(sendingAt)+" 00:00:00"));
				q.setParameter("sendingAtStart", sdf2.parse(sdf.format(sendingAt)+" 00:00:00"));
				//System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				q.setParameter("sendingAtEnd", sdf2.parse(sdf.format(sendingAt)+" 23:59:59"));
				
			}
			
			if (createdAt!=null ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//System.out.println(sdf2.parse(sdf.format(sendingAt)+" 00:00:00"));
				q.setParameter("createdAtStart", sdf2.parse(sdf.format(createdAt)+" 00:00:00"));
				//System.out.println(sdf2.parse(sdf.format(affiliationDate)+" 23:59:59"));
				q.setParameter("createdAtEnd", sdf2.parse(sdf.format(createdAt)+" 23:59:59"));
				
			}
			
			if (notificationState!=null) {
				q.setParameter("notificationState", notificationState);
			}
			
			if (notificationType!=null) {
				q.setParameter("notificationType", notificationType);
			}
			
			List<Notification> notifications = q.getResultList();
			return notifications;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
	}
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Notification findById(Integer notificationId) throws EaoException{
		try {
			
			Notification notification = em.find(Notification.class, notificationId);
			
			return notification;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}
	
	/*public Notification findLastBySaleId(Long saleId) throws EaoException{
		try {
			
			String query = "SELECT n FROM Notification n left join n.sale s WHERE s.id = :saleId order by n.createdAt desc";
			
			TypedQuery<Notification> q = em.createQuery(query, Notification.class);
			q.setParameter("saleId", saleId);
			q.setMaxResults(1);
			
			return q.getSingleResult();

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}
		
	}*/
	
	
	public Notification update(Notification notification) throws EaoException{
		try {

			Notification newNotification = em.merge(notification);
			em.flush();
			
			return newNotification;

		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	public Boolean verifyIfExist(Long nuicResponsible, String orderNumber) throws EaoException {
		try {
			
			String query ="select n.id from Notification n "
					+ "left join n.sale s "
					+ "left join s.payer p where n.type=:type and n.orderNumber=:orderNumber and p.nuicResponsible=:nuicResponsible";
			
			
			Query q = em.createQuery(query);
			q.setParameter("orderNumber", orderNumber);
			q.setParameter("nuicResponsible", nuicResponsible);
			q.setParameter("type", NotificationTypeEnum.PHYSICAL);
			
			Integer notificationId = (Integer)q.getSingleResult();
			
			if (notificationId!=null && notificationId>0) {
				return true;
			}else{
				return false;
			}
		} catch (NoResultException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	
	public Notification findByKey(Long nuicResponsible, String orderNumber) throws EaoException {
		try {
			
			String query ="select n from Notification n "
					+ "left join n.sale s "
					+ "left join s.payer p where n.type=:type and n.orderNumber=:orderNumber and p.nuicResponsible=:nuicResponsible";
			
			
			TypedQuery<Notification> q = em.createQuery(query,Notification.class);
			q.setParameter("orderNumber", orderNumber);
			q.setParameter("nuicResponsible", nuicResponsible);
			q.setParameter("type", NotificationTypeEnum.PHYSICAL);
			
			Notification notification = q.getSingleResult();
			
			return notification;
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e);
		}
	}
	
	public List<Sale> findBySale(Date saleDateStartedAt, Date saleDateEndedAt, Date sendingDate,
			List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState,
			NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress,
			Boolean withoutNotification, String orderNumber) throws EaoException {
		try {

			String query = "SELECT s FROM Sale s " 
			+ "left join fetch s.saleState ss "
			+ "left join fetch s.payer p " 
			+ "left join fetch s.creditCard cc " 
			+ "left join s.bank b "
			+ "left join s.notification n " 
			+ "WHERE s.id>0 ";
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				query+=" and s.date between :saleDateStartedAt and :saleDateEndedAt ";
			}

			if (sendingDate != null) {
				query += " and n.sendingAt between :sendingDateStart and  :sendingDateEnd";
			}
			if (bankId != null) {
				query += " and b.id = :bankId ";
			}

			if (saleState != null) {
				query += " and ss.state = :saleState ";
			}

			if (notificationType != null) {
				query += " and n.type = :notificationType ";
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				query += " and n.state in :notificationStates ";
			}

			if (withoutAddress) {
				query += " and (p.address is not null and p.address <> '') ";
			}

			if (withoutMail) {
				query += " and (p.mail is not null and p.mail <> '') ";
			}

			if (withoutNotification) {
				query += " and n.id is null ";
			}
			if (orderNumber != null && orderNumber.length() > 0) {
				query += " and n.orderNumber=:orderNumber ";
			}

			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				q.setParameter("saleDateStartedAt", saleDateStartedAt);
				q.setParameter("saleDateEndedAt", saleDateEndedAt);	
			}

			if (sendingDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("sendingDateStart", sdf2.parse(sdf.format(sendingDate) + " 00:00:00"));
				q.setParameter("sendingDateEnd", sdf2.parse(sdf.format(sendingDate) + " 23:59:59"));
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				q.setParameter("notificationStates", notificationStates);
			}

			if (saleState != null) {
				q.setParameter("saleState", saleState);
			}

			if (notificationType != null) {
				q.setParameter("notificationType", notificationType);
			}

			if (bankId != null) {
				q.setParameter("bankId", bankId);
			}

			if (orderNumber != null && orderNumber.length() > 0) {
				q.setParameter("orderNumber", orderNumber);
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

	//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Sale> findBySaleLimit(Date saleDateStartedAt, Date saleDateEndedAt, Date sendingDate,
			List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState,
			NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress,
			Boolean withoutNotification, String orderNumber, Integer first, Integer limit) throws EaoException {
		try {

			String query = 
					"SELECT s FROM Sale s " 
					+ "left join fetch s.saleState ss "
					+ "left join fetch s.creditCard cc " 
					+ "left join fetch s.payer p "
					+ "left join fetch s.bank b " 
					+ "left join s.notification n "
					+ "WHERE s.id>0 ";
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				query+=" and s.date between :saleDateStartedAt and :saleDateEndedAt ";
			}
			
			if (sendingDate != null) {
				query += " and n.sendingAt between :sendingDateStart and  :sendingDateEnd";
			}
			if (bankId != null) {
				query += " and b.id = :bankId ";
			}

			if (saleState != null) {
				query += " and ss.state = :saleState ";
			}

			if (notificationType != null) {
				query += " and n.type = :notificationType ";
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				query += " and n.state in :notificationStates ";
			}

			if (withoutAddress) {
				query += " and (p.address is not null and p.address <> '') ";
			}

			if (withoutMail) {
				query += " and (p.mail is not null and p.mail <> '') ";
			}

			if (withoutNotification) {
				query += " and n.id is null ";
			}

			if (orderNumber != null && orderNumber.length() > 0) {
				query += " and n.orderNumber=:orderNumber ";
			}

			TypedQuery<Sale> q = em.createQuery(query, Sale.class);
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				q.setParameter("saleDateStartedAt", saleDateStartedAt);
				q.setParameter("saleDateEndedAt", saleDateEndedAt);	
			}

			if (sendingDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("sendingDateStart", sdf2.parse(sdf.format(sendingDate) + " 00:00:00"));
				q.setParameter("sendingDateEnd", sdf2.parse(sdf.format(sendingDate) + " 23:59:59"));
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				q.setParameter("notificationStates", notificationStates);
			}

			if (saleState != null) {
				q.setParameter("saleState", saleState);
			}

			if (notificationType != null) {
				q.setParameter("notificationType", notificationType);
			}

			if (bankId != null) {
				q.setParameter("bankId", bankId);
			}

			if (orderNumber != null && orderNumber.length() > 0) {
				q.setParameter("orderNumber", orderNumber);
			}

			q.setFirstResult(first);
			q.setMaxResults(limit);

			List<Sale> sales = q.getResultList();
			return sales;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}

	//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Long findBySaleCount(Date saleDateStartedAt, Date saleDateEndedAt, Date sendingDate,
			List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState,
			NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress,
			Boolean withoutNotification, String orderNumber) throws EaoException {
		try {

			String query = 
					"SELECT count(s.id) FROM Sale s " 
					+ "left join fetch s.saleState ss "
					+ "left join fetch s.creditCard cc " 
					+ "left join fetch s.payer p " 
					+ "left join s.bank b "
					+ "left join s.notification n " 
					+ "WHERE s.id>0 ";
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				query+=" and s.date between :saleDateStartedAt and :saleDateEndedAt ";
			}

			if (sendingDate != null) {
				query += " and n.sendingAt between :sendingDateStart and  :sendingDateEnd";
			}
			if (bankId != null) {
				query += " and b.id = :bankId ";
			}

			if (saleState != null) {
				query += " and ss.state = :saleState ";
			}

			if (notificationType != null) {
				query += " and n.type = :notificationType ";
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				query += " and n.state in :notificationStates ";
			}

			if (withoutAddress) {
				query += " and (p.address is not null and p.address <> '') ";
			}

			if (withoutMail) {
				query += " and (p.mail is not null and p.mail <> '') ";
			}

			if (withoutNotification) {
				query += " and n.id is null ";
			}

			if (orderNumber != null && orderNumber.length() > 0) {
				query += " and n.orderNumber=:orderNumber ";
			}

			Query q = em.createQuery(query);
			
			if (saleDateStartedAt!=null && saleDateEndedAt!=null) {
				q.setParameter("saleDateStartedAt", saleDateStartedAt);
				q.setParameter("saleDateEndedAt", saleDateEndedAt);	
			}

			if (sendingDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				q.setParameter("sendingDateStart", sdf2.parse(sdf.format(sendingDate) + " 00:00:00"));
				q.setParameter("sendingDateEnd", sdf2.parse(sdf.format(sendingDate) + " 23:59:59"));
			}

			if (notificationStates != null && notificationStates.size() > 0) {
				q.setParameter("notificationStates", notificationStates);
			}

			if (saleState != null) {
				q.setParameter("saleState", saleState);
			}

			if (notificationType != null) {
				q.setParameter("notificationType", notificationType);
			}

			if (bankId != null) {
				q.setParameter("bankId", bankId);
			}

			if (orderNumber != null && orderNumber.length() > 0) {
				q.setParameter("orderNumber", orderNumber);
			}

			Long salesCount = (Long) q.getSingleResult();
			return salesCount;

		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new EaoException(e.getMessage());
		}

	}
	
	

}
