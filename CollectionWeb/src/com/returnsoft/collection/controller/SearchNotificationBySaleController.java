package com.returnsoft.collection.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.CommerceCodeException;
import com.returnsoft.collection.exception.NotificationAddressNullException;
import com.returnsoft.collection.exception.NotificationDepartmentNullException;
import com.returnsoft.collection.exception.NotificationDistrictNullException;
import com.returnsoft.collection.exception.NotificationFirstnameNullException;
import com.returnsoft.collection.exception.NotificationLastnameMaternalNullException;
import com.returnsoft.collection.exception.NotificationLastnamePaternalNullException;
import com.returnsoft.collection.exception.NotificationLimit1Exception;
import com.returnsoft.collection.exception.NotificationLimit2Exception;
import com.returnsoft.collection.exception.NotificationPayerNullException;
import com.returnsoft.collection.exception.NotificationProvinceNullException;
import com.returnsoft.collection.exception.PayerDataNullException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean
@ViewScoped
public class SearchNotificationBySaleController implements Serializable {

	/**
	 * Esta clase sirve para buscar ventas para gestionar notificaciones.
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private SaleService saleService;
	
	@EJB
	private UserService userService;
	
	@EJB
	private NotificationService notificationService;

	private String searchTypeSelected;

	private List<Sale> sales;
	private Sale saleSelected;

	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;

	private Date affiliationDate;

	private String nuicResponsible;
	
	private List<SelectItem> notificationStates;
	private List<String> notificationStatesSelected;

	private Boolean searchByDocumentNumberResponsibleRendered;
	private Boolean searchByDateSaleRendered;

	private List<Notification> notifications;
	private List<Payer> payers;
	
	private List<SelectItem> banks;
	private String bankSelected;
	
	private List<SelectItem> saleStates;
	private String saleStateSelected;
	

	private List<Commerce> commerces;
	
	private FacesUtil facesUtil;
	
	private String passwordSupervisor;
	
	private Boolean supervisorAccess;
	
	private Integer salesCount;
	
	///
	private Date sendingAt;

	public SearchNotificationBySaleController() {
		System.out.println("Se construye SearchSaleController");
		facesUtil = new FacesUtil();
	}

	public String initialize() {
		
		System.out.println("inicializando SearchSaleController");

		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}
					
				/*Short bankId = (Short) FacesContext.getCurrentInstance()
						.getExternalContext().getSessionMap().get("bankId");*/
				
				if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {
					Short bankId = sessionBean.getBank().getId();
					System.out.println("id de banco"+bankId);
					commerces = saleService.findCommercesByBankId(bankId);
					//System.out.println("cantidad de commerce encontrados:"+commerces.size());
				}
				
				List<Bank> banksEntity = saleService.getBanks();
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setValue(bank.getId().toString());
					item.setLabel(bank.getName());
					banks.add(item);
				}

				/*List<Product> productsEntity = saleService.getProducts();
				products = new ArrayList<SelectItem>();
				for (Product product : productsEntity) {
					SelectItem item = new SelectItem();
					item.setValue(product.getId().toString());
					item.setLabel(product.getName());
					products.add(item);
				}*/
				
				saleStates = new ArrayList<SelectItem>();
				for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
					SelectItem item = new SelectItem();
					item.setValue(saleStateEnum.getId());
					item.setLabel(saleStateEnum.getName());
					saleStates.add(item);
				}
				
				notificationStates = new ArrayList<SelectItem>();
				for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
					SelectItem item = new SelectItem();
					item.setValue(notificationStateEnum.getId());
					item.setLabel(notificationStateEnum.getName());
					notificationStates.add(item);
				}
				
				searchTypeSelected="";
				
				System.out.println("searchTypeSelected:"+searchTypeSelected);
				
				return null;
				
			} else{
				throw new UserLoggedNotFoundException();
			}

			

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return null;
		}

	}

	public void onChangeSearchType() {
		try {
			// if (fromRequest) {
			searchTypeSelected = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("form:searchType_input");
			// }

			System.out.println("onChangeSearchBy" + searchTypeSelected);

			if (searchTypeSelected.equals("dni")) {
				searchByDocumentNumberResponsibleRendered = true;
				searchByDateSaleRendered = false;
			} else if (searchTypeSelected.equals("saleData")) {
				searchByDocumentNumberResponsibleRendered = false;
				searchByDateSaleRendered = true;
			} else {
				searchByDocumentNumberResponsibleRendered = false;
				searchByDateSaleRendered = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	
	public void search() {

		try {
			
			saleSelected=null;

			if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				sales = saleService
						.findSalesByNuicResponsible(nuicResponsibleLong);
			} else if (searchTypeSelected.equals("saleData")) {
				
				List<NotificationStateEnum> notificationStatesEnum = new ArrayList<NotificationStateEnum>();
				if (notificationStatesSelected != null && notificationStatesSelected.size() > 0) {
					for (String notificationStateSelected : notificationStatesSelected) {
						NotificationStateEnum notificationStateEnum = NotificationStateEnum.findById(Short.parseShort(notificationStateSelected));
						notificationStatesEnum.add(notificationStateEnum);
					}
				}
				
				Short bankId = null;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				}
				SaleStateEnum saleState = null;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				}
				
				sales = saleService.findSalesBySaleData2(dateOfSaleStarted,
						dateOfSaleEnded, affiliationDate,notificationStatesEnum,bankId,saleState);
			} 
			
			

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
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
			row.createCell(38).setCellValue("MEDIO DE PAGO");
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

			row.createCell(49).setCellValue("BANCO");
			row.createCell(50).setCellValue("FECHA CREACION");
			row.createCell(51).setCellValue("USUARIO CREACION");

			for (int i = 0; i < sales.size(); i++) {
				Sale sale = sales.get(i);
				XSSFRow rowBody = sheet.createRow(i + 1);

				rowBody.createCell(0).setCellValue(sale.getCode());
				rowBody.createCell(1).setCellValue(sale.getDocumentType());
				rowBody.createCell(2).setCellValue(sale.getPayer().getNuicResponsible());
				rowBody.createCell(3).setCellValue(
						sale.getPayer().getLastnamePaternalResponsible());
				rowBody.createCell(4).setCellValue(
						sale.getPayer().getLastnameMaternalResponsible());
				rowBody.createCell(5).setCellValue(
						sale.getPayer().getFirstnameResponsible());
				// System.out.println("credi card number: "+sale.getCreditCardNumber());
				rowBody.createCell(6).setCellValue(
						sale.getCreditCard().getNumber() + "");
				rowBody.createCell(7)
						.setCellValue(sale.getAccountNumber() + "");
				rowBody.createCell(8).setCellValue(
						sale.getCreditCard().getExpirationDate() != null ? sdf1
								.format(sale.getCreditCard().getExpirationDate())
								: null);
				rowBody.createCell(9).setCellValue(""/*
						sale.getCreditCard().getDaysOfDefault() != null
						?sale.getCreditCard().getDaysOfDefault():null*/);
				
				rowBody.createCell(10).setCellValue(""/*sale.getNuicContractor()*/);
				rowBody.createCell(11).setCellValue(
						sale.getLastnamePaternalContractor());
				rowBody.createCell(12).setCellValue(
						sale.getLastnameMaternalContractor());
				rowBody.createCell(13).setCellValue(
						sale.getFirstnameContractor());
				rowBody.createCell(14).setCellValue(sale.getNuicInsured());
				rowBody.createCell(15).setCellValue(
						sale.getLastnamePaternalInsured());
				rowBody.createCell(16).setCellValue(
						sale.getLastnameMaternalInsured());
				rowBody.createCell(17).setCellValue(sale.getFirstnameInsured());
				rowBody.createCell(18).setCellValue(sale.getPhone1());
				rowBody.createCell(19).setCellValue(sale.getPhone2());
				rowBody.createCell(20).setCellValue(sale.getPayer().getMail());
				rowBody.createCell(21).setCellValue(sale.getPayer().getDepartment());
				rowBody.createCell(22).setCellValue(sale.getPayer().getProvince());
				rowBody.createCell(23).setCellValue(sale.getPayer().getDistrict());
				rowBody.createCell(24).setCellValue(sale.getPayer().getAddress());
				rowBody.createCell(25).setCellValue(
						sdf2.format(sale.getDateOfSale()));
				rowBody.createCell(26).setCellValue(sale.getChannelOfSale());
				rowBody.createCell(27).setCellValue(sale.getPlaceOfSale());
				rowBody.createCell(28).setCellValue(sale.getVendorCode());
				rowBody.createCell(29).setCellValue(sale.getVendorName());
				rowBody.createCell(30).setCellValue(sale.getPolicyNumber());
				rowBody.createCell(31)
						.setCellValue(sale.getCertificateNumber());
				rowBody.createCell(32).setCellValue(sale.getProposalNumber());
				rowBody.createCell(33).setCellValue(sale.getCommerce().getCode());
				rowBody.createCell(34)
						.setCellValue(sale.getCommerce().getProduct().getName());
				rowBody.createCell(35).setCellValue(
						sale.getProductDescription());
				rowBody.createCell(36).setCellValue(sale.getCollectionPeriod());
				rowBody.createCell(37).setCellValue(sale.getCollectionType());
				rowBody.createCell(38).setCellValue(
						sale.getCommerce().getPaymentMethod().getName());
				rowBody.createCell(39).setCellValue(
						sale.getInsurancePremium().doubleValue());
				rowBody.createCell(40).setCellValue(
						sdf2.format(sale.getAuditDate()));
				rowBody.createCell(41).setCellValue(sale.getAuditUser());
				rowBody.createCell(42).setCellValue(sale.getSaleState().getState().getName());
				rowBody.createCell(43).setCellValue(
						sale.getSaleState().getDate() != null ? sdf2.format(sale
								.getSaleState().getDate()) : "");
				rowBody.createCell(44).setCellValue(sale.getSaleState().getDownUser());
				rowBody.createCell(45).setCellValue(sale.getSaleState().getDownChannel());
				rowBody.createCell(46).setCellValue(sale.getSaleState().getDownReason());
				rowBody.createCell(47).setCellValue(sale.getSaleState().getDownObservation());
				rowBody.createCell(48).setCellValue(
						sale.getCreditCard().getUpdateDate() != null ? sdf2
								.format(sale.getCreditCard().getUpdateDate()) : "");

				rowBody.createCell(49).setCellValue(sale.getCommerce().getBank().getName());
				rowBody.createCell(50).setCellValue(
						sdf3.format(sale.getCreatedAt()));
				rowBody.createCell(51).setCellValue(
						sale.getCreatedBy().getUsername());

			}

			/*
			 * HSSFWorkbook workbook = new HSSFWorkbook(); HSSFSheet sheet =
			 * workbook.createSheet(); HSSFRow row = sheet.createRow(0);
			 * HSSFCell cell = row.createCell(0); cell.setCellValue(0.0);
			 */

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			// externalContext.setResponseContentType("application/vnd.ms-excel");
			externalContext
					.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition",
					"attachment; filename=\"ventas.xlsx\"");

			wb.write(externalContext.getResponseOutputStream());
			facesContext.responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}

	

	
	
	
	
	
	
	
	public void showNotifications() {

		try {
			
			Map<String, Object> options = new HashMap<String, Object>();
		   options.put("modal", true);
		   options.put("draggable", true);
		   options.put("resizable", true);
		   options.put("contentHeight", "'100%'");
		   options.put("contentWidth", "'100%'");
		   options.put("height", "400");
		   options.put("width", "1000");
			   
			
			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(String.valueOf(saleSelected.getId()));
			paramMap.put("saleId", paramList);
			RequestContext.getCurrentInstance().openDialog("edit_notification_by_sale", options,paramMap);
			

			/*System.out.println("Ingreso a showNotifications " + saleSelected);
			notifications = saleService.findNotifications(saleSelected.getId());
			System.out.println("notifications: " + notifications.size());*/

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}
	
	public void showPayers() {

		try {

			System.out.println("Ingreso a showDatas " + saleSelected);
			payers = saleService.findPayers(saleSelected.getId());
			System.out.println("payers: " + payers.size());
			// RequestContext.getCurrentInstance().openDialog("show_credit_card_update");

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}
	
	
	

	public void addMaintenance() {
		try {
			System.out.println("Ingreso a addMaintenance");
			
			//VALIDA BANK
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			
			
			if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {

				// VALIDA COMMERCIAL CODE
				Commerce commercialCodeObject = null;
				for (Commerce commerce : commerces) {
					if (saleSelected.getCommerce().getCode().equals(
							commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				if (commercialCodeObject == null) {
					String message = "Error " + saleSelected.getCommerce().getCode()
							+ " codigo de comercio inexistente.";
					FacesMessage msg = new FacesMessage(message);
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{
					// VALIDATE PASSWORD SUPERVISOR
					if (saleSelected.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
						// CHECK PASSWORD SUPERVISOR
						RequestContext context = RequestContext.getCurrentInstance();
						context.execute("PF('passSuperDialog').show();");
					} else {
						Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
						ArrayList<String> paramList = new ArrayList<>();
						paramList.add(String.valueOf(saleSelected.getId()));
						paramMap.put("saleId", paramList);
						RequestContext.getCurrentInstance().openDialog("add_maintenance", null,paramMap);
					}
				}
				
			}else{
				FacesMessage msg = new FacesMessage("Debe seleccionar banco");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

				

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}
	
	
	public void addPayer() {
		try {
			System.out.println("Ingreso a addPayer");
			
			//VALIDA BANK
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {

				// VALIDA COMMERCIAL CODE
				Commerce commercialCodeObject = null;
				for (Commerce commerce : commerces) {
					if (saleSelected.getCommerce().getCode().equals(
							commerce.getCode())) {
						commercialCodeObject = commerce;
					}
				}
				if (commercialCodeObject == null) {
					String message = "Error " + saleSelected.getCommerce().getCode()
							+ " codigo de comercio inexistente.";
					FacesMessage msg = new FacesMessage(message);
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
				}else{

					// VALIDATE PASSWORD SUPERVISOR
					//if (saleSelected.getSaleState().getState().equals(SaleStateEnum.DOWN)) {

						// CHECK PASSWORD SUPERVISOR
						//RequestContext context = RequestContext.getCurrentInstance();
						//context.execute("PF('passSuperDialog').show();");
						
					//} else {
						
						Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
						ArrayList<String> paramList = new ArrayList<>();
						paramList.add(String.valueOf(saleSelected.getId()));
						paramMap.put("saleId", paramList);
						RequestContext.getCurrentInstance().openDialog("add_payer", null,paramMap);

					//}
				}
				
			}else{
				FacesMessage msg = new FacesMessage("Debe seleccionar banco");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

				

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}
	
	
	public void afterAddMaintenance(SelectEvent event) {

		/*Object response = event.getObject();

		System.out.println("response:" + response + ":");*/
		
		Sale saleReturn = (Sale) event.getObject();
		
		for (int i = 0; i < sales.size(); i++) {
			Sale sale = sales.get(i);
			System.out.println("1"+sale.getId());
			System.out.println("1"+sale.getCode());
			System.out.println("2"+saleReturn.getId());
			System.out.println("2"+saleReturn.getCode());
			if (sale.getId().equals(saleReturn.getId())) {
				System.out.println("INGRESOOOOOOOO");
				/*System.out.println(saleReturn.getDownUser());
				System.out.println(saleReturn.getDownObservation());*/
				sales.set(i, saleReturn);
				saleSelected=saleReturn;
				break;
			}
		}

	}
	
	public void afterAddPayer(SelectEvent event) {
		
		Sale saleReturn = (Sale) event.getObject();
		
		for (int i = 0; i < sales.size(); i++) {
			Sale sale = sales.get(i);
			if (sale.getId().equals(saleReturn.getId())) {
				sales.set(i, saleReturn);
				saleSelected=saleReturn;
				break;
			}
		}

	}
	
	public void addNotification() {
		try {
			System.out.println("Ingreso a addNotification");
			System.out.println("1");

			// VALIDA SI LA VENTA NO ESTA ACTIVA
			if (!saleSelected.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
				Exception e = new SaleStateNoActiveException(saleSelected.getCode());
				facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
						e.getMessage());
			}else{
				if (saleSelected.getPayer()!=null) {
					System.out.println("2");
					if (saleSelected.getPayer().getFirstnameResponsible().trim().equals("")) {
						System.out.println("3"+saleSelected.getPayer().getFirstnameResponsible());
						Exception e = new NotificationFirstnameNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
						
					}else if (saleSelected.getPayer().getLastnamePaternalResponsible().trim().equals("")) {
						System.out.println("3");
						Exception e = new NotificationLastnamePaternalNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getPayer().getLastnameMaternalResponsible().trim().equals("")) {
						System.out.println("4");
						Exception e = new NotificationLastnameMaternalNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getPayer().getAddress().trim().equals("")) {
						System.out.println("5");
						Exception e = new NotificationAddressNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getPayer().getDepartment().trim().equals("")) {
						System.out.println("6");
						Exception e = new NotificationDepartmentNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getPayer().getProvince().trim().equals("")) {
						System.out.println("7");
						Exception e = new NotificationProvinceNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getPayer().getDistrict().trim().equals("")) {
						System.out.println("8");
						Exception e = new NotificationDistrictNullException();
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
					}else if (saleSelected.getVirtualNotifications()<3) {
						System.out.println("9");
						//Si tiene menos de 3 envios virtuales
						if (saleSelected.getPhysicalNotifications()>1) {
							System.out.println("10");
							//no se agrega porque tiene 2 envíos físicos y menos de 3 virtuales.
							Exception e = new NotificationLimit2Exception(saleSelected.getCode());
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
									e.getMessage());
						}else{
							System.out.println("antes de dialogo");
							
							Map<String, Object> options = new HashMap<String, Object>();
							options.put("modal", true);
							options.put("draggable", false);
							options.put("resizable", false);
							options.put("contentHeight", 420);
							options.put("contentWidth", 320);
	
							Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
							ArrayList<String> paramList = new ArrayList<>();
							paramList.add(String.valueOf(saleSelected.getId()));
							paramMap.put("saleId", paramList);
							RequestContext.getCurrentInstance().openDialog("add_notification", options,paramMap);
						}
					}else {
						System.out.println("1");
						if (saleSelected.getPhysicalNotifications()>0) {
							System.out.println("1");
							//No se agrega porque ya tiene 1 envío físico y 3 envíos virtuales.
							Exception e = new NotificationLimit1Exception(saleSelected.getCode());
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
									e.getMessage());
						}else{
							
							System.out.println("antes de dialogo");
						
							Map<String, Object> options = new HashMap<String, Object>();
							options.put("modal", true);
							options.put("draggable", false);
							options.put("resizable", false);
							options.put("contentHeight", 420);
							options.put("contentWidth", 320);
	
							Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
							ArrayList<String> paramList = new ArrayList<>();
							paramList.add(String.valueOf(saleSelected.getId()));
							paramMap.put("saleId", paramList);
							RequestContext.getCurrentInstance().openDialog("add_notification", options,paramMap);
						}
					
					}
				}else{
					Exception e = new NotificationPayerNullException();
					facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
							e.getMessage());
				}
				
				
			}
			
			
			
				
			/*}else{
				
				if (physical==0) {
					//SE ENVIA
				}else{
					//se han enviado 3 notificaciones por medio electrónico y una por medio físico.
				}
				FacesMessage msg = new FacesMessage("se han enviado 3 notificaciones por medio electrónico y una por medio físico.");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}*/
			
			

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}
	
	public void afterAddNotification(SelectEvent event) {
		
		Sale saleReturn = (Sale) event.getObject();
		
		for (int i = 0; i < sales.size(); i++) {
			Sale sale = sales.get(i);
			//System.out.println("1"+sale.getId());
			//System.out.println("2"+saleReturn.getId());
			if (sale.getId().equals(saleReturn.getId())) {
				//System.out.println("INGRESOOOOOOOO");
				/*System.out.println(saleReturn.getDownUser());
				System.out.println(saleReturn.getDownObservation());*/
				sales.set(i, saleReturn);
				saleSelected=saleReturn;
				break;
			}
		}

	}
	
	public void createNotifications(){
		try {
			
			System.out.println("Ingreso a printNotification");
			
			List<Exception> errors = new ArrayList<Exception>();
			
			if (sales!=null && sales.size()>0) {
				
				for (Sale sale : sales) {
					if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}
					if (sale.getPayer().getFirstnameResponsible()==null || sale.getPayer().getFirstnameResponsible().trim().length()==0) {
						errors.add(new PayerDataNullException("El nombre", sale.getCode()));
					}
					if (sale.getPayer().getLastnamePaternalResponsible()==null || sale.getPayer().getLastnamePaternalResponsible().trim().length()==0) {
						errors.add(new PayerDataNullException("El apellido paterno", sale.getCode()));
					}
					if (sale.getPayer().getLastnameMaternalResponsible()==null || sale.getPayer().getLastnameMaternalResponsible().trim().length()==0) {
						errors.add(new PayerDataNullException("El apellido materno", sale.getCode()));
					}
					if (sale.getPayer().getAddress()==null || sale.getPayer().getAddress().trim().length()==0) {
						errors.add(new PayerDataNullException("La dirección", sale.getCode()));
					}
					if (sale.getPayer().getProvince()==null || sale.getPayer().getProvince().trim().length()==0) {
						errors.add(new PayerDataNullException("La provincia", sale.getCode()));//
					}
					if (sale.getPayer().getDepartment()==null || sale.getPayer().getDepartment().trim().length()==0) {
						errors.add(new PayerDataNullException("El departamento", sale.getCode()));//
					}
					if (sale.getPayer().getDistrict()==null || sale.getPayer().getDistrict().trim().length()==0) {
						errors.add(new PayerDataNullException("El distrito", sale.getCode()));//
					}
					
					//Si tiene menos de 3 envios virtuales
					if (sale.getVirtualNotifications()<3) {
						if (sale.getPhysicalNotifications()>1) {
							//no se agrega porque tiene 2 envíos físicos y menos de 3 virtuales.
							errors.add(new NotificationLimit2Exception(sale.getCode()));
						}
					}else {
						if (sale.getPhysicalNotifications()>0) {
							//No se agrega porque ya tiene 1 envío físico y 3 envíos virtuales.
							errors.add(new NotificationLimit1Exception(sale.getCode()));
						}
					}
					
					// VALIDATE COMMERCIAL CODE
					Commerce commercialCodeObject = null;
					for (Commerce commerce : commerces) {
						if (sale.getCommerce().getCode().equals(commerce.getCode())) {
							commercialCodeObject = commerce;
							break;
						}
					}
					if (commercialCodeObject == null) {
						errors.add(new CommerceCodeException(sale.getCode(),sale.getCommerce().getCode()));
					}
					
					//validacion de confirmacion de imprimir.
					//preguntar si desea agregar notificación a las ventas.
					
					
				}
				
				if (errors.size()>0) {
					for (Exception e : errors) {
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
					}
				} else {
					
					SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance()
							.getExternalContext().getSessionMap().get("sessionBean");
					
					User user= sessionBean.getUser();
					List<Exception> errors2 = new ArrayList<Exception>();
					for (Sale sale : sales) {
						Notification notification = new Notification();
						notification.setSendingAt(sendingAt);
						notification.setSale(sale);
						notification.setType(NotificationTypeEnum.PHYSICAL);
						notification.setState(NotificationStateEnum.SENDING);
						notification.setCreatedBy(user);
						notification.setCreatedAt(new Date());
						try {
							notificationService.add(notification);	
						} catch (Exception e) {
							e.printStackTrace();
							errors2.add(e);
						}
					}
					RequestContext context = RequestContext.getCurrentInstance();
					context.execute("PF('notificationsDialog').hide()");
					context.update("form:messages");
					
					if (errors2.size()>0) {
						for (Exception e : errors) {
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
									e.getMessage());
						}
					}else{
						facesUtil.sendConfirmMessage("Se crearon las notificaciones correctamente", "");
					}
					
				}
				
			}else{
				facesUtil.sendErrorMessage("No existen ventas para notificar...","");
			}
			
			
			
			////////////////
		
		
		
		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
		
	}
	
	
	public void printNotifications(){
		
		try {
			
			System.out.println("Ingreso a printNotification");
			
			//VALIDA BANK
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {
				List<Exception> errors = new ArrayList<Exception>();
				
				if (sales!=null && sales.size()>0) {
					
					for (Sale sale : sales) {
						if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
							errors.add(new SaleStateNoActiveException(sale.getCode()));
						}
						if (sale.getPayer().getFirstnameResponsible()==null || sale.getPayer().getFirstnameResponsible().trim().length()==0) {
							errors.add(new PayerDataNullException("El nombre", sale.getCode()));
						}
						if (sale.getPayer().getLastnamePaternalResponsible()==null || sale.getPayer().getLastnamePaternalResponsible().trim().length()==0) {
							errors.add(new PayerDataNullException("El apellido paterno", sale.getCode()));
						}
						if (sale.getPayer().getLastnameMaternalResponsible()==null || sale.getPayer().getLastnameMaternalResponsible().trim().length()==0) {
							errors.add(new PayerDataNullException("El apellido materno", sale.getCode()));
						}
						if (sale.getPayer().getAddress()==null || sale.getPayer().getAddress().trim().length()==0) {
							errors.add(new PayerDataNullException("La dirección", sale.getCode()));
						}
						if (sale.getPayer().getProvince()==null || sale.getPayer().getProvince().trim().length()==0) {
							errors.add(new PayerDataNullException("La provincia", sale.getCode()));//
						}
						if (sale.getPayer().getDepartment()==null || sale.getPayer().getDepartment().trim().length()==0) {
							errors.add(new PayerDataNullException("El departamento", sale.getCode()));//
						}
						if (sale.getPayer().getDistrict()==null || sale.getPayer().getDistrict().trim().length()==0) {
							errors.add(new PayerDataNullException("El distrito", sale.getCode()));//
						}
						
						//Si tiene menos de 3 envios virtuales
						if (sale.getVirtualNotifications()<3) {
							if (sale.getPhysicalNotifications()>1) {
								//no se agrega porque tiene 2 envíos físicos y menos de 3 virtuales.
								errors.add(new NotificationLimit2Exception(sale.getCode()));
							}
						}else {
							if (sale.getPhysicalNotifications()>0) {
								//No se agrega porque ya tiene 1 envío físico y 3 envíos virtuales.
								errors.add(new NotificationLimit1Exception(sale.getCode()));
							}
						}
						
						// VALIDATE COMMERCIAL CODE
						Commerce commercialCodeObject = null;
						for (Commerce commerce : commerces) {
							if (sale.getCommerce().getCode().equals(commerce.getCode())) {
								commercialCodeObject = commerce;
								break;
							}
						}
						if (commercialCodeObject == null) {
							errors.add(new CommerceCodeException(sale.getCode(),sale.getCommerce().getCode()));
						}
						
						//validacion de confirmacion de imprimir.
						//preguntar si desea agregar notificación a las ventas.
						
						
					}
					
					if (errors.size()>0) {
						for (Exception e : errors) {
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
						}
					} else {
						
						/*SessionBean sessionBean = (SessionBean) FacesContext
								.getCurrentInstance().getExternalContext()
								.getSessionMap().get("sessionBean");*/
						
						Short bankId = sessionBean.getBank().getId();
						
						BankLetterEnum bankLetterEnum = BankLetterEnum.findById(bankId);
						
						Map<String, Object> parameters = new HashMap<String, Object>();
						
					    ServletContext servletContext=(ServletContext) FacesContext.getCurrentInstance ().getExternalContext().getContext();
		
						String separator=System.getProperty("file.separator");
						String rootPath= servletContext.getRealPath(separator);
						String fileName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getTemplate();
						String signatureName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getSignature();
						
						parameters.put("signature", signatureName);
						parameters.put("sales", sales);
						
						JasperReport report = JasperCompileManager.compileReport(fileName);
					    JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
					    
					    FacesContext facesContext = FacesContext.getCurrentInstance();
						ExternalContext externalContext = facesContext.getExternalContext();
						externalContext.setResponseContentType("application/pdf");
						externalContext.setResponseHeader("Content-Disposition",
								"attachment; filename=\"carta.pdf\"");
						
						JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
						
						facesContext.responseComplete();
						
					}
					
				}else{
					System.out.println("No existen ventas para imprimir...");
					facesUtil.sendErrorMessage("No existen ventas para imprimir...","");
					
				}
			}else{
				FacesMessage msg = new FacesMessage("Debe seleccionar banco");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}
			
			
			  
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
		}
		
	}
	
	/*public void printNotification(){
		
		try {
			
			System.out.println("Ingreso a printNotification");
			
			if (saleSelected!=null && saleSelected.getId()!=null) {
				
				// VALIDA SI LA VENTA NO ESTA ACTIVA
				if (!saleSelected.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
					Exception e = new SaleStateNoActiveException(saleSelected.getCode());
					facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
							e.getMessage());
				}else{
					System.out.println("bankId:"+saleSelected.getCommerce().getBank().getId());
					
					BankLetterEnum bankLetterEnum = BankLetterEnum.findById(saleSelected.getCommerce().getBank().getId());
					
					if (bankLetterEnum!=null) {
						
						Map<String, Object> parameters = new HashMap<String, Object>();
						
					    ServletContext servletContext=(ServletContext) FacesContext.getCurrentInstance ().getExternalContext().getContext();
		
						String separator=System.getProperty("file.separator");
						  
						String rootPath= servletContext.getRealPath(separator);
						  
						String fileName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getTemplate();
						
						String signatureName = rootPath+"resources"+separator+"templates"+separator+bankLetterEnum.getSignature();
						
						
						System.out.println("signatureName:"+signatureName);
						
						parameters.put("signature", signatureName);
						
						System.out.println("cantidad de sales:"+sales.size());
						
						parameters.put("sales", sales);
						
						
						JasperReport report = JasperCompileManager.compileReport(fileName);
					    JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
					    
					    FacesContext facesContext = FacesContext.getCurrentInstance();
						ExternalContext externalContext = facesContext.getExternalContext();
						
						externalContext
								.setResponseContentType("application/pdf");
						externalContext.setResponseHeader("Content-Disposition",
								"attachment; filename=\"carta.pdf\"");
						
						
						JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
						
						facesContext.responseComplete();
				    
					}else{
						Exception e = new BankLetterNotFoundException(saleSelected.getCode(),saleSelected.getCommerce().getBank().getName());
						facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
								e.getMessage());
						
					}
				}
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
		  
	}*/
	
	public void validate() {

		try {
			
			System.out.println("ingreso a validate");
			
			System.out.println("passwordSupervisor"+passwordSupervisor);
			
			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("sessionBean");
			
			/*Integer userId = (Integer) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userId");*/
			Integer userId = sessionBean.getUser().getId();
			
			User user = userService.findById(userId);

			if (user.getPasswordSupervisor().equals(passwordSupervisor)) {
				// CORRECT
				//RequestContext.getCurrentInstance().closeDialog("ValidatePasswordCorrect");
				RequestContext context = RequestContext.getCurrentInstance();
                // execute javascript and show dialog
                context.execute("PF('passSuperDialog').hide();");
				//System.out.println("isCorrecto");
				/////////
				Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
				ArrayList<String> paramList = new ArrayList<>();
				paramList.add(String.valueOf(saleSelected.getId()));
				paramMap.put("saleId", paramList);
				RequestContext.getCurrentInstance().openDialog("add_maintenance", null,paramMap);

				 				
			} else {
				FacesMessage msg = new FacesMessage(
						"Password supervisor incorrecto");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
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

	/*public List<SelectItem> getBanks() {
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
	}*/

	public String getSearchTypeSelected() {
		return searchTypeSelected;
	}

	public void setSearchTypeSelected(String searchTypeSelected) {
		this.searchTypeSelected = searchTypeSelected;
	}

	

	public Boolean getSearchByDocumentNumberResponsibleRendered() {
		return searchByDocumentNumberResponsibleRendered;
	}

	public void setSearchByDocumentNumberResponsibleRendered(
			Boolean searchByDocumentNumberResponsibleRendered) {
		this.searchByDocumentNumberResponsibleRendered = searchByDocumentNumberResponsibleRendered;
	}

	

	public Boolean getSearchByDateSaleRendered() {
		return searchByDateSaleRendered;
	}

	public void setSearchByDateSaleRendered(Boolean searchByDateSaleRendered) {
		this.searchByDateSaleRendered = searchByDateSaleRendered;
	}




	public Sale getSaleSelected() {
		return saleSelected;
	}

	public void setSaleSelected(Sale saleSelected) {
		this.saleSelected = saleSelected;
	}

	/*public String getSaleStateSelected() {
		return saleStateSelected;
	}

	public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}*/

	

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public List<SelectItem> getNotificationStates() {
		return notificationStates;
	}

	public void setNotificationStates(List<SelectItem> notificationStates) {
		this.notificationStates = notificationStates;
	}

	

	public List<String> getNotificationStatesSelected() {
		return notificationStatesSelected;
	}

	public void setNotificationStatesSelected(List<String> notificationStatesSelected) {
		this.notificationStatesSelected = notificationStatesSelected;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
	}


	/*public List<SelectItem> getSaleStates() {
		return saleStates;
	}

	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}*/

	public String getPasswordSupervisor() {
		return passwordSupervisor;
	}

	public void setPasswordSupervisor(String passwordSupervisor) {
		this.passwordSupervisor = passwordSupervisor;
	}

	public Boolean getSupervisorAccess() {
		return supervisorAccess;
	}

	public void setSupervisorAccess(Boolean supervisorAccess) {
		this.supervisorAccess = supervisorAccess;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public String getNuicResponsible() {
		return nuicResponsible;
	}

	public void setNuicResponsible(String nuicResponsible) {
		this.nuicResponsible = nuicResponsible;
	}

	public List<Payer> getPayers() {
		return payers;
	}

	public void setPayers(List<Payer> payers) {
		this.payers = payers;
	}

	public Date getSendingAt() {
		return sendingAt;
	}

	public void setSendingAt(Date sendingAt) {
		this.sendingAt = sendingAt;
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

	public List<SelectItem> getSaleStates() {
		return saleStates;
	}

	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}

	public String getSaleStateSelected() {
		return saleStateSelected;
	}

	public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}
	

}
