package com.returnsoft.collection.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import com.returnsoft.collection.controller.SaleFile;
import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.CollectionPeriodEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.DocumentTypeEnum;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.CreditCardDateOverflowException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultFormatException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultOverflowException;
import com.returnsoft.collection.exception.CreditCardExpirationDateFormatException;
import com.returnsoft.collection.exception.CreditCardExpirationDateOverflowException;
import com.returnsoft.collection.exception.CreditCardNumberFormatException;
import com.returnsoft.collection.exception.CreditCardNumberOverflowException;
import com.returnsoft.collection.exception.CreditCardStateOverflowException;
import com.returnsoft.collection.exception.EaoException;
import com.returnsoft.collection.exception.PayerAddressOverflowException;
import com.returnsoft.collection.exception.PayerDepartmentOverflowException;
import com.returnsoft.collection.exception.PayerDistrictOverflowException;
import com.returnsoft.collection.exception.PayerDocumentTypeInvalidException;
import com.returnsoft.collection.exception.PayerDocumentTypeNullException;
import com.returnsoft.collection.exception.PayerDocumentTypeOverflowException;
import com.returnsoft.collection.exception.PayerFirstnameNullException;
import com.returnsoft.collection.exception.PayerFirstnameOverflowException;
import com.returnsoft.collection.exception.PayerLastnameMaternalNullException;
import com.returnsoft.collection.exception.PayerLastnameMaternalOverflowException;
import com.returnsoft.collection.exception.PayerLastnamePaternalNullException;
import com.returnsoft.collection.exception.PayerLastnamePaternalOverflowException;
import com.returnsoft.collection.exception.PayerMailOverflowException;
import com.returnsoft.collection.exception.PayerNuicFormatException;
import com.returnsoft.collection.exception.PayerNuicNullException;
import com.returnsoft.collection.exception.PayerNuicOverflowException;
import com.returnsoft.collection.exception.PayerProvinceOverflowException;
import com.returnsoft.collection.exception.SaleAccountNumberFormatException;
import com.returnsoft.collection.exception.SaleAccountNumberNullException;
import com.returnsoft.collection.exception.SaleAccountNumberOverflowException;
import com.returnsoft.collection.exception.SaleAlreadyExistException;
import com.returnsoft.collection.exception.SaleAuditDateFormatException;
import com.returnsoft.collection.exception.SaleAuditDateNullException;
import com.returnsoft.collection.exception.SaleAuditDateOverflowException;
import com.returnsoft.collection.exception.SaleAuditUserNullException;
import com.returnsoft.collection.exception.SaleAuditUserOverflowException;
import com.returnsoft.collection.exception.SaleBankInvalidException;
import com.returnsoft.collection.exception.SaleBankNullException;
import com.returnsoft.collection.exception.SaleBankOverflowException;
import com.returnsoft.collection.exception.SaleCertificateNumberOverflowException;
import com.returnsoft.collection.exception.SaleChannelOverflowException;
import com.returnsoft.collection.exception.SaleCollectionPeriodInvalidException;
import com.returnsoft.collection.exception.SaleCollectionPeriodNullException;
import com.returnsoft.collection.exception.SaleCollectionPeriodOverflowException;
import com.returnsoft.collection.exception.SaleCollectionTypeNullException;
import com.returnsoft.collection.exception.SaleCollectionTypeOverflowException;
import com.returnsoft.collection.exception.SaleCommercialCodeNullException;
import com.returnsoft.collection.exception.SaleCommercialCodeOverflowException;
import com.returnsoft.collection.exception.SaleCreditCardUpdatedAtFormatException;
import com.returnsoft.collection.exception.SaleDateFormatException;
import com.returnsoft.collection.exception.SaleDateNullException;
import com.returnsoft.collection.exception.SaleDateOverflowException;
import com.returnsoft.collection.exception.SaleDownChannelOverflowException;
import com.returnsoft.collection.exception.SaleDownObservationOverflowException;
import com.returnsoft.collection.exception.SaleDownReasonOverflowException;
import com.returnsoft.collection.exception.SaleDownUserOverflowException;
import com.returnsoft.collection.exception.SaleFirstnameContractorOverFlowException;
import com.returnsoft.collection.exception.SaleFirstnameInsuredOverflowException;
import com.returnsoft.collection.exception.SaleInsurancePremiumFormatException;
import com.returnsoft.collection.exception.SaleInsurancePremiumNullException;
import com.returnsoft.collection.exception.SaleInsurancePremiumOverflowException;
import com.returnsoft.collection.exception.SaleLastnameMaternalContractorOverFlowException;
import com.returnsoft.collection.exception.SaleLastnameMaternalInsuredOverflowException;
import com.returnsoft.collection.exception.SaleLastnamePaternalContractorOverFlowException;
import com.returnsoft.collection.exception.SaleLastnamePaternalInsuredOverflowException;
import com.returnsoft.collection.exception.SaleNuicContractorFormatException;
import com.returnsoft.collection.exception.SaleNuicContractorOverFlowException;
import com.returnsoft.collection.exception.SaleNuicInsuredFormatException;
import com.returnsoft.collection.exception.SaleNuicInsuredNullException;
import com.returnsoft.collection.exception.SaleNuicInsuredOverflowException;
import com.returnsoft.collection.exception.SalePhone1FormatException;
import com.returnsoft.collection.exception.SalePhone1OverflowException;
import com.returnsoft.collection.exception.SalePhone2FormatException;
import com.returnsoft.collection.exception.SalePhone2OverflowException;
import com.returnsoft.collection.exception.SalePlaceOverflowException;
import com.returnsoft.collection.exception.SalePolicyNumberOverflowException;
import com.returnsoft.collection.exception.SaleProductDescriptionOverflowException;
import com.returnsoft.collection.exception.SaleProductInvalidException;
import com.returnsoft.collection.exception.SaleProductNullException;
import com.returnsoft.collection.exception.SaleProductOverflowException;
import com.returnsoft.collection.exception.SaleProposalNumberOverflowException;
import com.returnsoft.collection.exception.SaleStateDateFormatException;
import com.returnsoft.collection.exception.SaleStateDateOverflowException;
import com.returnsoft.collection.exception.SaleStateInvalidException;
import com.returnsoft.collection.exception.SaleStateNullException;
import com.returnsoft.collection.exception.SaleVendorCodeOverflowException;
import com.returnsoft.collection.exception.SaleVendorNameOverflowException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.LoteService;

@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class LoteServiceImpl implements LoteService {

	//@Resource
	//private UserTransaction userTransaction;

	@EJB
	private LoteEao loteEao;

	@EJB
	private ProductEao productEao;

	@EJB
	private CollectionPeriodEao collectionPeriodEao;

	@EJB
	private UserEao userEao;

	@EJB
	private BankEao bankEao;
	
	
	@EJB
	private PayerEao payerEao;

	@EJB
	private SaleStateEao saleStateEao;

	@EJB
	private CreditCardEao creditCardEao;
	
	@EJB
	private SaleEao saleEao;
	
	//@EJB
	//private LoteService loteService;
	

	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public void update(Lote lote) {
//
//		try {
//			
//			userTransaction.begin();
//			loteEao.update(lote);
//			userTransaction.commit();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public Lote create(String name, Integer total) throws ServiceException {
//
//		try {
//			
//			userTransaction.begin();
//
//			Lote lote = new Lote();
//			lote.setName(name);
//			lote.setTotal(total);
//			lote.setProcess(0);
//			lote.setDate(new Date());
//			lote.setState("En progreso");
//
//			loteEao.add(lote);
//			
//			userTransaction.commit();
//
//			return lote;			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
//				throw new ServiceException(e.getMessage(), e);
//			} else {
//				throw new ServiceException();
//			}
//		}
//
//	}

	public List<Lote> findByDate(Date date) throws ServiceException {
		try {

			return loteEao.findByDate(date);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
				throw new ServiceException(e.getMessage(), e);
			} else {
				throw new ServiceException();
			}
		}
	}

	@Asynchronous
	@Override
	public void addTypeSale(List<Sale> sales, String filename, SaleFile headers, Integer userId, Short bankId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			/// COMPLETE FOREIGNS
			//List<Product> productsEntity = productEao.getProducts();
			//List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodEao.getAll();
			Bank bank = bankEao.findById(bankId);
			User user = userEao.findById(userId);

			//userTransaction.begin();
			
			
			
			/*List<Sale> newSales = new ArrayList<Sale>();

			for (Sale sale : sales) {
				sale.setBank(bank);
				sale.setCreatedBy(user);
				for (CollectionPeriod collectionPeriod : collectionPeriodsEntity) {
					if (sale.getCollectionPeriod().getId().equals(collectionPeriod.getId())) {
						sale.setCollectionPeriod(collectionPeriod);
						break;
					}
				}
				for (Product product : productsEntity) {
					if (sale.getProduct().getId().equals(product.getId())) {
						sale.setProduct(product);
						break;
					}
				}
				newSales.add(sale);
			}*/
			lote.setName(filename);
			lote.setTotal(sales.size());
			lote.setProcess(0);
			lote.setDate(date);
			lote.setLoteType(LoteTypeEnum.CREATESALE);
			lote.setState("En progreso");
			//lote = loteService.create(filename,newSales.size());
			loteEao.add(lote);

			Integer lineNumber = 1;
			
			
			
			// lunes 10

			for (Sale sale : sales) {

				System.out.println("INSERTANDO" + lineNumber);
				sale.setBank(bank);
				sale.setCreatedBy(user);
				sale.setLote(lote);
				sale.setCreatedAt(date);

				/*SaleState saleState = sale.getSaleState();
				CreditCard creditCard = sale.getCreditCard();
				Payer payer = sale.getPayer();*/

				//saleStateEao.add(sale.getSaleState());
				//creditCardEao.add(sale.getCreditCard());
				//payerEao.add(sale.getPayer());
				
				//Payer payer = sale.getPayer();
				//sale.setPayer(null);
				/*sale.setSaleState(saleState);
				sale.setCreditCard(creditCard);
				sale.setPayer(payer);*/
				saleEao.add(sale);
				
				///payer.setSale(sale);
				//payerEao.add(sale.getPayer());

				lote.setProcess(lineNumber);
				loteEao.update(lote);

				lineNumber++;

			}
			
			/*for (Sale sale : newSales) {
				sale.setLote(lote);
				saleEao.update(sale);
			}*/

			lote.setState("Terminado");
			loteEao.update(lote);
			
			//userTransaction.commit();
			

		} catch (Exception e) {
			System.out.println("SERVICE: INGRESO AL CATCH DE ADDTYPESALE");
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
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*try {
				userTransaction.setRollbackOnly();
			} catch (Exception e2) {
				e2.printStackTrace();
				//throw new ServiceException(e2);
			}*/
			//throw new ServiceException(e);

		}

	}

}
