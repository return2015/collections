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
	
	public List<Sale> findBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Short bankId, Short productId, SaleStateEnum saleState) throws EaoException;
	
	public List<Sale> findBySaleData2(Date saleDateStartedAt,Date saleDateEndedAt, Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType)  throws EaoException;
	
	//public List<Sale> findByCreditCardNumber(Long creditCardNumber) throws EaoException;
	
	public Sale findByCode(String code) throws EaoException;
	
	public Sale findById(Long id) throws EaoException;
	
	public List<Sale> findByNuicResponsible(Long nuicResponsible) throws EaoException;
	
	public List<Sale> findByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws EaoException;
	
	public List<Sale> findByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws EaoException;
	
	public List<Sale> findByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws EaoException;
	
	public Sale findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws EaoException;
	
	public List<Sale> getNotConditioned() throws EaoException;


}
