package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.util.SaleFile;

public interface SaleService {
	

	public Sale findById(Long saleId) throws ServiceException;
	
	public Sale findByCode(String code);
	
	public void addSaleList(List<Sale> sales, SaleFile headers, String filename, User createdBy);
	
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt,Date saleDateEndedAt, Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException;
	public List<Sale> findSalesBySaleDataLimit(Date saleDateStartedAt,Date saleDateEndedAt, Short bankId, Short productId, SaleStateEnum saleState, Integer first, Integer limit) throws ServiceException;
	public Long findSalesBySaleDataCount(Date saleDateStartedAt,Date saleDateEndedAt, Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException;
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber, Integer first, Integer limit) throws ServiceException;
	//public Long findSalesByCreditCardNumberCount(Long creditCardNumber) throws ServiceException;
	
	public List<Sale> findSalesByNuicResponsible(Long nuicResponsible) throws ServiceException;
	public List<Sale> findSalesByNuicResponsibleLimit(Long nuicResponsible, Integer first, Integer limit) throws ServiceException;
	public Long findSalesByNuicResponsibleCount(Long nuicResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException;
	public List<Sale> findSalesByNamesResponsibleLimit(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible, Integer first, Integer limit) throws ServiceException;
	public Long findSalesByNamesResponsibleCount(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException;
	public List<Sale> findSalesByNamesInsuredLimit(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured, Integer first, Integer limit) throws ServiceException;
	public Long findSalesByNamesInsuredCount(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException;
	
	public List<Sale> findSalesByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException;
	public List<Sale> findSalesByNamesContractorLimit(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor, Integer first, Integer limit) throws ServiceException;
	public Long findSalesByNamesContractorCount(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException;
	
	public List<Sale> findForNotifications(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException;
	public List<Sale> findForNotificationsLimit(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification, String orderNumber,Integer first, Integer limit ) throws ServiceException;
	public Long findForNotificationsCount(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException;
	
	public long checkIfExistSale(int nuicInsured, Date dateOfSale, short bankId, short productId, short collectionPeriodId);
	
	
	
	

}
