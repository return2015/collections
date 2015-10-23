package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.NotificationLimit1Exception;
import com.returnsoft.collection.exception.NotificationLimit2Exception;
// import com.returnsoft.collection.exception.NotificationLimitException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;

@Stateless
public class NotificationServiceImpl implements NotificationService {
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private NotificationEao notificationEao;
	
	public Sale findSaleById(Long id) throws ServiceException {
		try {

			Sale sale = saleEao.findById(id);

			return sale;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public void add(Notification notification) throws ServiceException {
		try {
			
			Sale saleFound = saleEao.findById(notification.getSale().getId());
			
			//Si tiene menos de 3 envios virtuales
			/*if (saleFound.getVirtualNotifications()<3) {
				if (saleFound.getPhysicalNotifications()>1) {
					//no se agrega porque tiene 2 envíos físicos y menos de 3 virtuales.
					throw new NotificationLimit2Exception(saleFound.getCode());
				}
			}else {
				if (saleFound.getPhysicalNotifications()>0) {
					//No se agrega porque ya tiene 1 envío físico y 3 envíos virtuales.
					throw new NotificationLimit1Exception(saleFound.getCode());
				}
			}*/
			
			//se agrega notification 
			notificationEao.add(notification);
			if (notification.getType().equals(NotificationTypeEnum.PHYSICAL)) {
				saleFound.setPhysicalNotifications((short)(saleFound.getPhysicalNotifications()+1));	
			}else if (notification.getType().equals(NotificationTypeEnum.MAIL)) {
				saleFound.setVirtualNotifications((short)(saleFound.getVirtualNotifications()+1));
			}
			
			saleFound.setNotification(notification);
			saleFound = saleEao.update(saleFound);
			

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public List<Notification> findNotificationsByData(Date sendingAt,Date createdAt,NotificationTypeEnum notificationType, NotificationStateEnum notificationState) throws ServiceException{
		try {
			List<Notification> notifications = notificationEao.findByData(sendingAt, createdAt,  notificationType, notificationState);

			return notifications;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
		
	}
	
	public Notification findById(Integer notificationId) throws ServiceException {
		try {

			Notification notification = notificationEao.findById(notificationId);

			return notification;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public Notification update(Notification notification) throws ServiceException {

		try {
			notification = notificationEao.update(notification);
			
			return notification;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	

}
