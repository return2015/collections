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
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.CreditCardDateOverflowException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultFormatException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultOverflowException;
import com.returnsoft.collection.exception.CreditCardExpirationDateFormatException;
import com.returnsoft.collection.exception.CreditCardExpirationDateOverflowException;
import com.returnsoft.collection.exception.CreditCardNumberFormatException;
import com.returnsoft.collection.exception.CreditCardNumberOverflowException;
import com.returnsoft.collection.exception.CreditCardStateOverflowException;
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
@TransactionManagement(TransactionManagementType.BEAN)
public class SaleServiceBackgroundImpl implements com.returnsoft.collection.service.SaleServiceBackground {

	@Resource
	private UserTransaction userTransaction;


	@EJB
	private PayerEao payerEao;

	@EJB
	private SaleStateEao saleStateEao;

	@EJB
	private CreditCardEao creditCardEao;

	@EJB
	private SaleEao saleEao;

	///////

	@EJB
	private ProductEao productEao;

	@EJB
	private CollectionPeriodEao collectionPeriodEao;

	//

	@EJB
	private UserEao userEao;

	@EJB
	private BankEao bankEao;

	@EJB
	private LoteEao loteEao;
	
	@Asynchronous
	@Override
	public void add(List<SaleFile> dataList, String filename, SaleFile headers, Integer userId, Short bankId)
			 {

		List<String> errors = new ArrayList<>();
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

		try {
				
			userTransaction.begin();
			
			Lote lote = new Lote();
			lote.setName(filename);
			lote.setTotal(dataList.size());
			lote.setProcess(0);
			lote.setState("En progreso");
			
			loteEao.add(lote);
			
			Bank bank = bankEao.findById(bankId);
			User user = userEao.findById(userId);
			Date currentDate = new Date();
			List<Product> productsEntity = productEao.getProducts();
			List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodEao.getAll();

			List<Sale> sales = new ArrayList<Sale>();
			Integer lineNumber = 1;

			for (SaleFile saleFile : dataList) {

				System.out.println("VERIFICANDO"+lineNumber);
				lineNumber++;

				Sale sale = new Sale();
				Payer payer = new Payer();
				SaleState saleState = new SaleState();
				CreditCard creditCard = new CreditCard();

				// DOCUMENT_TYPE
				if (saleFile.getDocumentType().length() == 0) {
					Exception ex = new PayerDocumentTypeNullException();
					errors.add(generateErrorMessageHeader(headers.getDocumentType(), lineNumber) + ex.getMessage());
				} else if (saleFile.getDocumentType().length() != 3) {
					Exception ex = new PayerDocumentTypeOverflowException(3);
					errors.add(generateErrorMessageHeader(headers.getDocumentType(), lineNumber) + ex.getMessage());
				} else {
					DocumentTypeEnum documentTypeEnum = DocumentTypeEnum.findByName(saleFile.getDocumentType());
					if (documentTypeEnum == null) {
						Exception ex = new PayerDocumentTypeInvalidException();
						errors.add(generateErrorMessageHeader(headers.getDocumentType(), lineNumber) + ex.getMessage());
					} else {
						payer.setDocumentType(documentTypeEnum);
					}
				}

				// NUIC RESPONSIBLE
				if (saleFile.getNuicResponsible().length() == 0) {
					Exception ex = new PayerNuicNullException();
					errors.add(generateErrorMessageHeader(headers.getNuicResponsible(), lineNumber) + ex.getMessage());
				} else if (saleFile.getNuicResponsible().length() > 11) {
					Exception ex = new PayerNuicOverflowException(11);
					errors.add(generateErrorMessageHeader(headers.getNuicResponsible(), lineNumber) + ex.getMessage());
				} else {
					try {
						Long nuicResponsible = Long.parseLong(saleFile.getNuicResponsible());
						payer.setNuicResponsible(nuicResponsible);
					} catch (NumberFormatException e) {
						Exception ex = new PayerNuicFormatException();
						errors.add(
								generateErrorMessageHeader(headers.getNuicResponsible(), lineNumber) + ex.getMessage());
					}
				}

				// APELLIDO PATERNO RESPONSABLE DE PAGO
				if (saleFile.getLastnamePaternalResponsible().length() == 0) {
					Exception ex = new PayerLastnamePaternalNullException();
					errors.add(generateErrorMessageHeader(headers.getLastnamePaternalResponsible(), lineNumber)
							+ ex.getMessage());
				} else if (saleFile.getLastnamePaternalResponsible().length() > 50) {
					Exception ex = new PayerLastnamePaternalOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnamePaternalResponsible(), lineNumber)
							+ ex.getMessage());
				} else {
					payer.setLastnamePaternalResponsible(saleFile.getLastnamePaternalResponsible());
				}

				// APELLIDO MATERNO RESPONSABLE DE PAGO
				if (saleFile.getLastnameMaternalResponsible().length() == 0) {
					Exception ex = new PayerLastnameMaternalNullException();
					errors.add(generateErrorMessageHeader(headers.getLastnameMaternalResponsible(), lineNumber)
							+ ex.getMessage());
				} else if (saleFile.getLastnameMaternalResponsible().length() > 50) {
					Exception ex = new PayerLastnameMaternalOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnameMaternalResponsible(), lineNumber)
							+ ex.getMessage());
				} else {
					payer.setLastnameMaternalResponsible(saleFile.getLastnameMaternalResponsible());
				}

				// NOMBRES DE RESPONSABLE DE PAGO
				if (saleFile.getFirstnameResponsible().length() == 0) {
					Exception ex = new PayerFirstnameNullException();
					errors.add(generateErrorMessageHeader(headers.getFirstnameResponsible(), lineNumber)
							+ ex.getMessage());
				} else if (saleFile.getFirstnameResponsible().length() > 50) {
					Exception ex = new PayerFirstnameOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getFirstnameResponsible(), lineNumber)
							+ ex.getMessage());
				} else {
					payer.setFirstnameResponsible(saleFile.getFirstnameResponsible());
				}

				// NUMERO TARJETA DE CREDITO
				if (saleFile.getCreditCardNumber().length() > 0 && saleFile.getCreditCardNumber().length() != 16) {
					Exception ex = new CreditCardNumberOverflowException(16);
					errors.add(generateErrorMessageHeader(headers.getCreditCardNumber(), lineNumber) + ex.getMessage());
				} else if (saleFile.getCreditCardNumber().length() > 0) {
					try {
						Long creditCardNumber = Long.parseLong(saleFile.getCreditCardNumber());
						creditCard.setNumber(creditCardNumber);
					} catch (NumberFormatException e) {
						Exception ex = new CreditCardNumberFormatException();
						errors.add(generateErrorMessageHeader(headers.getCreditCardNumber(), lineNumber)
								+ ex.getMessage());
					}
				}

				// NUMERO DE CUENTA
				if (saleFile.getAccountNumber().length() == 0) {
					Exception ex = new SaleAccountNumberNullException();
					errors.add(generateErrorMessageHeader(headers.getAccountNumber(), lineNumber) + ex.getMessage());
				} else if (saleFile.getAccountNumber().length() > 10) {
					Exception ex = new SaleAccountNumberOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getAccountNumber(), lineNumber) + ex.getMessage());
				} else {
					try {
						Long accountNumber = Long.parseLong(saleFile.getAccountNumber());
						sale.setAccountNumber(accountNumber);
					} catch (NumberFormatException e) {
						Exception ex = new SaleAccountNumberFormatException();
						errors.add(
								generateErrorMessageHeader(headers.getAccountNumber(), lineNumber) + ex.getMessage());
					}
				}

				// FECHA DE VENCIMIENTO DE TARJETA
				if (saleFile.getCreditCardExpirationDate().length() > 0
						&& saleFile.getCreditCardExpirationDate().length() != 7) {
					Exception ex = new CreditCardExpirationDateOverflowException(7);
					errors.add(generateErrorMessageHeader(headers.getCreditCardExpirationDate(), lineNumber)
							+ ex.getMessage());
				} else {
					try {
						Date expirationDate = sdf2.parse(saleFile.getCreditCardExpirationDate());
						creditCard.setExpirationDate(expirationDate);
					} catch (ParseException e1) {
						Exception ex = new CreditCardExpirationDateFormatException();
						errors.add(generateErrorMessageHeader(headers.getCreditCardExpirationDate(), lineNumber)
								+ ex.getMessage());
					}
				}

				// ESTADO DE TARJETA DE CREDITO
				if (saleFile.getCreditCardState().length() > 20) {
					Exception ex = new CreditCardStateOverflowException(20);
					errors.add(generateErrorMessageHeader(headers.getCreditCardState(), lineNumber) + ex.getMessage());
				} else {
					creditCard.setState(saleFile.getCreditCardState());
				}

				// DIAS DE MORA
				if (saleFile.getCreditCardDaysOfDefault().length() > 4) {
					Exception ex = new CreditCardDaysOfDefaultOverflowException(4);
					errors.add(generateErrorMessageHeader(headers.getCreditCardDaysOfDefault(), lineNumber)
							+ ex.getMessage());
				} else if (saleFile.getCreditCardDaysOfDefault().length() > 0) {
					try {
						Integer daysOfDefault = Integer.parseInt(saleFile.getCreditCardDaysOfDefault());
						creditCard.setDaysOfDefault(daysOfDefault);
					} catch (NumberFormatException e) {
						Exception ex = new CreditCardDaysOfDefaultFormatException();
						errors.add(generateErrorMessageHeader(headers.getCreditCardDaysOfDefault(), lineNumber)
								+ ex.getMessage());
					}
				}

				// NUIC DE CONTRATANTE
				if (saleFile.getNuicContractor().length() > 8) {
					Exception ex = new SaleNuicContractorOverFlowException(8);
					errors.add(generateErrorMessageHeader(headers.getNuicContractor(), lineNumber) + ex.getMessage());
				} else if (saleFile.getNuicContractor().length() > 0) {
					try {
						Integer nuicContractor = Integer.parseInt(saleFile.getNuicContractor());
						sale.setNuicContractor(nuicContractor);
					} catch (NumberFormatException e) {
						Exception ex = new SaleNuicContractorFormatException();
						errors.add(
								generateErrorMessageHeader(headers.getNuicContractor(), lineNumber) + ex.getMessage());
					}
				}

				// APELLIDO PATERNO DE CONTRATANTE
				if (saleFile.getLastnamePaternalContractor().length() > 50) {
					Exception ex = new SaleLastnamePaternalContractorOverFlowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnamePaternalContractor(), lineNumber)
							+ ex.getMessage());
				} else {
					sale.setLastnamePaternalContractor(saleFile.getLastnamePaternalContractor());
				}

				// APELLIDO MATERNO CONTRATANTE
				if (saleFile.getLastnameMaternalContractor().length() > 50) {
					Exception ex = new SaleLastnameMaternalContractorOverFlowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnameMaternalContractor(), lineNumber)
							+ ex.getMessage());
				} else {
					sale.setLastnameMaternalContractor(saleFile.getLastnameMaternalContractor());
				}

				// NOMBRES DE CONTRATANTE
				// System.out.println("saleFile.getFirstnameContractor()" +
				// saleFile.getFirstnameContractor());
				if (saleFile.getFirstnameContractor().length() > 50) {
					Exception ex = new SaleFirstnameContractorOverFlowException(50);
					errors.add(
							generateErrorMessageHeader(headers.getFirstnameContractor(), lineNumber) + ex.getMessage());
				} else {
					sale.setFirstnameContractor(saleFile.getFirstnameContractor());
				}

				// NUIC DE ASEGURADO
				// System.out.println("saleFile.getNuicInsured():" +
				// saleFile.getNuicInsured());
				if (saleFile.getNuicInsured().length() == 0) {
					Exception ex = new SaleNuicInsuredNullException();
					errors.add(generateErrorMessageHeader(headers.getNuicInsured(), lineNumber) + ex.getMessage());
				} else if (saleFile.getNuicInsured().length() > 8) {
					Exception ex = new SaleNuicInsuredOverflowException(8);
					errors.add(generateErrorMessageHeader(headers.getNuicInsured(), lineNumber) + ex.getMessage());
				} else {
					try {
						Integer nuicInsured = Integer.parseInt(saleFile.getNuicInsured());
						sale.setNuicInsured(nuicInsured);
					} catch (NumberFormatException e) {
						Exception ex = new SaleNuicInsuredFormatException();
						errors.add(generateErrorMessageHeader(headers.getNuicInsured(), lineNumber) + ex.getMessage());
					}
				}

				// APELLIDO PATERNO ASEGURADO
				if (saleFile.getLastnamePaternalInsured().length() > 50) {
					Exception ex = new SaleLastnamePaternalInsuredOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnamePaternalInsured(), lineNumber)
							+ ex.getMessage());
				} else {
					sale.setLastnamePaternalInsured(saleFile.getLastnamePaternalInsured());
				}

				// APELLIDO MATERNO ASEGURADO
				if (saleFile.getLastnameMaternalInsured().length() > 50) {
					Exception ex = new SaleLastnameMaternalInsuredOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getLastnameMaternalInsured(), lineNumber)
							+ ex.getMessage());
				} else {
					sale.setLastnameMaternalInsured(saleFile.getLastnameMaternalInsured());
				}

				// NOMBRES ASEGURADO
				if (saleFile.getFirstnameInsured().length() > 50) {
					Exception ex = new SaleFirstnameInsuredOverflowException(50);
					errors.add(generateErrorMessageHeader(headers.getFirstnameInsured(), lineNumber) + ex.getMessage());
				} else {
					sale.setFirstnameInsured(saleFile.getFirstnameInsured());
				}

				// TELEFONO1
				if (saleFile.getPhone1().length() > 9) {
					Exception ex = new SalePhone1OverflowException(9);
					errors.add(generateErrorMessageHeader(headers.getPhone1(), lineNumber) + ex.getMessage());
				} else {
					if (saleFile.getPhone1().length() > 0) {
						try {
							Integer phone1 = Integer.parseInt(saleFile.getPhone1());
							sale.setPhone1(phone1);
						} catch (NumberFormatException e) {
							Exception ex = new SalePhone1FormatException();
							errors.add(generateErrorMessageHeader(headers.getPhone1(), lineNumber) + ex.getMessage());
						}
					}
				}

				// TELEFONO2
				if (saleFile.getPhone2().length() > 9) {
					Exception ex = new SalePhone2OverflowException(9);
					errors.add(generateErrorMessageHeader(headers.getPhone2(), lineNumber) + ex.getMessage());
				} else {
					if (saleFile.getPhone2().length() > 0) {
						try {
							Integer phone2 = Integer.parseInt(saleFile.getPhone2());
							sale.setPhone2(phone2);
						} catch (NumberFormatException e) {
							Exception ex = new SalePhone2FormatException();
							errors.add(generateErrorMessageHeader(headers.getPhone2(), lineNumber) + ex.getMessage());
						}
					}
				}

				// MAIL
				if (saleFile.getMail().length() > 45) {
					Exception ex = new PayerMailOverflowException(45);
					errors.add(generateErrorMessageHeader(headers.getMail(), lineNumber) + ex.getMessage());
				} else {
					payer.setMail(saleFile.getMail());
				}

				// DEPARTAMENTO
				if (saleFile.getDepartment().length() > 20) {
					Exception ex = new PayerDepartmentOverflowException(20);
					errors.add(generateErrorMessageHeader(headers.getDepartment(), lineNumber) + ex.getMessage());
				} else {
					payer.setDepartment(saleFile.getDepartment());
				}

				// PROVINCIA
				if (saleFile.getProvince().length() > 20) {
					Exception ex = new PayerProvinceOverflowException(20);
					errors.add(generateErrorMessageHeader(headers.getProvince(), lineNumber) + ex.getMessage());
				} else {
					payer.setProvince(saleFile.getProvince());
				}

				// DISTRITO
				if (saleFile.getDistrict().length() > 40) {
					Exception ex = new PayerDistrictOverflowException(40);
					errors.add(generateErrorMessageHeader(headers.getDistrict(), lineNumber) + ex.getMessage());
				} else {
					payer.setDistrict(saleFile.getDistrict());
				}

				// DIRECCION
				if (saleFile.getAddress().length() > 150) {
					Exception ex = new PayerAddressOverflowException(150);
					errors.add(generateErrorMessageHeader(headers.getAddress(), lineNumber) + ex.getMessage());
				} else {
					payer.setAddress(saleFile.getAddress());
				}

				// FECHA DE VENTA
				if (saleFile.getDate().length() == 0) {
					Exception ex = new SaleDateNullException();
					errors.add(generateErrorMessageHeader(headers.getDate(), lineNumber) + ex.getMessage());
				} else if (saleFile.getDate().length() != 10) {
					Exception ex = new SaleDateOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getDate(), lineNumber) + ex.getMessage());
				} else {
					try {
						Date saleDate = sdf1.parse(saleFile.getDate());
						sale.setDate(saleDate);
					} catch (ParseException e1) {
						Exception ex = new SaleDateFormatException();
						errors.add(generateErrorMessageHeader(headers.getDate(), lineNumber) + ex.getMessage());
					}
				}

				// CANAL DE VENTA
				if (saleFile.getChannel().length() > 15) {
					Exception ex = new SaleChannelOverflowException(15);
					errors.add(generateErrorMessageHeader(headers.getChannel(), lineNumber) + ex.getMessage());
				} else {
					sale.setChannel(saleFile.getChannel());
				}

				// LUGAR DE VENTA
				if (saleFile.getPlace().length() > 25) {
					Exception ex = new SalePlaceOverflowException(25);
					errors.add(generateErrorMessageHeader(headers.getPlace(), lineNumber) + ex.getMessage());
				} else {
					sale.setPlace(saleFile.getPlace());
				}

				// CODIGO DE VENDEDOR
				if (saleFile.getVendorCode().length() > 10) {
					Exception ex = new SaleVendorCodeOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getVendorCode(), lineNumber) + ex.getMessage());
				} else {
					sale.setVendorCode(saleFile.getVendorCode());
				}

				// NOMBRE DE VENDEDOR
				if (saleFile.getVendorName().length() > 35) {
					Exception ex = new SaleVendorNameOverflowException(35);
					errors.add(generateErrorMessageHeader(headers.getVendorName(), lineNumber) + ex.getMessage());
				} else {
					sale.setVendorName(saleFile.getVendorName());
				}

				// NUMERO DE POLIZA
				if (saleFile.getPolicyNumber().length() > 10) {
					Exception ex = new SalePolicyNumberOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getPolicyNumber(), lineNumber) + ex.getMessage());
				} else {
					sale.setPolicyNumber(saleFile.getPolicyNumber());
				}

				// NUMERO DE CERTIFICADO
				if (saleFile.getCertificateNumber().length() > 10) {
					Exception ex = new SaleCertificateNumberOverflowException(10);
					errors.add(
							generateErrorMessageHeader(headers.getCertificateNumber(), lineNumber) + ex.getMessage());
				} else {
					sale.setCertificateNumber(saleFile.getCertificateNumber());
				}

				// NUMERO DE PROPUESTA
				if (saleFile.getProposalNumber().length() > 25) {
					Exception ex = new SaleProposalNumberOverflowException(25);
					errors.add(generateErrorMessageHeader(headers.getProposalNumber(), lineNumber) + ex.getMessage());
				} else {
					sale.setProposalNumber(saleFile.getProposalNumber());
				}

				// CODIGO DE COMERCIO
				if (saleFile.getCommercialCode().length() == 0) {
					Exception ex = new SaleCommercialCodeNullException();
					errors.add(generateErrorMessageHeader(headers.getCommercialCode(), lineNumber) + ex.getMessage());
				} else if (saleFile.getCommercialCode().length() > 10) {
					Exception ex = new SaleCommercialCodeOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getCommercialCode(), lineNumber) + ex.getMessage());
				} else {
					sale.setCommerceCode(saleFile.getCommercialCode());
				}

				// PRODUCTO
				if (saleFile.getProduct().length() == 0) {
					Exception ex = new SaleProductNullException();
					errors.add(generateErrorMessageHeader(headers.getProduct(), lineNumber) + ex.getMessage());
				} else if (saleFile.getProduct().length() != 2) {
					Exception ex = new SaleProductOverflowException(2);
					errors.add(generateErrorMessageHeader(headers.getProduct(), lineNumber) + ex.getMessage());
				} else {
					Product product = null;
					for (Product productEntity : productsEntity) {
						if (saleFile.getProduct().equals(productEntity.getCode())) {
							product = productEntity;
							break;
						}
					}
					if (product == null) {
						Exception ex = new SaleProductInvalidException();
						errors.add(generateErrorMessageHeader(headers.getProduct(), lineNumber) + ex.getMessage());
					} else {
						sale.setProduct(product);
					}
				}

				// DESCRIPCION DEL PRODUCTO
				if (saleFile.getProductDescription().length() > 45) {
					Exception ex = new SaleProductDescriptionOverflowException(45);
					errors.add(
							generateErrorMessageHeader(headers.getProductDescription(), lineNumber) + ex.getMessage());
				} else {
					sale.setProductDescription(saleFile.getProductDescription());
				}

				// PERIODO DE COBRO
				if (saleFile.getCollectionPeriod().length() == 0) {
					Exception ex = new SaleCollectionPeriodNullException();
					errors.add(generateErrorMessageHeader(headers.getCollectionPeriod(), lineNumber) + ex.getMessage());
				} else if (saleFile.getCollectionPeriod().length() > 45) {
					Exception ex = new SaleCollectionPeriodOverflowException(45);
					errors.add(generateErrorMessageHeader(headers.getCollectionPeriod(), lineNumber) + ex.getMessage());
				} else {
					CollectionPeriod collectionPeriod = null;
					for (CollectionPeriod collectionPeriodEntity : collectionPeriodsEntity) {
						if (saleFile.getCollectionPeriod().equals(collectionPeriodEntity.getName())) {
							collectionPeriod = collectionPeriodEntity;
							break;
						}
					}
					if (collectionPeriod == null) {
						Exception ex = new SaleCollectionPeriodInvalidException();
						errors.add(generateErrorMessageHeader(headers.getCollectionPeriod(), lineNumber)
								+ ex.getMessage());
					} else {
						sale.setCollectionPeriod(collectionPeriod);
					}
				}

				// TIPO DE COBRO
				if (saleFile.getCollectionType().length() == 0) {
					Exception ex = new SaleCollectionTypeNullException();
					errors.add(generateErrorMessageHeader(headers.getCollectionType(), lineNumber) + ex.getMessage());
				} else if (saleFile.getCollectionType().length() > 15) {
					Exception ex = new SaleCollectionTypeOverflowException(15);
					errors.add(generateErrorMessageHeader(headers.getCollectionType(), lineNumber) + ex.getMessage());
				} else {
					sale.setCollectionType(saleFile.getCollectionType());
				}

				// BANCO
				if (saleFile.getBank().length() == 0) {
					Exception ex = new SaleBankNullException();
					errors.add(generateErrorMessageHeader(headers.getBank(), lineNumber) + ex.getMessage());
				} else if (saleFile.getBank().length() != 2) {
					Exception ex = new SaleBankOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getBank(), lineNumber) + ex.getMessage());
				} else if (!saleFile.getBank().equals(bank.getCode())) {
					Exception ex = new SaleBankInvalidException();
					errors.add(generateErrorMessageHeader(headers.getBank(), lineNumber) + ex.getMessage());
				} else {
					sale.setBank(bank);
				}

				// PRIMA
				if (saleFile.getInsurancePremium().length() == 0) {
					Exception ex = new SaleInsurancePremiumNullException();
					errors.add(generateErrorMessageHeader(headers.getInsurancePremium(), lineNumber) + ex.getMessage());
				} else if (saleFile.getInsurancePremium().length() > 8) {
					Exception ex = new SaleInsurancePremiumOverflowException(8);
					errors.add(generateErrorMessageHeader(headers.getInsurancePremium(), lineNumber) + ex.getMessage());
				} else {
					try {
						BigDecimal insurancePremium = new BigDecimal(saleFile.getInsurancePremium());
						if (insurancePremium.scale() > 2) {
							Exception ex = new SaleInsurancePremiumFormatException(2);
							errors.add(generateErrorMessageHeader(headers.getInsurancePremium(), lineNumber)
									+ ex.getMessage());
						} else {
							sale.setInsurancePremium(insurancePremium);
						}
					} catch (NumberFormatException e) {
						Exception ex = new SaleInsurancePremiumFormatException();
						errors.add(generateErrorMessageHeader(headers.getInsurancePremium(), lineNumber)
								+ ex.getMessage());
					}
				}

				// FECHA DE AUDITORIA
				if (saleFile.getAuditDate().length() == 0) {
					Exception ex = new SaleAuditDateNullException();
					errors.add(generateErrorMessageHeader(headers.getAuditDate(), lineNumber) + ex.getMessage());
				} else if (saleFile.getAuditDate().length() != 10) {
					Exception ex = new SaleAuditDateOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getAuditDate(), lineNumber) + ex.getMessage());
				} else {
					try {
						Date auditDate = sdf1.parse(saleFile.getAuditDate());
						sale.setAuditDate(auditDate);
					} catch (ParseException e) {
						Exception ex = new SaleAuditDateFormatException();
						errors.add(generateErrorMessageHeader(headers.getAuditDate(), lineNumber) + ex.getMessage());
					}
				}

				// USUARIO DE AUDITORIA
				if (saleFile.getAuditUser().length() == 0) {
					Exception ex = new SaleAuditUserNullException();
					errors.add(generateErrorMessageHeader(headers.getAuditUser(), lineNumber) + ex.getMessage());
				} else if (saleFile.getAuditUser().length() > 15) {
					Exception ex = new SaleAuditUserOverflowException(15);
					errors.add(generateErrorMessageHeader(headers.getAuditUser(), lineNumber) + ex.getMessage());
				} else {
					sale.setAuditUser(saleFile.getAuditUser());
				}

				// ESTADO DE VENTA
				if (saleFile.getState().length() == 0) {
					Exception ex = new SaleStateNullException();
					errors.add(generateErrorMessageHeader(headers.getState(), lineNumber) + ex.getMessage());
				} else {
					SaleStateEnum saleStateEnum = SaleStateEnum.findByName(saleFile.getState());
					if (saleStateEnum == null) {
						Exception ex = new SaleStateInvalidException();
						errors.add(generateErrorMessageHeader(headers.getState(), lineNumber) + ex.getMessage());
					} else {
						saleState.setState(saleStateEnum);
					}
				}

				// FECHA DE ESTADO DE VENTA
				if (saleFile.getStateDate().length() > 0 && saleFile.getStateDate().length() != 10) {
					Exception ex = new SaleStateDateOverflowException(10);
					errors.add(generateErrorMessageHeader(headers.getStateDate(), lineNumber) + ex.getMessage());
				} else if (saleFile.getStateDate().length() > 0) {
					try {
						Date stateDate = sdf1.parse(saleFile.getStateDate());
						saleState.setDate(stateDate);
					} catch (ParseException e) {
						Exception ex = new SaleStateDateFormatException();
						errors.add(generateErrorMessageHeader(headers.getStateDate(), lineNumber) + ex.getMessage());
					}
				}

				// USUARIO DE ESTADO
				if (saleFile.getDownUser().length() > 15) {
					Exception ex = new SaleDownUserOverflowException(15);
					errors.add(generateErrorMessageHeader(headers.getDownUser(), lineNumber) + ex.getMessage());
				} else {
					saleState.setUser(saleFile.getDownUser());
				}

				// CANAL DE ESTADO
				if (saleFile.getDownChannel().length() > 15) {
					Exception ex = new SaleDownChannelOverflowException(15);
					errors.add(generateErrorMessageHeader(headers.getDownChannel(), lineNumber) + ex.getMessage());
				} else {
					saleState.setChannel(saleFile.getDownChannel());
				}

				// MOTIVO DE ESTADO
				if (saleFile.getDownReason().length() > 30) {
					Exception ex = new SaleDownReasonOverflowException(30);
					errors.add(generateErrorMessageHeader(headers.getDownReason(), lineNumber) + ex.getMessage());
				} else {
					saleState.setReason(saleFile.getDownReason());
				}

				// OBSERVACION DE ESTADO
				if (saleFile.getDownObservation().length() > 2500) {
					Exception ex = new SaleDownObservationOverflowException(2500);
					errors.add(generateErrorMessageHeader(headers.getDownObservation(), lineNumber) + ex.getMessage());
				} else {
					saleState.setObservation(saleFile.getDownObservation());
				}

				// FECHA DE ACTUALIZACION DE TARJETA
				if (saleFile.getCreditCardUpdatedAt().length() != 10) {
					Exception ex = new CreditCardDateOverflowException(10);
					errors.add(
							generateErrorMessageHeader(headers.getCreditCardUpdatedAt(), lineNumber) + ex.getMessage());
				} else if (saleFile.getCreditCardUpdatedAt().length() > 0) {
					try {
						Date creditCardDate = sdf1.parse(saleFile.getCreditCardUpdatedAt());
						creditCard.setDate(creditCardDate);
					} catch (ParseException e) {
						Exception ex = new SaleCreditCardUpdatedAtFormatException();
						errors.add(generateErrorMessageHeader(headers.getCreditCardUpdatedAt(), lineNumber)
								+ ex.getMessage());
					}
				}

				// VERIFICA SI EXISTE LA VENTA
				if (sale.getBank() != null && sale.getProduct() != null && sale.getCollectionPeriod() != null) {
					Boolean saleExist = saleEao.checkExistSale(sale.getNuicInsured(), sale.getDate(),
							sale.getBank().getId(), sale.getProduct().getId(), sale.getCollectionPeriod().getId());
					if (saleExist) {
						Exception ex = new SaleAlreadyExistException();
						errors.add(generateErrorMessageHeader(lineNumber) + ex.getMessage());
					}
				}

				sale.setSaleState(saleState);
				sale.setCreditCard(creditCard);
				sale.setPayer(payer);
				sale.setCreatedBy(user);
				sale.setCreatedAt(currentDate);

				sales.add(sale);

				

			}

			System.out.println("errors:" + errors.size());

			if (errors.size() == 0) {

					lineNumber = 1;
					
					for (Sale sale : sales) {

						System.out.println("INSERTANDO"+lineNumber);

						SaleState saleState = sale.getSaleState();
						CreditCard creditCard = sale.getCreditCard();
						Payer payer = sale.getPayer();

						saleStateEao.add(saleState);
						creditCardEao.add(creditCard);
						payerEao.add(payer);

						sale.setSaleState(saleState);
						sale.setCreditCard(creditCard);
						sale.setPayer(payer);
						saleEao.add(sale);
						
						lote.setProcess(lineNumber);
						lote = loteEao.update(lote);
												
						lineNumber++;
						

					}
					
					lote.setState("Terminado");

					//userTransaction.commit();

				/*} catch (Exception e) {
					e.printStackTrace();
					userTransaction.setRollbackOnly();
					errors.add(e.getMessage());
					throw new ServiceException(errors);
				}*/
					
					userTransaction.commit();

					//futures.remove(future);

			} else {
				throw new ServiceException(errors);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			//lotes.remove(lote);
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				errors.add("Existen valores nulos.");
			} else {
				errors.add(e.getMessage());
			}
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e2) {
				errors.add(e2.getMessage());
				e2.printStackTrace();
			} 
			
		}
		
		

	}



	private String generateErrorMessageHeader(String columnName, int row) {
		return "Error en la fila " + row + " y columna " + columnName + ": ";
	}

	private String generateErrorMessageHeader(int row) {
		return "Error en la fila " + row + ": ";
	}


	
	
	
	
	

}


