package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;

public interface NotificationService {
	
	public Sale findSaleById(Long id) throws ServiceException;
	
	public void add(Notification notification) throws ServiceException;
	
	public List<Notification> findNotificationsByData(Date sendingAt,Date createdAt,NotificationTypeEnum notificationType, NotificationStateEnum notificationState) throws ServiceException;
	
	public Notification findById(Integer notificationId) throws ServiceException;
	
	public Notification update(Notification notification) throws ServiceException;

}
