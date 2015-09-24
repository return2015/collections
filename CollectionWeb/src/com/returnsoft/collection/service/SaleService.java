package com.returnsoft.collection.service;

import java.util.Date;
import java.util.List;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;

public interface SaleService {
	
	//public String load(List<Sale> sales) throws ServiceException;
	
	public void add(Sale sale) throws ServiceException;
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException;
	
	public List<Bank> getBanks() throws ServiceException;
	
	public List<Product> getProducts() throws ServiceException;
	
	public List<CreditCard> findUpdates(Long saleId) throws ServiceException;
	
	//public List<Notification> findMailings(Long saleId) throws ServiceException;
	
	public List<Payer> findPayers(Long saleId) throws ServiceException;
	
	public List<Collection> findCollections(Long saleId) throws ServiceException;
	
	public List<Repayment> findRepayments(Long saleId) throws ServiceException;
	
	public List<SaleState> findMaintenances(Long saleId) throws ServiceException;
	
	public List<Notification> findNotifications(Long saleId) throws ServiceException;
	
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException;
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber) throws ServiceException;
	
	public List<Sale> findSalesByNuicResponsible(Long nuicResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException;
	
	public List<Sale> findSalesByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException;
	
	public List<Sale> findSalesByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException;
	
	public Sale findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws ServiceException;
	

}
