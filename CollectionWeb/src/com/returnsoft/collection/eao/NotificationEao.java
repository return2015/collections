package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.EaoException;

public interface NotificationEao {
	
	public void add(Notification notification) throws EaoException;
	
	public List<Notification> findBySaleId(Long saleId) throws EaoException;
	
	public List<Notification> findByData(Date sendingAt,Date createdAt, NotificationTypeEnum notificationType, NotificationStateEnum notificationState) throws EaoException;
	
	public Notification findById(Integer notificationId) throws EaoException;
	
	public Notification update(Notification notification) throws EaoException;

}
