package com.returnsoft.collection.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.returnsoft.collection.exception.CreditCardDateOverflowException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultFormatException;
import com.returnsoft.collection.exception.CreditCardDaysOfDefaultOverflowException;
import com.returnsoft.collection.exception.CreditCardExpirationDateFormatException;
import com.returnsoft.collection.exception.CreditCardExpirationDateOverflowException;
import com.returnsoft.collection.exception.CreditCardNumberFormatException;
import com.returnsoft.collection.exception.CreditCardNumberOverflowException;
import com.returnsoft.collection.exception.CreditCardStateOverflowException;
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
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.service.impl.SaleServiceBackground;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class SearchSalesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7381792511876093798L;

	@EJB
	private CollectionPeriodService collectionPeriodService;

	@EJB
	private BankService bankService;

	@EJB
	private ProductService productService;

	@EJB
	private SaleService saleService;
	
	@EJB
	private SaleServiceBackground saleServiceBackground;

	@EJB
	private UserService userService;

	private FacesUtil facesUtil;

	//////////// BUSCAR VENTAS
	/////////////////////////////

	private String searchTypeSelected;
	private String personTypeSelected;

	private String creditCardNumber;
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;

	private String nuicResponsible;
	private String lastnamePaternalResponsible;
	private String lastnameMaternalResponsible;
	private String firstnameResponsible;

	private String nuicContractor;
	private String lastnamePaternalContractor;
	private String lastnameMaternalContractor;
	private String firstnameContractor;

	private String nuicInsured;
	private String lastnamePaternalInsured;
	private String lastnameMaternalInsured;
	private String firstnameInsured;

	private List<SelectItem> banks;
	private String bankSelected;

	private List<SelectItem> products;
	private String productSelected;

	private List<SelectItem> saleStates;
	private String saleStateSelected;

	private Boolean searchByCreditCardNumberRendered;
	private Boolean searchByDocumentNumberResponsibleRendered;
	private Boolean searchByNamesRendered;
	private Boolean searchByDateSaleRendered;

	private Boolean searchByContractorRendered;
	private Boolean searchByInsuredRendered;
	private Boolean searchByResponsibleRendered;

	private List<Sale> sales;
	private Sale saleSelected;
	private Integer salesCount;

	///////
	// CREAR VENTAS

	private UploadedFile file;
	private String filename;
	private Integer FILE_ROWS = 49;

	private List<String> errors;
	private SaleFile headers;
	private List<SaleFile> dataList;
	private Integer salesFileCount;

	private Integer progress;
	
	private Future<Integer> loadStatus;
	

	////////////////////////////
	///////////////////

	public SearchSalesController() {
		System.out.println("Se construye SearchSaleController");
		facesUtil = new FacesUtil();
	}

	public String initialize() {

		System.out.println("inicializando SearchSaleController");

		try {

			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");

			if (sessionBean != null && sessionBean.getUser() != null && sessionBean.getUser().getId() > 0) {

				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}

				List<Bank> banksEntity = bankService.getAll();
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setValue(bank.getId().toString());
					item.setLabel(bank.getName());
					banks.add(item);
				}

				List<Product> productsEntity = productService.getAll();
				products = new ArrayList<SelectItem>();
				for (Product product : productsEntity) {
					SelectItem item = new SelectItem();
					item.setValue(product.getId().toString());
					item.setLabel(product.getName());
					products.add(item);
				}

				saleStates = new ArrayList<SelectItem>();
				for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
					SelectItem item = new SelectItem();
					item.setValue(saleStateEnum.getId());
					item.setLabel(saleStateEnum.getName());
					saleStates.add(item);
				}

				searchTypeSelected = "";

				System.out.println("searchTypeSelected:" + searchTypeSelected);

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

	public void handleFileUpload(FileUploadEvent event) {

		System.out.println(event.getFile().getFileName());
		file = event.getFile();
		dataList = null;
		errors = null;
		salesFileCount = 0;

		try {

			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");

			if (sessionBean.getBank() == null) {
				Exception e = new BankNotSelectedException();
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			} else if (file != null && file.getFileName().length() > 0) {
				if (file.getFileName().endsWith(".csv") || file.getFileName().endsWith(".CSV")) {

					////
					dataList = new ArrayList<SaleFile>();
					errors = new ArrayList<String>();
					filename = file.getFileName();

					getFileData();

					if (dataList.size() == 0) {
						dataList = null;
						Exception e = new FileRowsZeroException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
					} else if (errors.size() > 0) {
						dataList = null;
						for (String error : errors) {
							facesUtil.sendErrorMessage("", error);
						}
					} else {
						salesFileCount = dataList.size();
						facesUtil.sendConfirmMessage("",
								"Existen " + dataList.size() + " ventas en el archivo " + file.getFileName());
					}
				} else {
					dataList = null;
					throw new FileExtensionException();
				}
			} else {
				dataList = null;
				throw new FileNotFoundException();
			}

		} catch (Exception e) {
			e.printStackTrace();
			dataList = null;
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				facesUtil.sendErrorMessage("", "Existen valores nulos.");
			} else {
				facesUtil.sendErrorMessage("", e.getMessage());
			}

		}

	}

	public void getFileData() {

		System.out.println("ingreso a getData()");

		/*
		 * BufferedReader br = null; try {
		 */
		/*
		 * InputStreamReader isr = new InputStreamReader(file.getInputstream());
		 * System.out.println("------------------------------------");
		 * System.out.println(isr.getEncoding());
		 * System.out.println("------------------------------------");
		 * 
		 * InputStreamReader isr2 = new
		 * InputStreamReader(file.getInputstream(),StandardCharsets.UTF_8);
		 * System.out.println("------------------------------------");
		 * System.out.println(isr2.getEncoding());
		 * System.out.println("------------------------------------");
		 */

		// br = new BufferedReader(new
		// InputStreamReader(file.getInputStream()));
		// br = new BufferedReader(new InputStreamReader(file.getInputstream(),
		// StandardCharsets.UTF_8));

		/*
		 * } catch (IOException e1) { e1.printStackTrace();
		 * facesUtil.sendErrorMessage(e1.getClass().getSimpleName(),
		 * e1.getMessage()); } catch (Exception e1) { e1.printStackTrace();
		 * facesUtil.sendErrorMessage(e1.getClass().getSimpleName(),
		 * e1.getMessage()); }
		 */

		String strLine = null;
		Integer lineNumber = 0;

		errors = new ArrayList<String>();
		headers = new SaleFile();
		dataList = new ArrayList<SaleFile>();

		try {

			BufferedReader br = new BufferedReader(
					new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8));

			while ((strLine = br.readLine()) != null) {

				System.out.println("strLine2:" + strLine);

				if (lineNumber == 0) {
					// SE LEE CABECERA
					String[] values = strLine.split("\\|", -1);
					if (values.length != FILE_ROWS) {
						Exception ex = new SaleFileRowsInvalidException(FILE_ROWS);
						errors.add(generateErrorMessageHeader(0) + ex.getMessage());

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
						Exception ex = new SaleFileRowsInvalidException(FILE_ROWS);
						errors.add(generateErrorMessageHeader(0) + ex.getMessage());
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

				lineNumber++;

			}

			System.out.println("getData dataList:" + dataList.size());
			System.out.println("getData errors:" + errors.size());

			if (dataList.size() > 0) {

				////////// VALIDANDO DUPLICADOS
				/////////////////////////////
				Set<String> dataSet = new HashSet<String>();
				Set<Integer> errorSet = new HashSet<Integer>();
				lineNumber = 1;

				for (SaleFile saleFile : dataList) {
					String dataString = saleFile.getNuicInsured() + saleFile.getDate() + saleFile.getProduct()
							+ saleFile.getBank() + saleFile.getCollectionPeriod();
					lineNumber++;
					if (!dataSet.add(dataString)) {
						errorSet.add(lineNumber);
					}
				}

				for (Integer errorLineNumber : errorSet) {
					Exception ex = new SaleDuplicateException();
					errors.add(generateErrorMessageHeader(errorLineNumber) + ex.getMessage());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				errors.add("Existen valores nulos.");
			} else {
				errors.add(e.getMessage());
			}
		}

	}

	public void loadData() {
		try {

			if (dataList == null && dataList.size() == 0) {
				Exception e = new FileRowsZeroException();
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
				dataList = null;
				salesFileCount = 0;
			} else {
				//List<Sale> sales = validateData();
				
				//saleServiceBackground.add(sales, filename);
				
				SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap().get("sessionBean");
				/*
				 * Short bankId = sessionBean.getBank().getId(); String bankName =
				 * sessionBean.getBank().getName();
				 */
				
				saleServiceBackground.add(dataList, filename, headers, sessionBean.getUser().getId(), sessionBean.getBank().getId());
				
				/*if (errors != null && errors.size() > 0) {
					dataList = null;
					salesFileCount = 0;
					for (String error : errors) {
						facesUtil.sendErrorMessage("", error);
					}
				} else {
					createSales(sales);
					if (errors != null && errors.size() > 0) {
						dataList = null;
						salesFileCount = 0;
						for (String error : errors) {
							facesUtil.sendErrorMessage("", error);
						}
					} else {
						//System.out.println("en la 628!");
						facesUtil.sendConfirmMessage("", "Se crearon " + sales.size() + " ventas.");
						//facesUtil.sendConfirmMessage("", "Se crearon ventas.");
						dataList = null;
						salesFileCount = 0;
					}
				}*/
			}
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("EXXCEPTION!!!!!!!!!!!!!!!!!!!!");
			System.out.println("EXXCEPTION!!!!!!!!!!!!!!!!!!!!");
			
			System.out.println(e instanceof ServiceException);
			System.out.println("EXXCEPTION!!!!!!!!!!!!!!!!!!!!");
			System.out.println("EXXCEPTION!!!!!!!!!!!!!!!!!!!!");
			System.out.println("EXXCEPTION!!!!!!!!!!!!!!!!!!!!");
			dataList = null;
			salesFileCount = 0;
			
			if (e instanceof ServiceException) {
				if (((ServiceException) e).getErrors()!=null) {
					for (String error : ((ServiceException) e).getErrors()) {
						facesUtil.sendErrorMessage("", error);
					}
				}
			}else if (e.getMessage() == null || e.getMessage().length() == 0) {
				facesUtil.sendErrorMessage("", "Existen valores nulos3333.");
			} else {
				facesUtil.sendErrorMessage("", e.getMessage());
			}
			
		}
	}

	public List<Sale> validateData() {

		System.out.println("Validando vacíos");

		Integer lineNumber = 1;

		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("MM/yyyy");

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

			Lote lote = new Lote();
			lote.setName(filename);

			List<Sale> sales = new ArrayList<Sale>();

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
				

				sale.setCreatedBy(user);
				sale.setCreatedAt(currentDate);
				sale.setPayer(payer);
				sale.setCreditCard(creditCard);
				sale.setSaleState(saleState);
				sale.setLote(lote);

				sales.add(sale);

				lineNumber++;
				
				System.out.println("termino de verificar"+lineNumber);

			}

			System.out.println("errors:" + errors.size());

			return sales;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				errors.add("Existen valores nulos.");
			} else {
				errors.add(e.getMessage());
			}
			return null;
		}

	}

	public void createSales(List<Sale> sales) {

		System.out.println("ingreso a createSales");

		// Integer lineNumber = 1;
		progress = 0;

		// for (Sale sale : sales) {
		// System.out.println("lineNumber:" + lineNumber);
		try {
			
			
			
			//saleServiceBackground.add(sales, filename);
			
			if (loadStatus!=null) {
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS"+loadStatus.get());
			}else{
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				System.out.println("SSSSSSSSSSSSSSSSSSSSSSSS es nulo");
				
			}
			
			
			
		} catch (Exception e) {
			System.out.println("EXXXXXXX");
			System.out.println("EXXXXXXX");
			System.out.println("EXXXXXXX");
			System.out.println("EXXXXXXX");
			System.out.println("EXXXXXXX");
			
			loadStatus=null;
			if (e.getMessage() == null || e.getMessage().length() == 0) {
				errors.add("Existen valores nulos2.");
			} else {
				errors.add(e.getMessage());
			}
		}
		// progress = (lineNumber * 100) / sales.size();
		// lineNumber++;
		// }

		//progress = 0;
	}

	public String generateErrorMessageHeader(String columnName, int row) {
		return "Error en la fila " + row + " y columna " + columnName + ": ";
	}

	public String generateErrorMessageHeader(int row) {
		return "Error en la fila " + row + ": ";
	}

	/*
	 * private String getFilename(Part part) { for (String cd :
	 * part.getHeader("content-disposition").split(";")) { if
	 * (cd.trim().startsWith("filename")) { String filename =
	 * cd.substring(cd.indexOf('=') + 1).trim().replace("\"", ""); return
	 * filename.substring(filename.lastIndexOf('/') +
	 * 1).substring(filename.lastIndexOf('\\') + 1); // MSIE // fix. } } return
	 * null; }
	 */

	// public void handleFileUpload(/*AjaxBehaviorEvent event*/) {
	/*
	 * System.out.println("file size: " + file.getSize()); System.out.println(
	 * "file type: " + file.getContentType()); System.out.println("file info: "
	 * + file.getHeader("Content-Disposition"));
	 */

	public void onChangeSearchType() {
		try {
			// if (fromRequest) {
			searchTypeSelected = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form:searchType_input");
			// }

			System.out.println("onChangeSearchBy" + searchTypeSelected);

			searchByContractorRendered = false;
			searchByInsuredRendered = false;
			searchByResponsibleRendered = false;
			personTypeSelected = null;

			if (searchTypeSelected.equals("creditCard")) {
				searchByCreditCardNumberRendered = true;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			} else if (searchTypeSelected.equals("dni")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = true;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			} else if (searchTypeSelected.equals("saleData")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = true;
			} else if (searchTypeSelected.equals("personalData")) {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = true;
				searchByDateSaleRendered = false;
			} else {
				searchByCreditCardNumberRendered = false;
				searchByDocumentNumberResponsibleRendered = false;
				searchByNamesRendered = false;
				searchByDateSaleRendered = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void onChangePersonType() {
		try {
			// if (fromRequest) {
			personTypeSelected = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form:personType_input");
			// }

			System.out.println("onChangeSearchBy" + searchTypeSelected);
			if (personTypeSelected.equals("contractor")) {
				searchByContractorRendered = true;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = false;
			} else if (personTypeSelected.equals("insured")) {
				searchByContractorRendered = false;
				searchByInsuredRendered = true;
				searchByResponsibleRendered = false;
			} else if (personTypeSelected.equals("responsible")) {
				searchByContractorRendered = false;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = true;
			} else {
				searchByContractorRendered = false;
				searchByInsuredRendered = false;
				searchByResponsibleRendered = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void search() {

		try {

			saleSelected = null;

			if (searchTypeSelected.equals("creditCard")) {
				Long creditCardNumberLong = Long.parseLong(creditCardNumber);
				sales = saleService.findSalesByCreditCardNumber(creditCardNumberLong);
			} else if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				sales = saleService.findSalesByNuicResponsible(nuicResponsibleLong);
			} else if (searchTypeSelected.equals("saleData")) {
				Short productId = null;
				if (productSelected != null && productSelected.length() > 0) {
					productId = Short.parseShort(productSelected);
				}
				Short bankId = null;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				}
				SaleStateEnum saleState = null;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				}
				
				System.out.println("Se inicia la busqueda XXX");
				
				sales = saleService.findSalesBySaleData(dateOfSaleStarted, dateOfSaleEnded, bankId,
						productId, saleState);
				
				System.out.println("Se termina la busqueda XXX");

			} else if (searchTypeSelected.equals("personalData")) {
				if (personTypeSelected.equals("contractor")) {

					if ((nuicContractor != null && nuicContractor.length() > 0)
							|| (firstnameContractor != null && firstnameContractor.length() > 0)
							|| (lastnamePaternalContractor != null && lastnamePaternalContractor.length() > 0)
							|| (lastnameMaternalContractor != null && lastnameMaternalContractor.length() > 0)) {

						Long nuicContractorLong = null;
						if (nuicContractor != null && nuicContractor.length() > 0) {
							nuicContractorLong = Long.parseLong(nuicContractor);
						}

						sales = saleService.findSalesByNamesContractor(nuicContractorLong, firstnameContractor,
								lastnamePaternalContractor, lastnameMaternalContractor);

					} else {
						FacesMessage msg = new FacesMessage("Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("insured")) {

					if ((nuicInsured != null && nuicInsured.length() > 0)
							|| (firstnameInsured != null && firstnameInsured.length() > 0)
							|| (lastnamePaternalInsured != null && lastnamePaternalInsured.length() > 0)
							|| (lastnameMaternalInsured != null && lastnameMaternalInsured.length() > 0)) {

						Long nuicInsuredLong = null;
						if (nuicInsured != null && nuicInsured.length() > 0) {
							nuicInsuredLong = Long.parseLong(nuicInsured);
						}

						sales = saleService.findSalesByNamesInsured(nuicInsuredLong, firstnameInsured,
								lastnamePaternalInsured, lastnameMaternalInsured);

					} else {
						FacesMessage msg = new FacesMessage("Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("responsible")) {

					if ((nuicResponsible != null && nuicResponsible.length() > 0)
							|| (firstnameResponsible != null && firstnameResponsible.length() > 0)
							|| (lastnamePaternalResponsible != null && lastnamePaternalResponsible.length() > 0)
							|| (lastnameMaternalResponsible != null && lastnameMaternalResponsible.length() > 0)) {

						Long nuicResponsibleLong = null;
						if (nuicResponsible != null && nuicResponsible.length() > 0) {
							nuicResponsibleLong = Long.parseLong(nuicResponsible);
						}

						sales = saleService.findSalesByNamesResponsible(nuicResponsibleLong, firstnameResponsible,
								lastnamePaternalResponsible, lastnameMaternalResponsible);

					} else {
						FacesMessage msg = new FacesMessage("Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void exportCSV() throws IOException {

		try {

			StringBuilder cadena = new StringBuilder();
			String separator = "|";
			String header = "";

			header += "COD UNICO" + separator;
			header += "TIPO DOC" + separator;
			header += "NUIC RESP PAGO" + separator;
			header += "AP PAT RESP PAGO" + separator;
			header += "AP MAT RESP PAGO" + separator;
			header += "NOMBRES RESP PAGO" + separator;
			header += "N° TARJETA DE CREDITO" + separator;
			header += "N° DE CUENTA" + separator;
			header += "FECHA VENC TARJETA" + separator;
			header += "DIAS MORA" + separator;
			header += "NUIC CONTRATANTE" + separator;
			header += "AP PAT CONTRATANTE" + separator;
			header += "AP MAT CONTRATANTE" + separator;
			header += "NOMBRES CONTRATANTE" + separator;
			header += "NUIC ASEGURADO" + separator;
			header += "AP PAT ASEGURADO" + separator;
			header += "AP MAT ASEGURADO" + separator;
			header += "NOMBRES ASEGURADO" + separator;
			header += "TELEFONO 1" + separator;
			header += "TELEFONO 2" + separator;
			header += "E-MAIL" + separator;
			header += "DEPARTAMENTO" + separator;
			header += "PROVINCIA" + separator;
			header += "DISTRITO" + separator;
			header += "DIRECCION" + separator;
			header += "FECHA DE VENTA" + separator;
			header += "CANAL DE VENTA" + separator;
			header += "LUGAR DE VENTA" + separator;
			header += "COD DE VENDEDOR" + separator;
			header += "NOMBRE DE VENDEDOR" + separator;
			header += "# DE POLIZA" + separator;
			header += "# CERTIFICADO" + separator;
			header += "# PROPUESTA" + separator;
			header += "CODIGO DE COMERCIO" + separator;
			header += "PRODUCTO" + separator;
			header += "DESCRIPCION DEL PRODUCTO" + separator;
			header += "PERIODO DE COBRO" + separator;
			header += "TIPO DE COBRO" + separator;
			header += "BANCO" + separator;
			header += "PRIMA" + separator;
			header += "FECHA DE AUDITORIA" + separator;
			header += "USUARIO DE AUDITORIA" + separator;
			header += "ESTADO" + separator;
			header += "FECHA ESTADO" + separator;
			header += "USUARIO BAJA" + separator;
			header += "CANAL BAJA" + separator;
			header += "MOTIVO BAJA" + separator;
			header += "OBSERVACION BAJA" + separator;
			header += "FECHA ACT TC" + separator;
			header += "FECHA CREACION" + separator;
			header += "USUARIO CREACION";

			cadena.append(header);
			cadena.append("\r\n");

			search();

			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			for (Sale sale : sales) {

				cadena.append(sale.getCode() + separator);
				cadena.append(sale.getPayer().getDocumentType().getName() + separator);
				cadena.append(sale.getPayer().getNuicResponsible() + separator);
				cadena.append(sale.getPayer().getLastnamePaternalResponsible() + separator);
				cadena.append(sale.getPayer().getLastnameMaternalResponsible() + separator);
				cadena.append(sale.getPayer().getFirstnameResponsible() + separator);
				cadena.append(sale.getCreditCard().getNumber() + separator);
				cadena.append(sale.getAccountNumber() + separator);
				cadena.append(sale.getCreditCard().getExpirationDate() != null
						? sdf1.format(sale.getCreditCard().getExpirationDate()) + separator : separator);
				cadena.append(sale.getCreditCard().getDaysOfDefault() != null
						? sale.getCreditCard().getDaysOfDefault().toString() + separator : separator);

				cadena.append(
						sale.getNuicContractor() != null ? sale.getNuicContractor().toString() + separator : separator);
				cadena.append(sale.getLastnamePaternalContractor() + separator);
				cadena.append(sale.getLastnameMaternalContractor() + separator);
				cadena.append(sale.getFirstnameContractor() + separator);
				cadena.append(sale.getNuicInsured() + separator);
				cadena.append(sale.getLastnamePaternalInsured() + separator);
				cadena.append(sale.getLastnameMaternalInsured() + separator);
				cadena.append(sale.getFirstnameInsured() + separator);
				cadena.append(sale.getPhone1() + separator);
				cadena.append(sale.getPhone2() + separator);
				cadena.append(sale.getPayer().getMail() + separator);
				cadena.append(sale.getPayer().getDepartment() + separator);
				cadena.append(sale.getPayer().getProvince() + separator);
				cadena.append(sale.getPayer().getDistrict() + separator);
				cadena.append(sale.getPayer().getAddress() + separator);
				cadena.append(sdf2.format(sale.getDate()) + separator);
				cadena.append(sale.getChannel() + separator);
				cadena.append(sale.getPlace() + separator);
				cadena.append(sale.getVendorCode() + separator);
				cadena.append(sale.getVendorName() + separator);
				cadena.append(sale.getPolicyNumber() + separator);
				cadena.append(sale.getCertificateNumber() + separator);
				cadena.append(sale.getProposalNumber() + separator);
				cadena.append(sale.getCommerceCode() + separator);
				cadena.append(sale.getProduct().getName() + separator);
				cadena.append(sale.getProductDescription() + separator);
				cadena.append(sale.getCollectionPeriod() + separator);
				cadena.append(sale.getCollectionType() + separator);
				cadena.append(sale.getBank().getName() + separator);
				cadena.append(sale.getInsurancePremium().doubleValue() + separator);
				cadena.append(sdf2.format(sale.getAuditDate()) + separator);
				cadena.append(sale.getAuditUser() + separator);
				cadena.append(sale.getSaleState().getState().getName() + separator);
				cadena.append(sale.getSaleState().getDate() != null
						? sdf2.format(sale.getSaleState().getDate()) + separator : separator);
				cadena.append(sale.getSaleState().getUser() + separator);
				cadena.append(sale.getSaleState().getChannel() + separator);
				cadena.append(sale.getSaleState().getReason() + separator);
				cadena.append(sale.getSaleState().getObservation() + separator);
				cadena.append(sale.getCreditCard().getDate() != null
						? sdf2.format(sale.getCreditCard().getDate()) + separator : separator);

				cadena.append(sdf3.format(sale.getCreatedAt()) + separator);
				cadena.append(sale.getCreatedBy().getUsername());
				cadena.append("\r\n");

			}

			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

			response.reset();
			response.setContentType("text/comma-separated-values");
			response.setHeader("Content-Disposition", "attachment; filename=\"ventas.csv\"");

			OutputStream output = response.getOutputStream();

			// for (String s : strings) {
			output.write(cadena.toString().getBytes());
			// }

			output.flush();
			output.close();

			fc.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void exportExcel() throws IOException {

		try {

			search();

			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet();
			XSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("COD UNICO");
			row.createCell(1).setCellValue("TIPO DOC");
			row.createCell(2).setCellValue("NUIC RESP PAGO");
			row.createCell(3).setCellValue("AP PAT RESP PAGO");
			row.createCell(4).setCellValue("AP MAT RESP PAGO");
			row.createCell(5).setCellValue("NOMBRES RESP PAGO");
			row.createCell(6).setCellValue("N° TARJETA DE CREDITO");
			row.createCell(7).setCellValue("N° DE CUENTA");
			row.createCell(8).setCellValue("FECHA VENC TARJETA");
			row.createCell(9).setCellValue("DIAS MORA");
			row.createCell(10).setCellValue("NUIC CONTRATANTE");
			row.createCell(11).setCellValue("AP PAT CONTRATANTE");
			row.createCell(12).setCellValue("AP MAT CONTRATANTE");
			row.createCell(13).setCellValue("NOMBRES CONTRATANTE");
			row.createCell(14).setCellValue("NUIC ASEGURADO");
			row.createCell(15).setCellValue("AP PAT ASEGURADO");
			row.createCell(16).setCellValue("AP MAT ASEGURADO");
			row.createCell(17).setCellValue("NOMBRES ASEGURADO");
			row.createCell(18).setCellValue("TELEFONO 1");
			row.createCell(19).setCellValue("TELEFONO 2");
			row.createCell(20).setCellValue("E-MAIL");
			row.createCell(21).setCellValue("DEPARTAMENTO");
			row.createCell(22).setCellValue("PROVINCIA");
			row.createCell(23).setCellValue("DISTRITO");
			row.createCell(24).setCellValue("DIRECCION");
			row.createCell(25).setCellValue("FECHA DE VENTA");
			row.createCell(26).setCellValue("CANAL DE VENTA");
			row.createCell(27).setCellValue("LUGAR DE VENTA");
			row.createCell(28).setCellValue("COD DE VENDEDOR");
			row.createCell(29).setCellValue("NOMBRE DE VENDEDOR");
			row.createCell(30).setCellValue("# DE POLIZA");
			row.createCell(31).setCellValue("# CERTIFICADO");
			row.createCell(32).setCellValue("# PROPUESTA");
			row.createCell(33).setCellValue("CODIGO DE COMERCIO");
			row.createCell(34).setCellValue("PRODUCTO");
			row.createCell(35).setCellValue("DESCRIPCION DEL PRODUCTO");
			row.createCell(36).setCellValue("PERIODO DE COBRO");
			row.createCell(37).setCellValue("TIPO DE COBRO");
			row.createCell(38).setCellValue("BANCO");
			row.createCell(39).setCellValue("PRIMA");
			row.createCell(40).setCellValue("FECHA DE AUDITORIA");
			row.createCell(41).setCellValue("USUARIO DE AUDITORIA");
			row.createCell(42).setCellValue("ESTADO");
			row.createCell(43).setCellValue("FECHA ESTADO");
			row.createCell(44).setCellValue("USUARIO BAJA");
			row.createCell(45).setCellValue("CANAL BAJA");
			row.createCell(46).setCellValue("MOTIVO BAJA");
			row.createCell(47).setCellValue("OBSERVACION BAJA");
			row.createCell(48).setCellValue("FECHA ACT TC");

			row.createCell(49).setCellValue("FECHA CREACION");
			row.createCell(50).setCellValue("USUARIO CREACION");

			// for (int i = 0; i < sales.size(); i++) {

			int lineNumber = 1;

			for (Sale sale : sales) {

				System.out.println("Procesando a excel:" + lineNumber);
				// Sale sale = sales.get(i);
				XSSFRow rowBody = sheet.createRow(lineNumber);

				rowBody.createCell(0).setCellValue(sale.getCode());
				rowBody.createCell(1).setCellValue(sale.getPayer().getDocumentType().getName());
				rowBody.createCell(2).setCellValue(sale.getPayer().getNuicResponsible());
				rowBody.createCell(3).setCellValue(sale.getPayer().getLastnamePaternalResponsible());
				rowBody.createCell(4).setCellValue(sale.getPayer().getLastnameMaternalResponsible());
				rowBody.createCell(5).setCellValue(sale.getPayer().getFirstnameResponsible());
				rowBody.createCell(6).setCellValue(sale.getCreditCard().getNumber() + "");
				rowBody.createCell(7).setCellValue(sale.getAccountNumber() + "");
				rowBody.createCell(8).setCellValue(sale.getCreditCard().getExpirationDate() != null
						? sdf1.format(sale.getCreditCard().getExpirationDate()) : null);
				rowBody.createCell(9).setCellValue(sale.getCreditCard().getDaysOfDefault() != null
						? sale.getCreditCard().getDaysOfDefault().toString() : "");

				rowBody.createCell(10)
						.setCellValue(sale.getNuicContractor() != null ? sale.getNuicContractor().toString() : "");
				rowBody.createCell(11).setCellValue(sale.getLastnamePaternalContractor());
				rowBody.createCell(12).setCellValue(sale.getLastnameMaternalContractor());
				rowBody.createCell(13).setCellValue(sale.getFirstnameContractor());
				rowBody.createCell(14).setCellValue(sale.getNuicInsured());
				rowBody.createCell(15).setCellValue(sale.getLastnamePaternalInsured());
				rowBody.createCell(16).setCellValue(sale.getLastnameMaternalInsured());
				rowBody.createCell(17).setCellValue(sale.getFirstnameInsured());
				rowBody.createCell(18).setCellValue(sale.getPhone1());
				rowBody.createCell(19).setCellValue(sale.getPhone2());
				rowBody.createCell(20).setCellValue(sale.getPayer().getMail());
				rowBody.createCell(21).setCellValue(sale.getPayer().getDepartment());
				rowBody.createCell(22).setCellValue(sale.getPayer().getProvince());
				rowBody.createCell(23).setCellValue(sale.getPayer().getDistrict());
				rowBody.createCell(24).setCellValue(sale.getPayer().getAddress());
				rowBody.createCell(25).setCellValue(sdf2.format(sale.getDate()));
				rowBody.createCell(26).setCellValue(sale.getChannel());
				rowBody.createCell(27).setCellValue(sale.getPlace());
				rowBody.createCell(28).setCellValue(sale.getVendorCode());
				rowBody.createCell(29).setCellValue(sale.getVendorName());
				rowBody.createCell(30).setCellValue(sale.getPolicyNumber());
				rowBody.createCell(31).setCellValue(sale.getCertificateNumber());
				rowBody.createCell(32).setCellValue(sale.getProposalNumber());
				rowBody.createCell(33).setCellValue(sale.getCommerceCode());
				rowBody.createCell(34).setCellValue(sale.getProduct().getName());
				rowBody.createCell(35).setCellValue(sale.getProductDescription());
				rowBody.createCell(36).setCellValue(sale.getCollectionPeriod().getName());
				rowBody.createCell(37).setCellValue(sale.getCollectionType());
				rowBody.createCell(38).setCellValue(sale.getBank().getName());
				rowBody.createCell(39).setCellValue(sale.getInsurancePremium().doubleValue());
				rowBody.createCell(40).setCellValue(sdf2.format(sale.getAuditDate()));
				rowBody.createCell(41).setCellValue(sale.getAuditUser());
				rowBody.createCell(42).setCellValue(sale.getSaleState().getState().getName());
				rowBody.createCell(43).setCellValue(
						sale.getSaleState().getDate() != null ? sdf2.format(sale.getSaleState().getDate()) : "");
				rowBody.createCell(44).setCellValue(sale.getSaleState().getUser());
				rowBody.createCell(45).setCellValue(sale.getSaleState().getChannel());
				rowBody.createCell(46).setCellValue(sale.getSaleState().getReason());
				rowBody.createCell(47).setCellValue(sale.getSaleState().getObservation());
				rowBody.createCell(48).setCellValue(
						sale.getCreditCard().getDate() != null ? sdf2.format(sale.getCreditCard().getDate()) : "");

				rowBody.createCell(49).setCellValue(sdf3.format(sale.getCreatedAt()));
				rowBody.createCell(50).setCellValue(sale.getCreatedBy().getUsername());

				lineNumber++;

			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			// externalContext.setResponseContentType("application/vnd.ms-excel");
			externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"ventas.xlsx\"");

			wb.write(externalContext.getResponseOutputStream());
			wb.close();
			facesContext.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void download() {
		try {

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
					.getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String fileName = rootPath + "resources" + separator + "templates" + separator + "tramas_ventas.xlsx";
			File file = new File(fileName);
			InputStream pdfInputStream = new FileInputStream(file);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"tramas_ventas.xlsx\"");

			// Read PDF contents and write them to the output
			byte[] bytesBuffer = new byte[2048];
			int bytesRead;

			while ((bytesRead = pdfInputStream.read(bytesBuffer)) > 0) {
				externalContext.getResponseOutputStream().write(bytesBuffer, 0, bytesRead);
			}

			externalContext.getResponseOutputStream().flush();
			externalContext.getResponseOutputStream().close();
			pdfInputStream.close();
			facesContext.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	public Date getDateOfSaleStarted() {
		return dateOfSaleStarted;
	}

	public void setDateOfSaleStarted(Date dateOfSaleStarted) {
		this.dateOfSaleStarted = dateOfSaleStarted;
	}

	public Date getDateOfSaleEnded() {
		return dateOfSaleEnded;
	}

	public void setDateOfSaleEnded(Date dateOfSaleEnded) {
		this.dateOfSaleEnded = dateOfSaleEnded;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public String getLastnamePaternalResponsible() {
		return lastnamePaternalResponsible;
	}

	public void setLastnamePaternalResponsible(String lastnamePaternalResponsible) {
		this.lastnamePaternalResponsible = lastnamePaternalResponsible;
	}

	public String getLastnameMaternalResponsible() {
		return lastnameMaternalResponsible;
	}

	public void setLastnameMaternalResponsible(String lastnameMaternalResponsible) {
		this.lastnameMaternalResponsible = lastnameMaternalResponsible;
	}

	public String getFirstnameResponsible() {
		return firstnameResponsible;
	}

	public void setFirstnameResponsible(String firstnameResponsible) {
		this.firstnameResponsible = firstnameResponsible;
	}

	public String getFirstnameContractor() {
		return firstnameContractor;
	}

	public void setFirstnameContractor(String firstnameContractor) {
		this.firstnameContractor = firstnameContractor;
	}

	public String getFirstnameInsured() {
		return firstnameInsured;
	}

	public void setFirstnameInsured(String firstnameInsured) {
		this.firstnameInsured = firstnameInsured;
	}

	public String getLastnamePaternalContractor() {
		return lastnamePaternalContractor;
	}

	public void setLastnamePaternalContractor(String lastnamePaternalContractor) {
		this.lastnamePaternalContractor = lastnamePaternalContractor;
	}

	public String getLastnameMaternalContractor() {
		return lastnameMaternalContractor;
	}

	public void setLastnameMaternalContractor(String lastnameMaternalContractor) {
		this.lastnameMaternalContractor = lastnameMaternalContractor;
	}

	public String getLastnamePaternalInsured() {
		return lastnamePaternalInsured;
	}

	public void setLastnamePaternalInsured(String lastnamePaternalInsured) {
		this.lastnamePaternalInsured = lastnamePaternalInsured;
	}

	public String getLastnameMaternalInsured() {
		return lastnameMaternalInsured;
	}

	public void setLastnameMaternalInsured(String lastnameMaternalInsured) {
		this.lastnameMaternalInsured = lastnameMaternalInsured;
	}

	public List<SelectItem> getBanks() {
		return banks;
	}

	public void setBanks(List<SelectItem> banks) {
		this.banks = banks;
	}

	public String getBankSelected() {
		return bankSelected;
	}

	public void setBankSelected(String bankSelected) {
		this.bankSelected = bankSelected;
	}

	public List<SelectItem> getProducts() {
		return products;
	}

	public void setProducts(List<SelectItem> products) {
		this.products = products;
	}

	public String getProductSelected() {
		return productSelected;
	}

	public void setProductSelected(String productSelected) {
		this.productSelected = productSelected;
	}

	public String getSearchTypeSelected() {
		return searchTypeSelected;
	}

	public void setSearchTypeSelected(String searchTypeSelected) {
		this.searchTypeSelected = searchTypeSelected;
	}

	public Boolean getSearchByCreditCardNumberRendered() {
		return searchByCreditCardNumberRendered;
	}

	public void setSearchByCreditCardNumberRendered(Boolean searchByCreditCardNumberRendered) {
		this.searchByCreditCardNumberRendered = searchByCreditCardNumberRendered;
	}

	public Boolean getSearchByDocumentNumberResponsibleRendered() {
		return searchByDocumentNumberResponsibleRendered;
	}

	public void setSearchByDocumentNumberResponsibleRendered(Boolean searchByDocumentNumberResponsibleRendered) {
		this.searchByDocumentNumberResponsibleRendered = searchByDocumentNumberResponsibleRendered;
	}

	public Boolean getSearchByNamesRendered() {
		return searchByNamesRendered;
	}

	public void setSearchByNamesRendered(Boolean searchByNamesRendered) {
		this.searchByNamesRendered = searchByNamesRendered;
	}

	public Boolean getSearchByDateSaleRendered() {
		return searchByDateSaleRendered;
	}

	public void setSearchByDateSaleRendered(Boolean searchByDateSaleRendered) {
		this.searchByDateSaleRendered = searchByDateSaleRendered;
	}

	public String getPersonTypeSelected() {
		return personTypeSelected;
	}

	public void setPersonTypeSelected(String personTypeSelected) {
		this.personTypeSelected = personTypeSelected;
	}

	public Boolean getSearchByContractorRendered() {
		return searchByContractorRendered;
	}

	public void setSearchByContractorRendered(Boolean searchByContractorRendered) {
		this.searchByContractorRendered = searchByContractorRendered;
	}

	public Boolean getSearchByInsuredRendered() {
		return searchByInsuredRendered;
	}

	public void setSearchByInsuredRendered(Boolean searchByInsuredRendered) {
		this.searchByInsuredRendered = searchByInsuredRendered;
	}

	public Boolean getSearchByResponsibleRendered() {
		return searchByResponsibleRendered;
	}

	public void setSearchByResponsibleRendered(Boolean searchByResponsibleRendered) {
		this.searchByResponsibleRendered = searchByResponsibleRendered;
	}

	public String getNuicResponsible() {
		return nuicResponsible;
	}

	public void setNuicResponsible(String nuicResponsible) {
		this.nuicResponsible = nuicResponsible;
	}

	public String getNuicContractor() {
		return nuicContractor;
	}

	public void setNuicContractor(String nuicContractor) {
		this.nuicContractor = nuicContractor;
	}

	public String getNuicInsured() {
		return nuicInsured;
	}

	public void setNuicInsured(String nuicInsured) {
		this.nuicInsured = nuicInsured;
	}

	public Sale getSaleSelected() {
		return saleSelected;
	}

	public void setSaleSelected(Sale saleSelected) {
		this.saleSelected = saleSelected;
	}

	public String getSaleStateSelected() {
		return saleStateSelected;
	}

	public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}



	public List<SelectItem> getSaleStates() {
		return saleStates;
	}

	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public Integer getProgress() {
		System.out.println("Ingreso a getProgress");
		
		List<Future<Lote>> futures = saleServiceBackground.getFutures();
		
		if (futures.size()>0) {
			for (Future<Lote> future : futures) {
				try {
					System.out.println("GETPROCESS"+future.get().getProcess());
					System.out.println("GETPROCESS"+future.get().getProcess());
					System.out.println("GETPROCESS"+future.get().getProcess());
					System.out.println("GETPROCESS"+future.get().getProcess());
					System.out.println("GETPROCESS"+future.get().getProcess());
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}	
		}else{
			System.out.println("FUTURES SON NULOS");
		}
		
		
		
		return progress;
		
//		if (loadStatus!=null) {
//			try {
//				System.out.println("loadStatus"+loadStatus.get());
//				return loadStatus.get();
//			} catch (Exception e) {
//				e.printStackTrace();
//				System.out.println("en el catch");
//				return progress;
//				
//			}
//		}else{
//			System.out.println("loadStatus es nulo");
//			return progress;
//		}
		
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Integer getSalesFileCount() {
		return salesFileCount;
	}

	public void setSalesFileCount(Integer salesFileCount) {
		this.salesFileCount = salesFileCount;
	}

}
