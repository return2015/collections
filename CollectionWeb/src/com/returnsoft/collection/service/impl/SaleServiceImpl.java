package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.MaintenanceEao;
import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.eao.SaleEao;
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
import com.returnsoft.collection.service.SaleService;

@Stateless
public class SaleServiceImpl implements SaleService{

	@EJB
	private SaleEao saleEao;
	
	@EJB
	private BankEao bankEao;
	
	@EJB
	private ProductEao productEao;
	
	
	@EJB
	private CommerceEao commerceEao;
	
	@EJB
	private CreditCardEao creditCardUpdateEao;
	
	@EJB
	private CollectionEao collectionEao;
	
	@EJB
	private RepaymentEao repaymentEao;
	
	@EJB
	private MaintenanceEao maintenanceEao;
	
	@EJB
	private NotificationEao notificationEao;
	
	@EJB
	private PayerEao payerEao;
	
	
	
	/*@EJB
	private MailingEao mailingEao;*/
	
	/*public String load(List<Sale> sales) throws ServiceException{
		try {
			for (Sale sale : sales) {
				saleEao.add(sale);	
			}
			
			return "success";

		} catch (EaoException e) {
			throw new ServiceException("EaoException:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("ServiceException:" + e.getMessage());
		}
	}*/
	
	public void add(Sale sale) throws ServiceException{
		try {
			
			SaleState saleState = sale.getSaleState();
			
			CreditCard creditCard = sale.getCreditCard();
			
			Payer payer = sale.getPayer();
			
			sale.setSaleState(null);
			sale.setCreditCard(null);
			sale.setPayer(null);
			
			saleEao.add(sale);
			
			saleState.setSale(sale);
			maintenanceEao.add(saleState);
			
			creditCard.setSale(sale);
			creditCardUpdateEao.add(creditCard);
			
			payer.setSale(sale);
			payerEao.add(payer);
			
			sale.setSaleState(saleState);
			sale.setCreditCard(creditCard);
			sale.setPayer(payer);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException{
		try {
			
			List<Commerce> commerces = commerceEao.findByBankId(bankId);
			return commerces;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Bank> getBanks() throws ServiceException {
		try {

			List<Bank> banks = bankEao.getBanks();

			return banks;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Product> getProducts() throws ServiceException {
		try {

			List<Product> products = productEao.getProducts();

			return products;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Integer bankId, Integer productId, SaleStateEnum saleState) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findBySaleData(saleDateStartedAt, saleDateEndedAt,  affiliationDate, bankId, productId, saleState);

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber) throws ServiceException {
		try {
			
			/*List<Sale> sales = new ArrayList<Sale>();
			
			List<Sale> sales1 = saleEao.findByCreditCardNumber(creditCardNumber);
			for (Sale sale : sales1) {
				sales.add(sale);
			}*/
			
			List<Sale> sales2 = creditCardUpdateEao.findSalesByCreditCardNumber(creditCardNumber);
			/*for (Sale sale : sales2) {
				sales.add(sale);
			}*/

			return sales2;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findSalesByNuicResponsible(Long nuicResponsible) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNuicResponsible(nuicResponsible);

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findSalesByNamesResponsible(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNamesResponsible(nuicResponsible,firstnameResponsible,lastnamePaternalResponsible,lastnameMaternalResponsible);

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findSalesByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNamesInsured(nuicInsured,firstnameInsured,lastnamePaternalInsured,lastnameMaternalInsured);

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findSalesByNamesContractor(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNamesContractor(nuicContractor,firstnameContractor,lastnamePaternalContractor,lastnameMaternalContractor);

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	/*public List<Notification> findMailings(Long saleId) throws ServiceException{
		try {
			
			List<Notification> mailings = mailingEao.findBySaleId(saleId);

			return mailings;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}*/
	
	public List<CreditCard> findUpdates(Long saleId) throws ServiceException{
		try {
			
			List<CreditCard> creditCardUpdates = creditCardUpdateEao.findBySaleId(saleId);

			return creditCardUpdates;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Collection> findCollections(Long saleId) throws ServiceException{
		try {
			
			List<Collection> collections = collectionEao.findBySaleId(saleId);

			return collections;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public List<Repayment> findRepayments(Long saleId) throws ServiceException{
		try {
			
			List<Repayment> repayments = repaymentEao.findBySaleId(saleId);

			return repayments;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public List<SaleState> findMaintenances(Long saleId) throws ServiceException{
		try {
			
			List<SaleState> maintenances = maintenanceEao.findBySaleId(saleId);

			return maintenances;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Notification> findNotifications(Long saleId) throws ServiceException{
		try {
			
			List<Notification> notifications = notificationEao.findBySaleId(saleId);

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
	
	
	public Sale findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws ServiceException{
		try {
			
			Sale sale = saleEao.findByNuicInsuredAndDateOfSale(nuicInsured, dateOfSale);
			
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
	
	
}
