package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

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
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.ControllerException;
import com.returnsoft.collection.exception.CreditCardDateOverflowException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultFormatException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultOverflowException;
import com.returnsoft.collection.exception.CreditCardExpirationDateFormatException;
import com.returnsoft.collection.exception.CreditCardExpirationDateOverflowException;
import com.returnsoft.collection.exception.CreditCardNumberFormatException;
import com.returnsoft.collection.exception.CreditCardNumberOverflowException;
import com.returnsoft.collection.exception.CreditCardStateOverflowException;
import com.returnsoft.collection.exception.FileMultipleErrorsException;
import com.returnsoft.collection.exception.FileExtensionException;
import com.returnsoft.collection.exception.FileNotFoundException;
import com.returnsoft.collection.exception.FileRowsZeroException;
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
import com.returnsoft.collection.exception.SaleDuplicateException;
import com.returnsoft.collection.exception.SaleFileRowsInvalidException;
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
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.LoteService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@Named
@RequestScoped
public class SearchLoteController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5267773436408492212L;

	@EJB
	private LoteService loteService;
	
	@Inject
	private FacesUtil facesUtil;
	
	private Date loteDate;
	
	private List<Lote> lotes;
	
	private Lote loteSelected;
	
	///////
	
	//private UploadedFile file;
	private Part file;
	//private String filename;
	private Integer FILE_ROWS = 49;
	//private List<String> errors;
	//private SaleFile headers;
	
	//private List<SaleFile> dataList;
	private Integer salesFileCount;
	
	
	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private BankService bankService;

	@EJB
	private ProductService productService;
	
	@EJB
	private SaleService saleService;
	
	
	public SearchLoteController(){
		System.out.println("Se construye SearchLoteController");
		//facesUtil = new FacesUtil();
	}
	
	public String initialize() {

		System.out.println("inicializando SearchLoteController");

		try {

			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");

			if (sessionBean != null && sessionBean.getUser() != null && sessionBean.getUser().getId() > 0) {

				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}

				return null;

			} else {
				throw new UserLoggedNotFoundException();
			}

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}
	
	public void search(){
		try {
			if (loteDate!=null) {
				lotes = loteService.findByDate(loteDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}
	
	
	//public void validateFile(FileUploadEvent event) {
	public String validateFile() {

		System.out.println("validateFile");

		try {
			
			//file = event.getFile();
			//file = (Part)value;
			System.out.println("fielname"+file.getName());
			System.out.println("fielname"+file.getSubmittedFileName());
			System.out.println("fielname"+file.getHeaderNames());
			System.out.println("fielname"+file.getSize());
			System.out.println("fielname"+file.getContentType());

			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");

			if (sessionBean.getBank() == null) {
				
				throw new BankNotSelectedException();
				
			} else if (file != null && file.getSize() > 0) {
				if (file.getContentType().equals("text/plain")) {

					getFileData(file.getName());

				} else {
					throw new FileExtensionException();
				}
			} else {
				throw new FileNotFoundException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				facesUtil.sendErrorMessage("Existen valores nulos.");
			} else {
				facesUtil.sendErrorMessage(e.getMessage());
			}
		}
		
		return null;

	}
	
	
	public void getFileData(String filename) throws ControllerException {

		System.out.println("getFileData");
		
		try {

			BufferedReader 
				br = new BufferedReader(
						new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
			
			
			String strLine = null;
			Integer numLine = 0;
			
			SaleFile headers = new SaleFile();
			List<SaleFile> dataList = new ArrayList<SaleFile>();

			while ((strLine = br.readLine()) != null) {

				System.out.println("numLine:" + numLine);

				if (numLine == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						throw new SaleFileRowsInvalidException(FILE_ROWS);
					} else {
						
						headers.setDocumentType(values[0]);
						headers.setNuicResponsible(values[1]);
						headers.setLastnamePaternalResponsible(values[2]);
						headers.setLastnameMaternalResponsible(values[3]);
						headers.setFirstnameResponsible(values[4]);

						headers.setCreditCardNumber(values[5]);
						headers.setAccountNumber(values[6]);
						headers.setCreditCardExpirationDate(values[7]);
						headers.setCreditCardState(values[8]);
						headers.setCreditCardDaysOfDefault(values[9]);

						headers.setNuicContractor(values[10]);
						headers.setLastnamePaternalContractor(values[11]);
						headers.setLastnameMaternalContractor(values[12]);
						headers.setFirstnameContractor(values[13]);
						headers.setNuicInsured(values[14]);

						headers.setLastnamePaternalInsured(values[15]);
						headers.setLastnameMaternalInsured(values[16]);
						headers.setFirstnameInsured(values[17]);
						headers.setPhone1(values[18]);
						headers.setPhone2(values[19]);

						headers.setMail(values[20]);
						headers.setDepartment(values[21]);
						headers.setProvince(values[22]);
						headers.setDistrict(values[23]);
						headers.setAddress(values[24]);

						headers.setDate(values[25]);
						headers.setChannel(values[26]);
						headers.setPlace(values[27]);
						headers.setVendorCode(values[28]);
						headers.setVendorName(values[29]);

						headers.setPolicyNumber(values[30]);
						headers.setCertificateNumber(values[31]);
						headers.setProposalNumber(values[32]);
						headers.setCommercialCode(values[33]);
						headers.setProduct(values[34]);

						headers.setProductDescription(values[35]);
						headers.setCollectionPeriod(values[36]);
						headers.setCollectionType(values[37]);
						headers.setBank(values[38]);
						headers.setInsurancePremium(values[39]);

						headers.setAuditDate(values[40]);
						headers.setAuditUser(values[41]);
						headers.setState(values[42]);
						headers.setStateDate(values[43]);
						headers.setDownUser(values[44]);

						headers.setDownChannel(values[45]);
						headers.setDownReason(values[46]);
						headers.setDownObservation(values[47]);
						headers.setCreditCardUpdatedAt(values[48]);

					}
				} else {

					String[] values = strLine.split("\\|", -1);

					if (values.length != FILE_ROWS) {
						throw new SaleFileRowsInvalidException(FILE_ROWS);
					} else {

						SaleFile saleFile = new SaleFile();
						saleFile.setDocumentType(values[0].trim());
						saleFile.setNuicResponsible(values[1].trim());
						saleFile.setLastnamePaternalResponsible(values[2].trim());
						saleFile.setLastnameMaternalResponsible(values[3].trim());
						saleFile.setFirstnameResponsible(values[4].trim());
						saleFile.setCreditCardNumber(values[5].trim());
						saleFile.setAccountNumber(values[6].trim());
						saleFile.setCreditCardExpirationDate(values[7].trim());
						saleFile.setCreditCardState(values[8].trim());
						saleFile.setCreditCardDaysOfDefault(values[9].trim());
						saleFile.setNuicContractor(values[10].trim());
						saleFile.setLastnamePaternalContractor(values[11].trim());
						saleFile.setLastnameMaternalContractor(values[12].trim());
						saleFile.setFirstnameContractor(values[13].trim());
						saleFile.setNuicInsured(values[14].trim());
						saleFile.setLastnamePaternalInsured(values[15].trim());
						saleFile.setLastnameMaternalInsured(values[16].trim());
						saleFile.setFirstnameInsured(values[17].trim());
						saleFile.setPhone1(values[18].trim());
						saleFile.setPhone2(values[19].trim());
						saleFile.setMail(values[20].trim());
						saleFile.setDepartment(values[21].trim());
						saleFile.setProvince(values[22].trim());
						saleFile.setDistrict(values[23].trim());
						saleFile.setAddress(values[24].trim());
						saleFile.setDate(values[25].trim());
						saleFile.setChannel(values[26].trim());
						saleFile.setPlace(values[27].trim());
						saleFile.setVendorCode(values[28].trim());
						saleFile.setVendorName(values[29].trim());
						saleFile.setPolicyNumber(values[30].trim());
						saleFile.setCertificateNumber(values[31].trim());
						saleFile.setProposalNumber(values[32].trim());
						saleFile.setCommercialCode(values[33].trim());
						saleFile.setProduct(values[34].trim());
						saleFile.setProductDescription(values[35].trim());
						saleFile.setCollectionPeriod(values[36].trim());
						saleFile.setCollectionType(values[37].trim());
						saleFile.setBank(values[38].trim());
						saleFile.setInsurancePremium(values[39].trim());
						saleFile.setAuditDate(values[40].trim());
						saleFile.setAuditUser(values[41].trim());
						saleFile.setState(values[42].trim());
						saleFile.setStateDate(values[43].trim());
						saleFile.setDownUser(values[44].trim());
						saleFile.setDownChannel(values[45].trim());
						saleFile.setDownReason(values[46].trim());
						saleFile.setDownObservation(values[47].trim());
						saleFile.setCreditCardUpdatedAt(values[48].trim());

						dataList.add(saleFile);

					}
				}

				numLine++;

			}

			if (dataList.size() > 0) {

				////////// VALIDANDO DUPLICADOS
				/////////////////////////////
				Set<String> dataSet = new HashSet<String>();
				Set<Integer> errorSet = new HashSet<Integer>();
				List<String> errors = new ArrayList<String>();
				numLine = 1;

				for (SaleFile saleFile : dataList) {
					String dataString = saleFile.getNuicInsured() + saleFile.getDate() + saleFile.getProduct()
							+ saleFile.getBank() + saleFile.getCollectionPeriod();
					numLine++;
					if (!dataSet.add(dataString)) {
						errorSet.add(numLine);
					}
				}

				for (Integer errorLineNumber : errorSet) {
					Exception ex = new SaleDuplicateException();
					errors.add(generateErrorMessageHeader(errorLineNumber) + ex.getMessage());
				}
				
				/////////////////////////////////////////
				//////////////////////////
				//////////////////////////////////////////
				
				if (errors.size()==0) {
					validateData(headers, dataList, filename);
				}else{
					throw new FileMultipleErrorsException(errors);
				}
				
			}else{
				throw new FileRowsZeroException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ControllerException(e);
		}

	}
	
	public void validateData(SaleFile headers, List<SaleFile> dataList, String filename) throws ControllerException{

		System.out.println("Validando vacíos");

		try {

			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");
			/*
			 * Short bankId = sessionBean.getBank().getId(); String bankName =
			 * sessionBean.getBank().getName();
			 */
			Bank bank = sessionBean.getBank();
			User user = sessionBean.getUser();
			Date currentDate = new Date();
			List<Product> productsEntity = productService.getAll();
			List<CollectionPeriod> collectionPeriodsEntity = collectionPeriodService.getAll();
			
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");
			Integer lineNumber = 1;
			
			List<Sale> sales = new ArrayList<Sale>();
			List<String> errors = new ArrayList<String>();

			for (SaleFile saleFile : dataList) {
				
				System.out.println("verificando ..");

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
				//System.out.println("saleFile.getFirstnameContractor()" + saleFile.getFirstnameContractor());
				if (saleFile.getFirstnameContractor().length() > 50) {
					Exception ex = new SaleFirstnameContractorOverFlowException(50);
					errors.add(
							generateErrorMessageHeader(headers.getFirstnameContractor(), lineNumber) + ex.getMessage());
				} else {
					sale.setFirstnameContractor(saleFile.getFirstnameContractor());
				}

				// NUIC DE ASEGURADO
				//System.out.println("saleFile.getNuicInsured():" + saleFile.getNuicInsured());
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
				if (sale.getBank()!=null && sale.getProduct()!=null && sale.getCollectionPeriod() !=null) {
					Boolean saleExist = saleService.checkIfExistSale(sale.getNuicInsured(), sale.getDate(),
							sale.getBank().getId(), sale.getProduct().getId(), sale.getCollectionPeriod().getId());
					if (saleExist) {
						Exception ex = new SaleAlreadyExistException();
						errors.add(generateErrorMessageHeader(lineNumber) + ex.getMessage());
					}
				}
				

				//sale.setCreatedBy(user);
				sale.setCreatedAt(currentDate);
				sale.setPayer(payer);
				sale.setCreditCard(creditCard);
				sale.setSaleState(saleState);

				sales.add(sale);

				lineNumber++;
				
			}

			//System.out.println("errors:" + errors.size());
			
			if (errors.size()==0) {
				loteService.add(sales, filename, headers, user.getId(), bank.getId());
				
			}else{
				throw new FileMultipleErrorsException(errors);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ControllerException(e);
			
		}

	}
	
	public String generateErrorMessageHeader(String columnName, int row) {
		return "Error en la fila " + row + " y columna " + columnName + ": ";
	}

	public String generateErrorMessageHeader(int row) {
		return "Error en la fila " + row + ": ";
	}


	public Date getLoteDate() {
		return loteDate;
	}

	public void setLoteDate(Date loteDate) {
		this.loteDate = loteDate;
	}

	public List<Lote> getLotes() {
		return lotes;
	}

	public void setLotes(List<Lote> lotes) {
		this.lotes = lotes;
	}

	public Lote getLoteSelected() {
		return loteSelected;
	}

	public void setLoteSelected(Lote loteSelected) {
		this.loteSelected = loteSelected;
	}

	/*public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}*/

	public Integer getSalesFileCount() {
		return salesFileCount;
	}

	public void setSalesFileCount(Integer salesFileCount) {
		this.salesFileCount = salesFileCount;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}
	
	
	

}
