package com.returnsoft.collection.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.PayerHistory;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankInvalidException;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.BankNotSelectedException;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.NotificationAlreadyExistException;
import com.returnsoft.collection.exception.NotificationClosedException;
import com.returnsoft.collection.exception.NotificationLimit1Exception;
import com.returnsoft.collection.exception.NotificationLimit2Exception;
import com.returnsoft.collection.exception.NotificationLimit3Exception;
import com.returnsoft.collection.exception.NotificationNotFoundException;
import com.returnsoft.collection.exception.NotificationPendingException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.PayerService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SaleLazyModel;
import com.returnsoft.collection.util.SessionBean;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@ManagedBean
@ViewScoped
public class SearchSalesForNotificationsController implements Serializable {

	/**
	 * Esta clase busca ventas para gestionar notificaciones.
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private BankService bankService;

	// @EJB
	// private CommerceService commerceService;

	@EJB
	private SaleService saleService;

	@EJB
	private PayerService payerService;

	//////

	// @EJB
	// private UserService userService;

	@EJB
	private NotificationService notificationService;

	/*
	 * @EJB private MailingService mailingService;
	 */

	// VENTAS ENCONTRADAS
	// private List<Sale> sales;
	private Sale saleSelected;
	// private Integer salesCount;

	// TIPOS DE BÚSQUEDA
	private String searchTypeSelected;
	// private Boolean searchByDocumentNumberResponsibleRendered;
	// private Boolean searchByDateSaleRendered;

	// BUSQUEDA POR DATOS DE LA VENTA
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;
	private Date sendingDate;
	private List<SelectItem> notificationStates;
	private List<String> notificationStatesSelected;
	private List<SelectItem> banks;
	private String bankSelected;
	private List<SelectItem> saleStates;
	private String saleStateSelected;
	private List<SelectItem> notificationTypes;
	private String notificationTypeSelected;

	private Boolean withoutMail;
	private Boolean withoutAddress;
	private Boolean withoutNotification;
	
	private String departmentSelected;
	private List<SelectItem> departments;
	
	private List<String> provincesSelected;
	private List<SelectItem> provinces;

	// BÚSQUEDA POR NUIC DE RESPONSABLE
	private String nuicResponsible;

	private String orderNumber;

	// INGRESAR PASSWORD SUPERVISOR
	private String passwordSupervisor;
	private Boolean supervisorAccess;

	// ENVÍO DE NOTIFICACIONES FÍSICAS
	private Date sendingAtForPhysicals;
	private String orderNumberForPhysicals;

	// VER NOTIFICACIONES
	private List<Notification> notifications;

	// VER ACTUALIZACIONES DE DATOS
	private List<PayerHistory> payers;

	// UTIL
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;

	// private List<Commerce> commerces;

	private LazyDataModel<Sale> sales;

	public SearchSalesForNotificationsController() {

		System.out.println("Se construye SearchSaleController");
		// facesUtil = new FacesUtil();

	}

	public String initialize() {

		System.out.println("inicializando SearchSaleController");

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			// SE BUSCAN BANCOS
			List<Bank> banksEntity = bankService.getAll();
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setValue(bank.getId().toString());
				item.setLabel(bank.getName());
				banks.add(item);
			}

			// SOLO PUEDEN BUSCARSE VENTAS ACTIVAS
			saleStates = new ArrayList<SelectItem>();
			for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
				SelectItem saleStateItem = new SelectItem();
				saleStateItem.setValue(saleStateEnum.getId());
				saleStateItem.setLabel(saleStateEnum.getName());
				saleStates.add(saleStateItem);
			}

			// SE BUSCAN TIPOS DE NOTIFICACION
			notificationTypes = new ArrayList<SelectItem>();
			for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
				SelectItem item = new SelectItem();
				item.setValue(notificationTypeEnum.getId());
				item.setLabel(notificationTypeEnum.getName());
				notificationTypes.add(item);
			}

			// SE BUSCAN ESTADOS DE NOTIFICACION
			notificationStates = new ArrayList<SelectItem>();
			for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
				SelectItem item = new SelectItem();
				item.setValue(notificationStateEnum.getId());
				item.setLabel(notificationStateEnum.getName());
				notificationStates.add(item);
			}

			// SE RESETEA EL TIPO DE BUSQUEDA
			searchTypeSelected = "notificationData";
			// onChangeSearchType();
			saleStateSelected = SaleStateEnum.ACTIVE.getId().toString();

			return null;

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}

	/*
	 * public void onChangeSearchType() { try {
	 * 
	 * //searchTypeSelected =
	 * FacesContext.getCurrentInstance().getExternalContext().
	 * getRequestParameterMap() // .get("form:searchType_input");
	 * 
	 * if (searchTypeSelected.equals("dni")) {
	 * searchByDocumentNumberResponsibleRendered = true;
	 * searchByDateSaleRendered = false; } else if
	 * (searchTypeSelected.equals("notificationData")) {
	 * searchByDocumentNumberResponsibleRendered = false;
	 * searchByDateSaleRendered = true; } else {
	 * searchByDocumentNumberResponsibleRendered = false;
	 * searchByDateSaleRendered = false; } } catch (Exception e) {
	 * e.printStackTrace();
	 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
	 * } }
	 */

	public void search() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			saleSelected = null;

			if (searchTypeSelected.equals("dni")) {
				// NUIC RESPONSIBLE
				Long nuicResponsibleLong = null;
				if (nuicResponsible != null) {
					nuicResponsibleLong = Long.parseLong(nuicResponsible);
				}
				sales = new SaleLazyModel(saleService, searchTypeSelected, nuicResponsibleLong);
			} else if (searchTypeSelected.equals("notificationData")) {
				// ESTADOS DE NOTIFICACION
				List<NotificationStateEnum> notificationStatesEnum = new ArrayList<NotificationStateEnum>();
				if (notificationStatesSelected != null && notificationStatesSelected.size() > 0) {
					for (String notificationStateSelected : notificationStatesSelected) {
						NotificationStateEnum notificationStateEnum = NotificationStateEnum
								.findById(Short.parseShort(notificationStateSelected));
						notificationStatesEnum.add(notificationStateEnum);
					}
				}
				// BANCO
				Short bankId = null;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				}
				// ESTADO DE VENTA
				SaleStateEnum saleState = null;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				}
				// TIPO DE NOTIFICACIÓN
				NotificationTypeEnum notificationType = null;
				if (notificationTypeSelected != null && notificationTypeSelected.length() > 0) {
					notificationType = NotificationTypeEnum.findById(Short.parseShort(notificationTypeSelected));
				}
				sales = new SaleLazyModel(saleService, dateOfSaleStarted, dateOfSaleEnded, sendingDate,
						notificationStatesEnum, bankId, saleState, notificationType, withoutMail, withoutAddress,
						withoutNotification, orderNumber);
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public List<Sale> searchAll() {

		try {
			List<Sale> sales = new ArrayList<Sale>();

			if (searchTypeSelected.equals("dni")) {
				// NUIC RESPONSIBLE
				Long nuicResponsibleLong = null;
				if (nuicResponsible != null) {
					nuicResponsibleLong = Long.parseLong(nuicResponsible);
				}
				sales = saleService.findSalesByNuicResponsible(nuicResponsibleLong);

			} else if (searchTypeSelected.equals("notificationData")) {
				// ESTADOS DE NOTIFICACION
				List<NotificationStateEnum> notificationStatesEnum = new ArrayList<NotificationStateEnum>();
				if (notificationStatesSelected != null && notificationStatesSelected.size() > 0) {
					for (String notificationStateSelected : notificationStatesSelected) {
						NotificationStateEnum notificationStateEnum = NotificationStateEnum
								.findById(Short.parseShort(notificationStateSelected));
						notificationStatesEnum.add(notificationStateEnum);
					}
				}
				// BANCO
				Short bankId = null;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				}
				// ESTADO DE VENTA
				SaleStateEnum saleState = null;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				}
				// TIPO DE NOTIFICACIÓN
				NotificationTypeEnum notificationType = null;
				if (notificationTypeSelected != null && notificationTypeSelected.length() > 0) {
					notificationType = NotificationTypeEnum.findById(Short.parseShort(notificationTypeSelected));
				}
				sales = saleService.findForNotifications(dateOfSaleStarted, dateOfSaleEnded, sendingDate,
						notificationStatesEnum, bankId, saleState, notificationType, withoutMail, withoutAddress,
						withoutNotification, orderNumber);
			}

			return sales;
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}
	
	
	public void loadNotifications() {
		try {

			System.out.println("loadNotifications");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", true);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 200);
			options.put("contentWidth", 500);

			// Map<String, List<String>> paramMap = new HashMap<String,
			// List<String>>();
			// ArrayList<String> paramList = new ArrayList<>();
			// paramList.add(String.valueOf(saleSelected.getNotification().getId()));
			// paramMap.put("notificationId", paramList);
			RequestContext.getCurrentInstance().openDialog("load_update_notifications", options, null);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void afterLoadNotifications(SelectEvent event) {
		try {

			System.out.println("afterLoadSales");

			Exception exceptionReturn = (Exception) event.getObject();
			if (exceptionReturn != null) {
				if (exceptionReturn instanceof MultipleErrorsException) {
					for (Exception err : ((MultipleErrorsException) exceptionReturn).getErrors()) {
						facesUtil.sendErrorMessage(err.getMessage());
					}
				} else if (exceptionReturn instanceof NullPointerException) {
					facesUtil.sendErrorMessage("Existen valores nulos.");
				} else {
					facesUtil.sendErrorMessage(exceptionReturn.getMessage());
				}
			} else {
				facesUtil.sendConfirmMessage("Se creó el lote satisfactorimente.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void exportTxt() throws IOException {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			StringBuilder cadena = new StringBuilder();
			String separator = "|";
			String header = "";

			header += "Código único" + separator;
			header += "Tipo documento" + separator;
			header += "NUIC responsable" + separator;
			header += "Apellido paterno responsable" + separator;
			header += "Apellido materno responsable" + separator;
			header += "Nombres responsable" + separator;

			header += "Correo" + separator;
			header += "Departamento" + separator;
			header += "Provincia" + separator;
			header += "Distrito" + separator;
			header += "Dirección" + separator;
			header += "Fecha venta" + separator;
			header += "Estado venta" + separator;
			header += "Banco" + separator;
			header += "Virtuales" + separator;
			header += "Físicas" + separator;
			header += "Tipo" + separator;
			header += "Fecha envío" + separator;
			header += "Fecha respuesta" + separator;
			header += "N° orden" + separator;
			header += "N° correlativo" + separator;
			;
			header += "Motivo";

			cadena.append(header);
			cadena.append("\r\n");

			List<Sale> salesFound = searchAll();

			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

			for (Sale sale : salesFound) {

				cadena.append(sale.getCode() + separator);
				cadena.append(sale.getPayer().getDocumentType() + separator);
				cadena.append(sale.getPayer().getNuicResponsible() + separator);
				cadena.append(sale.getPayer().getLastnamePaternalResponsible() + separator);
				cadena.append(sale.getPayer().getLastnameMaternalResponsible() + separator);
				cadena.append(sale.getPayer().getFirstnameResponsible() + separator);

				cadena.append(sale.getPayer().getMail() + separator);
				cadena.append(sale.getPayer().getDepartment() + separator);
				cadena.append(sale.getPayer().getProvince() + separator);
				cadena.append(sale.getPayer().getDistrict() + separator);
				cadena.append(sale.getPayer().getAddress() + separator);
				cadena.append(sdf2.format(sale.getDate()) + separator);
				cadena.append(sale.getSaleState().getState().getName() + separator);
				cadena.append(sale.getBank().getName() + separator);

				cadena.append(sale.getVirtualNotifications() + separator);
				cadena.append(sale.getPhysicalNotifications() + separator);
				cadena.append(sale.getNotification() != null ? sale.getNotification().getType().getName() + separator
						: separator);
				cadena.append(sale.getNotification() != null
						? sdf2.format(sale.getNotification().getSendingAt()) + separator : separator);
				cadena.append(sale.getNotification() != null
						? sale.getNotification().getAnsweringAt() != null
								? sdf2.format(sale.getNotification().getAnsweringAt()) + separator : separator
						: separator);
				cadena.append(sale.getNotification() != null ? sale.getNotification().getOrderNumber() + separator
						: separator);
				cadena.append(sale.getNotification() != null ? sale.getNotification().getCorrelativeNumber() + separator
						: separator);
				cadena.append(sale.getNotification() != null ? sale.getNotification().getReason() : separator);
				cadena.append("\r\n");

			}

			FacesContext fc = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

			response.reset();
			response.setContentType("text/comma-separated-values");
			response.setHeader("Content-Disposition", "attachment; filename=\"notificaciones.txt\"");

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

			// search();

			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet();
			XSSFRow row = sheet.createRow(0);

			row.createCell(0).setCellValue("Código único");
			row.createCell(1).setCellValue("Tipo documento");
			row.createCell(2).setCellValue("NUIC responsable");
			row.createCell(3).setCellValue("Apellido paterno responsable");
			row.createCell(4).setCellValue("Apellido materno responsable");
			row.createCell(5).setCellValue("Nombres responsable");

			row.createCell(6).setCellValue("Correo");
			row.createCell(7).setCellValue("Departamento");
			row.createCell(8).setCellValue("Provincia");
			row.createCell(9).setCellValue("Distrito");
			row.createCell(10).setCellValue("Dirección");
			row.createCell(11).setCellValue("Fecha venta");
			row.createCell(12).setCellValue("Estado venta");
			row.createCell(13).setCellValue("Banco");
			row.createCell(14).setCellValue("Virtuales");
			row.createCell(15).setCellValue("Físicas");
			row.createCell(16).setCellValue("Tipo");
			row.createCell(17).setCellValue("Fecha envío");
			row.createCell(18).setCellValue("Fecha respuesta");
			row.createCell(19).setCellValue("N° orden");
			row.createCell(20).setCellValue("N° correlativo");
			row.createCell(21).setCellValue("Motivo");

			int i = 0;
			// @SuppressWarnings("unchecked")
			// List<Sale> salesWrapped = (List<Sale>) sales.getWrappedData();
			// System.out.println("tam:"+salesWrapped.size());
			if (sales != null && sales.getWrappedData()!=null && ((List<Sale>)sales.getWrappedData()).size() > 1) {

				for (Sale sale : (List<Sale>)sales.getWrappedData()) {

					// Sale sale = sales.get(i);
					XSSFRow rowBody = sheet.createRow(i + 1);

					rowBody.createCell(0).setCellValue(sale.getCode());
					rowBody.createCell(1).setCellValue(sale.getPayer().getDocumentType().getName());
					rowBody.createCell(2).setCellValue(sale.getPayer().getNuicResponsible());
					rowBody.createCell(3).setCellValue(sale.getPayer().getLastnamePaternalResponsible());
					rowBody.createCell(4).setCellValue(sale.getPayer().getLastnameMaternalResponsible());
					rowBody.createCell(5).setCellValue(sale.getPayer().getFirstnameResponsible());

					rowBody.createCell(6).setCellValue(sale.getPayer().getMail());
					rowBody.createCell(7).setCellValue(sale.getPayer().getDepartment());
					rowBody.createCell(8).setCellValue(sale.getPayer().getProvince());
					rowBody.createCell(9).setCellValue(sale.getPayer().getDistrict());
					rowBody.createCell(10).setCellValue(sale.getPayer().getAddress());
					rowBody.createCell(11).setCellValue(sdf2.format(sale.getDate()));
					rowBody.createCell(12).setCellValue(sale.getSaleState().getState().getName());
					rowBody.createCell(13).setCellValue(sale.getBank().getName());

					rowBody.createCell(14).setCellValue(
							sale.getVirtualNotifications() != null ? sale.getVirtualNotifications().toString() : "");
					rowBody.createCell(15).setCellValue(
							sale.getPhysicalNotifications() != null ? sale.getPhysicalNotifications().toString() : "");
					rowBody.createCell(16).setCellValue(
							sale.getNotification() != null ? sale.getNotification().getType().getName() : "");
					rowBody.createCell(17).setCellValue(
							sale.getNotification() != null ? sdf2.format(sale.getNotification().getSendingAt()) : "");
					rowBody.createCell(18).setCellValue(
							sale.getNotification() != null ? sale.getNotification().getAnsweringAt() != null
									? sdf2.format(sale.getNotification().getAnsweringAt()) : "" : "");
					rowBody.createCell(19).setCellValue(
							sale.getNotification() != null ? sale.getNotification().getOrderNumber() : "");
					rowBody.createCell(20).setCellValue(
							sale.getNotification() != null ? sale.getNotification().getCorrelativeNumber() : "");
					rowBody.createCell(21)
							.setCellValue(sale.getNotification() != null ? sale.getNotification().getReason() : "");

					i++;

				}

			}

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
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

	public void showNotifications() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			notifications = notificationService.findBySale(saleSelected.getId());

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void showPayers() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			payers = payerService.findBySale(saleSelected.getId());

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void editNotification() {

		try {


			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}

			if (!saleSelected.getBank().getId().equals(sessionBean.getBank().getId())) {
				throw new BankInvalidException();
			}

			// VALIDA SI EXISTE NOTIFICACION
			// if (validate) {
			if (saleSelected.getNotification() == null) {
				

				throw new NotificationNotFoundException(saleSelected.getCode());
			}
			// }

			// if (validate) {
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", false);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 300);
			options.put("contentWidth", 400);

			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(String.valueOf(saleSelected.getNotification().getId()));
			paramMap.put("notificationId", paramList);
			RequestContext.getCurrentInstance().openDialog("edit_notification", options, paramMap);

			// }

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public void afterEditNotification(SelectEvent event) {

		Sale saleReturn = (Sale) event.getObject();
		for (Sale sale : sales) {
			// Sale sale = sales.get(i);
			if (sale.getId().equals(saleReturn.getId())) {
				// sales.set(i, saleReturn);
				saleSelected = saleReturn;
				break;
			}
		}

	}

	public void editPayer() {
		try {
			// ActionEvent event
			// System.out.println("Ingreso a addPayer");

			/*
			 * if (saleSelected==null) { System.out.println("es nulo"); }else{
			 * System.out.println("no es nulo"+saleSelected.toString()); if
			 * (saleSelected.getBank()==null) { System.out.println(
			 * "banco es nulo"); }else{ System.out.println("banco no es nulo"
			 * +saleSelected.getBank().getId()); } }
			 */

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}

			if (saleSelected.getBank().getId() != sessionBean.getBank().getId()) {
				throw new BankInvalidException();
			}

			// Boolean validate = true;

			// VALIDA SELECCION DE BANCO
			/*
			 * SessionBean sessionBean = (SessionBean)
			 * FacesContext.getCurrentInstance().getExternalContext()
			 * .getSessionMap().get("sessionBean"); if (sessionBean.getBank() ==
			 * null) { Exception e = new BankNotSelectedException();
			 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
			 * e.getMessage()); //validate = false; }
			 */

			// VALIDA COMMERCIAL CODE
			/*
			 * if (validate) { Commerce commercialCodeObject = null; for
			 * (Commerce commerce : commerces) { if
			 * (saleSelected.getCommerceCode().equals(commerce.getCode())) {
			 * commercialCodeObject = commerce; } } if (commercialCodeObject ==
			 * null) { Exception e = new
			 * CommerceCodeException(saleSelected.getCode(),
			 * saleSelected.getCommerceCode());
			 * facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
			 * e.getMessage()); validate = false; } }
			 */

			// if (validate) {

			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(String.valueOf(saleSelected.getId()));
			paramMap.put("saleId", paramList);
			RequestContext.getCurrentInstance().openDialog("edit_payer", null, paramMap);

			// }

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void afterUpdatePayer(SelectEvent event) {

		Sale saleReturn = (Sale) event.getObject();

		for (Sale sale : sales) {
			// Sale sale = sales.get(i);
			if (sale.getId().equals(saleReturn.getId())) {
				// sales.set(i, saleReturn);
				saleSelected = saleReturn;
				break;
			}
		}

	}

	public void addPhysicalNotification() {
		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}

			if (saleSelected.getBank().getId() != sessionBean.getBank().getId()) {
				throw new BankInvalidException();
			}
			
			validatePhysicalNotification(saleSelected,orderNumberForPhysicals);

			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", false);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 420);
			options.put("contentWidth", 320);

			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(String.valueOf(saleSelected.getId()));
			paramMap.put("saleId", paramList);
			RequestContext.getCurrentInstance().openDialog("add_physical_notification", options, paramMap);

		} catch (Exception exceptionReturn) {
			if (exceptionReturn instanceof MultipleErrorsException) {
				for (Exception err : ((MultipleErrorsException) exceptionReturn).getErrors()) {
					facesUtil.sendErrorMessage(err.getMessage());
				}
			} else if (exceptionReturn instanceof NullPointerException) {
				facesUtil.sendErrorMessage("Existen valores nulos.");
			} else {
				facesUtil.sendErrorMessage(exceptionReturn.getMessage());
			}
		}
	}

	public void afterAddPhysicalNotification(SelectEvent event) {

		Sale saleReturn = (Sale) event.getObject();
		for (Sale sale : sales) {
			// Sale sale = sales.get(i);
			if (sale.getId().equals(saleReturn.getId())) {
				// sales.set(i, saleReturn);
				saleSelected = saleReturn;
				break;
			}
		}

	}

	public void createPhysicalNotifications() {

		try {

			System.out.println("createPhysicalNotifications()");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}

			if (sales == null || sales.getRowCount() == 0) {
				throw new Exception("No existen ventas a notificar");
			}

			List<Exception> errors = new ArrayList<Exception>();
			for (Sale sale : sales) {
				try {
					validatePhysicalNotification(sale,orderNumberForPhysicals);	
				} catch (MultipleErrorsException e) {
					for (Exception exception : e.getErrors()) {
						errors.add(exception);
					}
				}	
			}
			if (errors.size()>0) {
				throw new MultipleErrorsException(errors);
			}
			
			

			User user = sessionBean.getUser();
			notificationService.addPhysicalList((List<Sale>) sales.getWrappedData(), sendingAtForPhysicals, orderNumberForPhysicals, user);

			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('createPhysicalsDialog').hide()");
			context.update("form:messages");

			facesUtil.sendConfirmMessage("Se crearon las notificaciones correctamente");

			////////////////

		} catch (Exception exceptionReturn) {

			if (exceptionReturn instanceof MultipleErrorsException) {
				for (Exception err : ((MultipleErrorsException) exceptionReturn).getErrors()) {
					facesUtil.sendErrorMessage(err.getMessage());
				}
			} else if (exceptionReturn instanceof NullPointerException) {
				facesUtil.sendErrorMessage("Existen valores nulos.");
			} else {
				facesUtil.sendErrorMessage(exceptionReturn.getMessage());
			}

		}

	}

	private void validatePhysicalNotification(Sale sale, String orderNumber) throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();

		//for (Sale sale : sales) {

			// VALIDA SI LA NOTIFICACION EXISTE
			Boolean exist = notificationService.verifyIfExist(sale.getPayer().getNuicResponsible(),
					orderNumber);
			if (exist) {
				errors.add(new NotificationAlreadyExistException(sale.getPayer().getNuicResponsible(),orderNumber));
			}

			// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION
			if (sale.getNotification() != null) {
				if (sale.getNotification().getType().equals(NotificationTypeEnum.PHYSICAL)
						&& sale.getNotification().getState().getPending() == true) {
					errors.add(new NotificationPendingException(sale.getCode()));
				}
			}

			// VALIDA SI LA VENTA NO ESTA ACTIVA
			if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
				errors.add(new SaleStateNoActiveException(sale.getCode()));
			}

			// VALIDA DATOS DE RESPONSABLE
			if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
				errors.add(new SaleStateNoActiveException(sale.getCode()));
			}
			if (sale.getPayer().getFirstnameResponsible() == null
					|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
				errors.add(new NullException("NOMBRE", sale.getCode()));
			}
			if (sale.getPayer().getLastnamePaternalResponsible() == null
					|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
				errors.add(new NullException("APELLIDO PATERNO", sale.getCode()));
			}
			if (sale.getPayer().getLastnameMaternalResponsible() == null
					|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
				errors.add(new NullException("APELLIDO MATERNO", sale.getCode()));
			}
			if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
				errors.add(new NullException("DIRECCIÓN", sale.getCode()));
			}
			if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
				errors.add(new NullException("PROVINCIA", sale.getCode()));//
			}
			if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
				errors.add(new NullException("DEPARTAMENTO", sale.getCode()));//
			}
			if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
				errors.add(new NullException("DISTRITO", sale.getCode()));//
			}

			// VALIDA CANTIDAD DE NOTIFICACIONES ENVIADAS

			// Si tiene menos de 3 envios virtuales
			if (sale.getVirtualNotifications() == null || sale.getVirtualNotifications() < 3) {
				if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 1) {
					// no se agrega porque tiene 2 envíos físicos y
					// menos de 3 virtuales.
					errors.add(new NotificationLimit2Exception(sale.getCode()));
				}
			} else {
				if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 0) {
					// No se agrega porque ya tiene 1 envío físico y
					// 3
					// envíos virtuales.
					errors.add(new NotificationLimit1Exception(sale.getCode()));
				}
			}

		//}

		if (errors.size() > 0) {
			throw new MultipleErrorsException(errors);
		}
	}

	public void createVirtualNotifications() {
		try {

			System.out.println("Ingreso a createVirtualNotifications");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}
			
			if (sales == null || sales.getRowCount() == 0) {
				throw new Exception("No existen ventas a notificar");
			}
			
			BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sessionBean.getBank().getId());
			if (bankLetterEnum == null) {
				throw new BankLetterNotFoundException(sessionBean.getBank().getName());
			}

			List<Exception> errors = new ArrayList<Exception>();
			for (Sale sale : sales) {
				try {
					validateVirtualNotification(sale);	
				} catch (MultipleErrorsException e) {
					for (Exception exception : e.getErrors()) {
						errors.add(exception);
					}
				}	
			}
			if (errors.size()>0) {
				throw new MultipleErrorsException(errors);
			}

			User user = sessionBean.getUser();
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String templatePath = rootPath + "resources" + separator + "templates" + separator;
			
			notificationService.addVirtualList((List<Sale>) sales.getWrappedData(), user,templatePath);

			RequestContext context = RequestContext.getCurrentInstance();
			// context.execute("PF('createPhysicalsDialog').hide()");
			context.update("form:messages");
			facesUtil.sendConfirmMessage("Se crearon las notificaciones correctamente", "");

		} catch (Exception exceptionReturn) {
			
			
				if (exceptionReturn instanceof MultipleErrorsException) {
					for (Exception err : ((MultipleErrorsException) exceptionReturn).getErrors()) {
						facesUtil.sendErrorMessage(err.getMessage());
					}
				} else if (exceptionReturn instanceof NullPointerException) {
					facesUtil.sendErrorMessage("Existen valores nulos.");
				} else {
					facesUtil.sendErrorMessage(exceptionReturn.getMessage());
				}
			
		}

	}

	private void validateVirtualNotification(Sale sale) throws MultipleErrorsException {

		List<Exception> errors = new ArrayList<Exception>();

		//for (Sale sale : sales) {

			if (sessionBean.getBank().getId() != sale.getBank().getId()) {
				errors.add(new BankInvalidException());
			}

			// VALIDA EL ESTADO DE LA ULTIMA NOTIFICACION NO SEA CERRADO
			if (sale.getNotification() != null) {
				if (sale.getNotification().getState().getPending() == false) {
					errors.add(new NotificationClosedException(sale.getCode()));
				}
			}

			// VALIDA SI LA VENTA NO ESTA ACTIVA
			if (!sale.getSaleState().getState().equals(SaleStateEnum.ACTIVE)) {
				errors.add(new SaleStateNoActiveException(sale.getCode()));
			}

			// VALIDA DATOS DE RESPONSABLE
			if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
				errors.add(new SaleStateNoActiveException(sale.getCode()));
			}
			if (sale.getPayer().getFirstnameResponsible() == null
					|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
				errors.add(new NullException("NOMBRE", sale.getCode()));
			}
			if (sale.getPayer().getLastnamePaternalResponsible() == null
					|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
				errors.add(new NullException("APELLIDO PATERNO", sale.getCode()));
			}
			if (sale.getPayer().getLastnameMaternalResponsible() == null
					|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
				errors.add(new NullException("APELLIDO MATERNO", sale.getCode()));
			}
			if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
				errors.add(new NullException("DIRECCIÓN", sale.getCode()));
			}
			if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
				errors.add(new NullException("PROVINCIA", sale.getCode()));//
			}
			if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
				errors.add(new NullException("DEPARTAMENTO", sale.getCode()));//
			}
			if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
				errors.add(new NullException("DISTRITO", sale.getCode()));//
			}

			// VALIDA CANTIDAD DE NOTIFICACIONES VIRTUALES ENVIADAS
			// Solo puede tener 3 notificaciones virtuales.
			if (sale.getVirtualNotifications() != null && sale.getVirtualNotifications() > 2) {

				errors.add(new NotificationLimit3Exception(sale.getCode()));
			}
			
			if (errors.size()>0) {
				throw new MultipleErrorsException(errors);
			}

		//}

	}

	public void printLetters() {

		try {

			System.out.println("Ingreso a printNotification");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			if (sessionBean.getBank() == null || sessionBean.getBank().getId() == null) {
				throw new BankNotSelectedException();
			}

			BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sessionBean.getBank().getId());
			if (bankLetterEnum == null) {
				throw new BankLetterNotFoundException(sessionBean.getBank().getName());
			}

			// SEARCH

			List<Sale> salesFound = searchAll();

			// search();

			List<Exception> errors = new ArrayList<Exception>();

			// if (sales != null && sales.size() > 0) {

			for (Sale sale : salesFound) {

				if (sessionBean.getBank().getId() != sale.getBank().getId()) {
					errors.add(new BankInvalidException());
				}

				if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
					errors.add(new SaleStateNoActiveException(sale.getCode()));
				}
				if (sale.getPayer().getFirstnameResponsible() == null
						|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
					errors.add(new NullException("NOMBRE", sale.getCode()));
				}
				if (sale.getPayer().getLastnamePaternalResponsible() == null
						|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
					errors.add(new NullException("APELLIDO PATERNO", sale.getCode()));
				}
				if (sale.getPayer().getLastnameMaternalResponsible() == null
						|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
					errors.add(new NullException("APELLIDO MATERNO", sale.getCode()));
				}
				if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
					errors.add(new NullException("DIRECCIÓN", sale.getCode()));
				}
				if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
					errors.add(new NullException("PROVINCIA", sale.getCode()));//
				}
				if (sale.getPayer().getDepartment() == null || sale.getPayer().getDepartment().trim().length() == 0) {
					errors.add(new NullException("DEPARTAMENTO", sale.getCode()));//
				}
				if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
					errors.add(new NullException("DISTRITO", sale.getCode()));//
				}

				// Si tiene menos de 3 envios virtuales
				if (sale.getVirtualNotifications() == null || sale.getVirtualNotifications() < 3) {
					if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 1) {
						// no se agrega porque tiene 2 envíos físicos y
						// menos de 3 virtuales.
						errors.add(new NotificationLimit2Exception(sale.getCode()));
					}
				} else {
					if (sale.getPhysicalNotifications() != null && sale.getPhysicalNotifications() > 0) {
						// No se agrega porque ya tiene 1 envío físico y 3
						// envíos virtuales.
						errors.add(new NotificationLimit1Exception(sale.getCode()));
					}
				}

				// VALIDATE COMMERCIAL CODE
				/*
				 * Commerce commercialCodeObject = null; for (Commerce commerce
				 * : commerces) { if
				 * (sale.getCommerce().getCode().equals(commerce.getCode())) {
				 * commercialCodeObject = commerce; break; } } if
				 * (commercialCodeObject == null) { errors.add(new
				 * CommerceCodeException(sale.getCode(),sale.getCommerce().
				 * getCode())); }
				 */

				// validacion de confirmacion de imprimir.
				// preguntar si desea agregar notificación a las ventas.

			}

			if (errors.size() > 0) {
				for (Exception e : errors) {
					facesUtil.sendErrorMessage(e.getMessage());
				}
			} else {

				Map<String, Object> parameters = new HashMap<String, Object>();

				ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
						.getContext();
				String separator = System.getProperty("file.separator");
				String rootPath = servletContext.getRealPath(separator);
				String templatePath = rootPath + "resources" + separator + "templates" + separator;

				String fileName = "";
				if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
					fileName = templatePath + "lettersFalabella.jrxml";
				} else if (bankLetterEnum.equals(BankLetterEnum.GNB)) {
					fileName = templatePath + "lettersGNB.jrxml";
				}

				parameters.put("sales", salesFound);
				parameters.put("ROOT_DIR", templatePath);
				parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "pe"));

				JasperReport report = JasperCompileManager.compileReport(fileName);
				JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

				FacesContext facesContext = FacesContext.getCurrentInstance();
				ExternalContext externalContext = facesContext.getExternalContext();
				externalContext.setResponseContentType("application/pdf");
				externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"cartas.pdf\"");

				/*
				 * File file = new File("temp"); FileOutputStream fos = new
				 * FileOutputStream(file);
				 */

				// externalContext.getResponseOutputStream().write(fos.);

				JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
				facesContext.responseComplete();

			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}

	public LazyDataModel<Sale> getSales() {
		return sales;
	}

	public void setSales(LazyDataModel<Sale> sales) {
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

	public String getSearchTypeSelected() {
		return searchTypeSelected;
	}

	public void setSearchTypeSelected(String searchTypeSelected) {
		this.searchTypeSelected = searchTypeSelected;
	}

	/*
	 * public Boolean getSearchByDocumentNumberResponsibleRendered() { return
	 * searchByDocumentNumberResponsibleRendered; }
	 * 
	 * public void setSearchByDocumentNumberResponsibleRendered(Boolean
	 * searchByDocumentNumberResponsibleRendered) {
	 * this.searchByDocumentNumberResponsibleRendered =
	 * searchByDocumentNumberResponsibleRendered; }
	 * 
	 * public Boolean getSearchByDateSaleRendered() { return
	 * searchByDateSaleRendered; }
	 * 
	 * public void setSearchByDateSaleRendered(Boolean searchByDateSaleRendered)
	 * { this.searchByDateSaleRendered = searchByDateSaleRendered; }
	 */

	public Sale getSaleSelected() {
		return saleSelected;
	}

	public void setSaleSelected(Sale saleSelected) {
		this.saleSelected = saleSelected;
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

	public String getNuicResponsible() {
		return nuicResponsible;
	}

	public void setNuicResponsible(String nuicResponsible) {
		this.nuicResponsible = nuicResponsible;
	}

	public List<PayerHistory> getPayers() {
		return payers;
	}

	public void setPayers(List<PayerHistory> payers) {
		this.payers = payers;
	}

	public Date getSendingAtForPhysicals() {
		return sendingAtForPhysicals;
	}

	public void setSendingAtForPhysicals(Date sendingAtForPhysicals) {
		this.sendingAtForPhysicals = sendingAtForPhysicals;
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

	public Date getSendingDate() {
		return sendingDate;
	}

	public void setSendingDate(Date sendingDate) {
		this.sendingDate = sendingDate;
	}

	public List<SelectItem> getNotificationTypes() {
		return notificationTypes;
	}

	public void setNotificationTypes(List<SelectItem> notificationTypes) {
		this.notificationTypes = notificationTypes;
	}

	public String getNotificationTypeSelected() {
		return notificationTypeSelected;
	}

	public void setNotificationTypeSelected(String notificationTypeSelected) {
		this.notificationTypeSelected = notificationTypeSelected;
	}

	public Boolean getWithoutMail() {
		return withoutMail;
	}

	public void setWithoutMail(Boolean withoutMail) {
		this.withoutMail = withoutMail;
	}

	public Boolean getWithoutAddress() {
		return withoutAddress;
	}

	public void setWithoutAddress(Boolean withoutAddress) {
		this.withoutAddress = withoutAddress;
	}

	public Boolean getWithoutNotification() {
		return withoutNotification;
	}

	public void setWithoutNotification(Boolean withoutNotification) {
		this.withoutNotification = withoutNotification;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumberForPhysicals() {
		return orderNumberForPhysicals;
	}

	public void setOrderNumberForPhysicals(String orderNumberForPhysicals) {
		this.orderNumberForPhysicals = orderNumberForPhysicals;
	}

	public String getDepartmentSelected() {
		return departmentSelected;
	}

	public void setDepartmentSelected(String departmentSelected) {
		this.departmentSelected = departmentSelected;
	}

	public List<SelectItem> getDepartments() {
		return departments;
	}

	public void setDepartments(List<SelectItem> departments) {
		this.departments = departments;
	}

	public List<String> getProvincesSelected() {
		return provincesSelected;
	}

	public void setProvincesSelected(List<String> provincesSelected) {
		this.provincesSelected = provincesSelected;
	}

	public List<SelectItem> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<SelectItem> provinces) {
		this.provinces = provinces;
	}
	
	

}
