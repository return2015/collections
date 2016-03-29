package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.vo.NotificationFile;

public interface NotificationService {
	
	//public void add(Notification notification) throws ServiceException;
	
	public void updatePhysicalList(List<Notification> notifications, NotificationFile headers, String filename, User createdBy);
	
	public void addPhysical(Sale sale, Date sendingAt, String orderNumber, User createdBy) throws ServiceException;
	
	public void addPhysicalList(List<Sale> sales, Date sendingAt, String orderNumber, User createdAt);
	
	public void addVirtualList(List<Sale> sales, User createdBy,String templatePath);
	
	public List<Notification> findBySale(Long saleId) throws ServiceException;
	
	public Notification findById(Integer notificationId) throws ServiceException;
	
	public Notification update(Notification notification) throws ServiceException;
	
	//public File generateLetter(String code) throws ServiceException, MultipleErrorsException;
	
	public Boolean verifyIfExist(Long nuicResponsible, String orderNumber);
	
	public Notification findByKey(Long nuicResponsible, String orderNumber);
	
	public List<Sale> findBySale(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException;
	public List<Sale> findBySaleLimit(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification, String orderNumber,Integer first, Integer limit ) throws ServiceException;
	public Long findBySaleCount(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException;
	
	
}
