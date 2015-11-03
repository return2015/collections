package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.exception.ServiceException;

public interface NotificationService {
	
	public void add(Notification notification) throws ServiceException;
	
	public List<Notification> findBySale(Long saleId) throws ServiceException;
	
	public Notification findById(Integer notificationId) throws ServiceException;
	
	public Notification update(Notification notification) throws ServiceException;
	
}
