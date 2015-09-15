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
import com.returnsoft.collection.exception.NotificationLimitException;
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
			
			//Si ya tiene 3 envíos por correo
			if (saleFound.getMailingRetries()!=null && saleFound.getMailingRetries()==3) {
				if (saleFound.getPrintingRetries()==null) {
					//se agrega porque, ya tiene 3 envíos por correo pero no tiene envíos físicos.
					notificationEao.add(notification);
					saleFound.setPrintingRetries((short)(saleFound.getPrintingRetries()+1));
					saleFound.setNotification(notification);
					saleFound = saleEao.update(saleFound);
					
				}else{
					if (saleFound.getPrintingRetries()>0) {
						//no se agrega porque, ya tiene 3 envíos por correo y 1 envío físico.
						throw new NotificationLimitException();
					}else{
						//se agrega porque, ya tiene 3 envíos por correo pero no tiene envíos físicos. 
						notificationEao.add(notification);
						saleFound.setPrintingRetries((short)(saleFound.getPrintingRetries()+1));
						saleFound.setNotification(notification);
						saleFound = saleEao.update(saleFound);
					}
				}
			}else if(saleFound.getMailingRetries()==null || saleFound.getMailingRetries()<3) {
				if (saleFound.getPrintingRetries()<3) {
					//se agrega porque, menos de 3 envios fisicos.
					notificationEao.add(notification);
					saleFound.setPrintingRetries((short)(saleFound.getPrintingRetries()+1));
					saleFound.setNotification(notification);
					saleFound = saleEao.update(saleFound);
				}else{
					//no se agrega porque, ya tiene 3 envíos físicos.
					throw new NotificationLimitException();
				}
			}
			

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
