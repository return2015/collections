package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;

public interface SaleService {
	
	public void add(Sale sale) throws ServiceException;
	
	public Sale findById(Long saleId) throws ServiceException;
	
	public Sale findByCode(String code) throws ServiceException;
	
	public Sale affiliate(String code, int userId, Date affiliateDate) throws ServiceException;
	
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException;
	
	public List<Sale> findSalesBySaleData2(Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType) throws ServiceException;
	
	public List<Sale> findSalesForNotifications(String searchType, Long nuicResponsible, Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType) throws ServiceException;
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber) throws ServiceException;
	
	public List<Sale> findSalesByNuicResponsible(Long nuicResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException;
	
	public List<Sale> findSalesByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException;
	
	public Sale findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws ServiceException;
	
	
	
	

}
