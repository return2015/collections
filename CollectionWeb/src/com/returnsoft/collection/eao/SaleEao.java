package com.returnsoft.collection.eao;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.EaoException;

public interface SaleEao{
	
	public void add(Sale sale) throws EaoException;
	
	public Sale update(Sale sale) throws EaoException;
	
	public List<Sale> findBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState) throws EaoException;
	public List<Sale> findBySaleDataLimit(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState, Integer first, Integer limit) throws EaoException;
	public Long findBySaleDataCount(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState) throws EaoException;
	
	public List<Sale> findForNotifications(Date saleDateStartedAt,Date saleDateEndedAt, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber)  throws EaoException;
	public List<Sale> findForNotificationsLimit(Date saleDateStartedAt,Date saleDateEndedAt, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber, Integer first, Integer limit)  throws EaoException;
	public Long findForNotificationsCount(Date saleDateStartedAt,Date saleDateEndedAt, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber)  throws EaoException;
	
	//public List<Sale> findByCreditCardNumber(Long creditCardNumber) throws EaoException;
	
	public Sale findByCode(String code) throws EaoException;
	
	public Sale findById(Long id) throws EaoException;
	
	public List<Sale> findByNuicResponsible(Long nuicResponsible) throws EaoException;
	public List<Sale> findByNuicResponsibleLimit(Long nuicResponsible, Integer first, Integer limit) throws EaoException;
	public Long findByNuicResponsibleCount(Long nuicResponsible) throws EaoException;
	
	public Boolean checkExistSale(Integer nuicInsured, Date dateOfSale, Short bankId, Short productId, Short collectionPeriodId) throws EaoException;
	
	public List<Sale> findByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws EaoException;
	public List<Sale> findByNamesResponsibleLimit(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible, Integer first, Integer limit) throws EaoException;
	public Long findByNamesResponsibleCount(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws EaoException;
	
	public List<Sale> findByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws EaoException;
	public List<Sale> findByNamesInsuredLimit(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured, Integer first, Integer limit) throws EaoException;
	public Long findByNamesInsuredCount(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws EaoException;
	
	public List<Sale> findByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws EaoException;
	public List<Sale> findByNamesContractorLimit(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor, Integer first, Integer limit) throws EaoException;
	public Long findByNamesContractorCount(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws EaoException;
	
	//public Long findIdByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws EaoException;
	
	//public List<Sale> getNotConditioned() throws EaoException;


}
