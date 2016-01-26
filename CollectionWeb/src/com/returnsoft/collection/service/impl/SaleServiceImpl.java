package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.NotificationEao;
import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.EaoException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.SaleFile;

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
	private CreditCardEao creditCardEao;
	
	@EJB
	private CollectionEao collectionEao;
	
	@EJB
	private RepaymentEao repaymentEao;
	
	@EJB
	private SaleStateEao saleStateEao;
	
	@EJB
	private NotificationEao notificationEao;
	
	@EJB
	private PayerEao payerEao;
	
	@EJB
	private UserEao userEao;
	
	@EJB
	private LoteEao loteEao;
	
	
	
	public Sale findById(Long saleId) throws ServiceException {
		try {

			Sale sale = saleEao.findById(saleId);

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
	
	public Sale findByCode(String code) throws ServiceException{
		try {
			System.out.println("Ingreso a findByCode ServiceImpl");
			
			Sale sale = saleEao.findByCode(code);

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
	
	@Asynchronous
	@Override
	public void addSaleList(List<Sale> sales, String filename, SaleFile headers, Integer userId, Short bankId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			Bank bank = bankEao.findById(bankId);
			User user = userEao.findById(userId);

			lote.setName(filename);
			lote.setTotal(sales.size());
			lote.setProcess(0);
			lote.setDate(date);
			lote.setLoteType(LoteTypeEnum.CREATESALE);
			lote.setState("En progreso");
			loteEao.add(lote);
			// villanuevan@pe.geainternacional.com nidia
			

			Integer lineNumber = 1;

			for (Sale sale : sales) {
				sale.setBank(bank);
				sale.setCreatedBy(user);
				sale.setLote(lote);
				sale.setCreatedAt(date);
				saleEao.add(sale);
				
				lote.setProcess(lineNumber);
				loteEao.update(lote);
				
				lineNumber++;
			}

			lote.setState("Terminado");
			loteEao.update(lote);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null || e.getMessage().length()==0) {
				lote.setState((new NullPointerException()).toString());
			}else{
				if (e.getMessage().length()>500) {
					lote.setState(e.getMessage().substring(0,500));	
				}else{
					lote.setState(e.getMessage());
				}
			}
			try {
				loteEao.update(lote);
			} catch (EaoException e1) {
				e1.printStackTrace();
			}

		}

	}
	
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findBySaleData(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState);

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
	
	public List<Sale> findSalesBySaleDataLimit(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState, Integer first, Integer limit) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findBySaleDataLimit(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState, first, limit);

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
	
	public Long findSalesBySaleDataCount(Date saleDateStartedAt,Date saleDateEndedAt,Short bankId, Short productId, SaleStateEnum saleState) throws ServiceException {
		try {
			
			Long salesCount = saleEao.findBySaleDataCount(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState);

			return salesCount;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Sale> findForNotifications(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException {
		try {
			
			List<Sale> sales = saleEao.findForNotifications(saleDateStartedAt, saleDateEndedAt,  sendingDate, notificationStates,bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification,orderNumber);

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
	
	public List<Sale> findForNotificationsLimit(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber, Integer first, Integer limit) throws ServiceException {
		try {
			
			List<Sale> sales = saleEao.findForNotificationsLimit(saleDateStartedAt, saleDateEndedAt,  sendingDate, notificationStates,bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification,orderNumber,first,limit);

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
	
	public Long findForNotificationsCount(Date saleDateStartedAt,Date saleDateEndedAt,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification,String orderNumber) throws ServiceException {
		try {
			
			Long salesCount = saleEao.findForNotificationsCount(saleDateStartedAt, saleDateEndedAt,  sendingDate, notificationStates,bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification,orderNumber);

			return salesCount;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	/*public List<Sale> findSalesForNotifications(String searchType, Long nuicResponsible, Date saleDateStartedAt,Date saleDateEndedAt,Date affiliationDate, Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType) throws ServiceException{
		try {
			
			List<Sale> sales = null;
			
			if (searchType.equals("dni")) {
				sales = saleEao.findByNuicResponsible(nuicResponsible);
			} else if (searchType.equals("saleData")) {
				sales = saleEao.findBySaleData2(saleDateStartedAt, saleDateEndedAt, affiliationDate,
						sendingDate, notificationStates, bankId, saleState, notificationType);
			}
			
			return sales;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}*/
	
	
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber, Integer first, Integer limit) throws ServiceException {
		try {
			
			/*List<Sale> sales = new ArrayList<Sale>();
			
			List<Sale> sales1 = saleEao.findByCreditCardNumber(creditCardNumber);
			for (Sale sale : sales1) {
				sales.add(sale);
			}*/
			
			//List<Sale> sales2 = creditCardEao.findSalesByCreditCardNumber(creditCardNumber);
			/*for (Sale sale : sales2) {
				sales.add(sale);
			}*/

			return null;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	/*public Long findSalesByCreditCardNumberCount(Long creditCardNumber) throws ServiceException {
		
	}*/
	
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
	
	
	public List<Sale> findSalesByNuicResponsibleLimit(Long nuicResponsible, Integer first, Integer limit) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNuicResponsibleLimit(nuicResponsible,first,limit);

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
	public Long findSalesByNuicResponsibleCount(Long nuicResponsible) throws ServiceException {
		try {
			Long salesCount = saleEao.findByNuicResponsibleCount(nuicResponsible);

			return salesCount;

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
	
	public List<Sale> findSalesByNamesResponsibleLimit(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible, Integer first, Integer limit) throws ServiceException {
		try {
			
			List<Sale> sales = saleEao.findByNamesResponsibleLimit(nuicResponsible,firstnameResponsible,lastnamePaternalResponsible,lastnameMaternalResponsible,first,limit);

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
	
	public Long findSalesByNamesResponsibleCount(Long nuicResponsible, String firstnameResponsible, String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException {
		try {
			
			Long salesCount = saleEao.findByNamesResponsibleCount(nuicResponsible,firstnameResponsible,lastnamePaternalResponsible,lastnameMaternalResponsible);

			return salesCount;

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
	
	public List<Sale> findSalesByNamesInsuredLimit(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured, Integer first, Integer limit) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNamesInsuredLimit(nuicInsured,firstnameInsured,lastnamePaternalInsured,lastnameMaternalInsured,first,limit);

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
	
	public Long findSalesByNamesInsuredCount(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured, String lastnameMaternalInsured) throws ServiceException {
		try {
			Long salesCount = saleEao.findByNamesInsuredCount(nuicInsured,firstnameInsured,lastnamePaternalInsured,lastnameMaternalInsured);

			return salesCount;

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
	
	
	public List<Sale> findSalesByNamesContractorLimit(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor, Integer first, Integer limit) throws ServiceException {
		try {
			List<Sale> sales = saleEao.findByNamesContractorLimit(nuicContractor,firstnameContractor,lastnamePaternalContractor,lastnameMaternalContractor,first,limit);

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
	
	public Long findSalesByNamesContractorCount(Long nuicContractor, String firstnameContractor, String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException {
		try {
			Long salesCount = saleEao.findByNamesContractorCount(nuicContractor,firstnameContractor,lastnamePaternalContractor,lastnameMaternalContractor);

			return salesCount;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	/*public List<SaleState> findMaintenances(Long saleId) throws ServiceException{
		try {
			
			List<SaleState> maintenances = saleStateEao.findBySaleId(saleId);

			return maintenances;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}*/
	
	
	
	//public Long findByNuicInsuredAndDateOfSale(Integer nuicInsured, Date dateOfSale) throws ServiceException{
	public Boolean checkIfExistSale(Integer nuicInsured, Date dateOfSale, Short bankId, Short productId, Short collectionPeriodId) throws ServiceException{	
		try {
			
			//return saleEao.findIdByNuicInsuredAndDateOfSale(nuicInsured, dateOfSale);
			
			return saleEao.checkExistSale(nuicInsured, dateOfSale, bankId, productId, collectionPeriodId);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	/*public Sale affiliate(String code, int userId, Date affiliationDate) throws ServiceException{
		try {
			
			Sale sale = saleEao.findByCode(code);
			sale.setAffiliation(true);
			sale.setAffiliationDate(affiliationDate);
			User user = new User();
			user.setId(userId);
			sale.setAffiliationUser(user);
			sale = saleEao.update(sale);
			
			return sale;
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}*/
	
	
	
}
