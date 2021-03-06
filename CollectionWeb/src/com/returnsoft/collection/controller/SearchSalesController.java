package com.returnsoft.collection.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.primefaces.model.UploadedFile;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.MultipleErrorsException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.lazy.SaleLazyModel;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CollectionPeriodService;
import com.returnsoft.collection.service.CreditCardService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

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
	private CreditCardService creditCardService;
	
	@EJB
	private SaleStateService saleStateService;


	@EJB
	private UserService userService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;

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
	
	private UploadedFile file;

	private List<SelectItem> personTypes;

	private List<SelectItem> searchTypes;

	private Sale saleSelected;

	private LazyDataModel<Sale> sales;
	
	private List<CreditCardHistory> creditCardsHistory;
	
	private List<SaleStateHistory> saleStatesHistory;

	////////////////////////////
	///////////////////

	public SearchSalesController() {
		System.out.println("Se construye SearchSaleController");
		// searchByNamesRendered=true;
		// facesUtil = new FacesUtil();
		/*
		 * sales = new LazyDataModel<Sale>() {
		 * 
		 * private static final long serialVersionUID = 1636739105248691317L;
		 * 
		 * @Override public int getPageSize() { // TODO Auto-generated method
		 * stub return super.getPageSize(); }
		 * 
		 * @Override public int getRowCount() { // TODO Auto-generated method
		 * stub return super.getRowCount(); }
		 * 
		 * @Override public Sale getRowData() { // TODO Auto-generated method
		 * stub return super.getRowData(); }
		 * 
		 * @Override public Sale getRowData(String rowKey) { // TODO
		 * Auto-generated method stub return super.getRowData(rowKey); }
		 * 
		 * @Override public int getRowIndex() { // TODO Auto-generated method
		 * stub return super.getRowIndex(); }
		 * 
		 * @Override public Object getRowKey(Sale object) { // TODO
		 * Auto-generated method stub return super.getRowKey(object); }
		 * 
		 * @Override public Object getWrappedData() { // TODO Auto-generated
		 * method stub return super.getWrappedData(); }
		 * 
		 * @Override public boolean isRowAvailable() { // TODO Auto-generated
		 * method stub return super.isRowAvailable(); }
		 * 
		 * @Override public List<Sale> load(int first, int pageSize,
		 * List<SortMeta> multiSortMeta, Map<String, Object> filters) { // TODO
		 * Auto-generated method stub return new ArrayList<Sale>(); }
		 * 
		 * @Override public List<Sale> load(int first, int pageSize, String
		 * sortField, SortOrder sortOrder, Map<String, Object> filters) { //
		 * TODO Auto-generated method stub return new ArrayList<Sale>(); }
		 * 
		 * @Override public void setPageSize(int pageSize) { // TODO
		 * Auto-generated method stub super.setPageSize(pageSize); }
		 * 
		 * @Override public void setRowCount(int rowCount) { // TODO
		 * Auto-generated method stub super.setRowCount(rowCount); }
		 * 
		 * @Override public void setRowIndex(int arg0) { // TODO Auto-generated
		 * method stub super.setRowIndex(arg0); }
		 * 
		 * @Override public void setWrappedData(Object list) { // TODO
		 * Auto-generated method stub super.setWrappedData(list); }
		 * 
		 * 
		 * };
		 */

	}

	public String initialize() {

		// System.out.println("initialize");

		/*
		 * if (searchTypeSelected==null) { System.out.println("search es nullo"
		 * ); }else{ System.out.println("search "+searchTypeSelected);
		 * onChangeSearchType(); }
		 * 
		 * if (personTypeSelected==null) { System.out.println("person es nullo"
		 * ); }else{ System.out.println("person "+personTypeSelected);
		 * onChangePersonType(); }
		 */

		// System.out.println(searchTypeSelected);
		// System.out.println(personTypeSelected);

		/*
		 * if (("personalData").equals(searchTypeSelected)) {
		 * 
		 * }
		 */

		try {
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			personTypes = new ArrayList<SelectItem>();
			SelectItem pt1 = new SelectItem("responsible", "Responsable");
			SelectItem pt2 = new SelectItem("insured", "Asegurado");
			SelectItem pt3 = new SelectItem("contractor", "Contratante");
			personTypes.add(pt1);
			personTypes.add(pt2);
			personTypes.add(pt3);

			searchTypes = new ArrayList<SelectItem>();
			SelectItem st1 = new SelectItem("creditCard", "Tarjeta Cr�dito");
			SelectItem st2 = new SelectItem("dni", "DNI");
			SelectItem st3 = new SelectItem("saleData", "Datos Venta");
			SelectItem st4 = new SelectItem("personalData", "Datos Persona");
			searchTypes.add(st1);
			searchTypes.add(st2);
			searchTypes.add(st3);
			searchTypes.add(st4);

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

			searchTypeSelected = "saleData";

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

	/*
	 * public void onChangeSearchType() { try {
	 * 
	 * // if (fromRequest) { //searchTypeSelected =
	 * FacesContext.getCurrentInstance().getExternalContext().
	 * getRequestParameterMap() // .get("form:searchType_input"); // }
	 * 
	 * System.out.println("onChangeSearchType");
	 * System.out.println("onChangeSearchType:" + searchTypeSelected);
	 * 
	 * 
	 * 
	 * searchByContractorRendered = false; searchByInsuredRendered = false;
	 * searchByResponsibleRendered = false; //personTypeSelected = "";
	 * 
	 * if (("creditCard").equals(searchTypeSelected)) {
	 * searchByCreditCardNumberRendered = true;
	 * searchByDocumentNumberResponsibleRendered = false; searchByNamesRendered
	 * = false; searchByDateSaleRendered = false; } else if
	 * (("dni").equals(searchTypeSelected)) { searchByCreditCardNumberRendered =
	 * false; searchByDocumentNumberResponsibleRendered = true;
	 * searchByNamesRendered = false; searchByDateSaleRendered = false; } else
	 * if (("saleData").equals(searchTypeSelected)) {
	 * searchByCreditCardNumberRendered = false;
	 * searchByDocumentNumberResponsibleRendered = false; searchByNamesRendered
	 * = false; searchByDateSaleRendered = true; } else if
	 * (("personalData").equals(searchTypeSelected)) {
	 * searchByCreditCardNumberRendered = false;
	 * searchByDocumentNumberResponsibleRendered = false; searchByNamesRendered
	 * = true; searchByDateSaleRendered = false; } else {
	 * searchByCreditCardNumberRendered = false;
	 * searchByDocumentNumberResponsibleRendered = false; searchByNamesRendered
	 * = false; searchByDateSaleRendered = false; } } catch (Exception e) {
	 * e.printStackTrace(); facesUtil.sendErrorMessage( e.getMessage()); } }
	 */

	/*
	 * public void metodo(ComponentSystemEvent e){ System.out.println(
	 * "ingreso a metodo"); }
	 */

	/*
	 * public void onChangePersonType(){ try { // if (fromRequest) {
	 * //personTypeSelected =
	 * FacesContext.getCurrentInstance().getExternalContext().
	 * getRequestParameterMap() // .get("form:personType_input"); // }
	 * 
	 * System.out.println("onChangePersonType");
	 * 
	 * //onChangeSearchType();
	 * 
	 * 
	 * System.out.println("onChangePersonType:" + personTypeSelected); if
	 * (("contractor").equals(personTypeSelected)) { searchByContractorRendered
	 * = true; searchByInsuredRendered = false; searchByResponsibleRendered =
	 * false; } else if (("insured").equals(personTypeSelected)) {
	 * searchByContractorRendered = false; searchByInsuredRendered = true;
	 * searchByResponsibleRendered = false; } else if
	 * (("responsible").equals(personTypeSelected)) { searchByContractorRendered
	 * = false; searchByInsuredRendered = false; searchByResponsibleRendered =
	 * true; } else { searchByContractorRendered = false;
	 * searchByInsuredRendered = false; searchByResponsibleRendered = false; } }
	 * catch (Exception e) { e.printStackTrace();
	 * facesUtil.sendErrorMessage(e.getMessage()); } }
	 */

	public void loadSales() {
		try {

			System.out.println("loadSales");

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
			RequestContext.getCurrentInstance().openDialog("load_sales", options, null);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void afterLoadSales(SelectEvent event) {
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
				facesUtil.sendConfirmMessage("Se cre� el lote satisfactorimente.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void loadSaleStates() {
		try {
			System.out.println("loadSaleStates");
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", true);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 200);
			options.put("contentWidth", 500);

			RequestContext.getCurrentInstance().openDialog("load_sale_states", options, null);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void afterLoadSaleStates(SelectEvent event) {
		try {
			System.out.println("afterLoadSaleStates");
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
				facesUtil.sendConfirmMessage("Se cre� el lote satisfactorimente.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}
	
	
	public void loadCreditCards() {
		try {
			System.out.println("loadCreditCards");
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}
			Map<String, Object> options = new HashMap<String, Object>();
			options.put("modal", true);
			options.put("draggable", true);
			options.put("resizable", false);
			options.put("contentHeight", 200);
			options.put("contentWidth", 500);

			RequestContext.getCurrentInstance().openDialog("load_credit_cards", options, null);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void afterLoadCreditCards(SelectEvent event) {
		try {
			System.out.println("afterLoadCreditCards");
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
				facesUtil.sendConfirmMessage("Se cre� el lote satisfactorimente.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void search() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			saleSelected = null;

			if (searchTypeSelected.equals("creditCard")) {
				Long creditCardNumberLong = Long.parseLong(creditCardNumber);
				 List<Sale> salesFound =  saleService.findSalesByCreditCardNumber(creditCardNumberLong);
				//System.out.println("Se inicia la busqueda XXX");
				 sales = new SaleLazyModel(salesFound);
					
				//sales = new SaleLazyModel(saleService, searchTypeSelected, creditCardNumberLong);

				//System.out.println("Se termina la busqueda XXX");

			} else if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				 List<Sale> salesFound = saleService.findSalesByNuicResponsible(nuicResponsibleLong);

				//System.out.println("Se inicia la busqueda XXX");

				sales = new SaleLazyModel(salesFound);
				//sales.setWrappedData(salesFound);
				//sales.setRowCount(salesFound.size());

				//System.out.println("Se termina la busqueda XXX");

			} else if (searchTypeSelected.equals("saleData")) {
				
				//System.out.println("Ingreso a buscar por datos de venta.");
				// System.out.println(""+dateOfSaleStarted);
				// System.out.println(""+dateOfSaleEnded);
				final short productId;
				// System.out.println(productSelected);
				if (productSelected != null && productSelected.length() > 0) {
					productId = Short.parseShort(productSelected);
				} else {
					productId = 0;
				}
				final short bankId;
				// System.out.println(bankSelected);
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				} else {
					bankId = 0;
				}
				final SaleStateEnum saleState;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				} else {
					saleState = null;
				}


				sales = new SaleLazyModel(saleService, dateOfSaleStarted, dateOfSaleEnded, bankId, productId,
						saleState);
				
			

				// System.out.println("Se termina la busqueda XXX");

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

						//ystem.out.println("Se inicia la busqueda XXX");
						List<Sale> salesFound = saleService.findSalesByNamesContractor(nuicContractorLong, firstnameContractor, lastnamePaternalContractor, lastnameMaternalContractor);
						sales = new SaleLazyModel(salesFound);
						//sales.setWrappedData(salesFound);
						//sales.setRowCount(salesFound.size());
						
						/*sales = new SaleLazyModel(saleService, personTypeSelected, nuicContractorLong,
								firstnameContractor, lastnamePaternalContractor, lastnameMaternalContractor);*/
						//System.out.println("Se termina la busqueda XXX");

					} else {

						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
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

						// sales =
						// saleService.findSalesByNamesInsured(nuicInsuredLong,
						// firstnameInsured,
						// lastnamePaternalInsured, lastnameMaternalInsured);
						List<Sale> salesFound = saleService.findSalesByNamesInsured(nuicInsuredLong, firstnameInsured, lastnamePaternalInsured, lastnameMaternalInsured);
						sales = new SaleLazyModel(salesFound);
						//sales.setWrappedData(salesFound);
						//sales.setRowCount(salesFound.size());
						
						/*System.out.println("Se inicia la busqueda XXX");
						sales = new SaleLazyModel(saleService, personTypeSelected, nuicInsuredLong, firstnameInsured,
								lastnamePaternalInsured, lastnameMaternalInsured);*/
						//System.out.println("Se termina la busqueda XXX");

					} else {
						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
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

						// sales =
						// saleService.findSalesByNamesResponsible(nuicResponsibleLong,
						// firstnameResponsible,
						// lastnamePaternalResponsible,
						// lastnameMaternalResponsible);
						List<Sale> salesFound = saleService.findSalesByNamesResponsible(nuicResponsibleLong, firstnameResponsible, lastnamePaternalResponsible, lastnameMaternalResponsible);
						sales = new SaleLazyModel(salesFound);
						//sales.setWrappedData(salesFound);
						//sales.setRowCount(salesFound.size());
						
						/*System.out.println("Se inicia la busqueda XXX");
						sales = new SaleLazyModel(saleService, personTypeSelected, nuicResponsibleLong,
								firstnameResponsible, lastnamePaternalResponsible, lastnameMaternalResponsible);
						System.out.println("Se termina la busqueda XXX");*/

					} else {
						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
					}
				}

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

			List<Sale> salesFound = new ArrayList<Sale>();

			if (searchTypeSelected.equals("creditCard")) {
				Long creditCardNumberLong = Long.parseLong(creditCardNumber);
				salesFound =  saleService.findSalesByCreditCardNumber(creditCardNumberLong);

			} else if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				// sales =
				// saleService.findSalesByNuicResponsible(nuicResponsibleLong);

				//System.out.println("Se inicia la busqueda XXX");

				// sales = new SaleLazyModel(saleService, searchTypeSelected,
				// nuicResponsibleLong);
				salesFound = saleService.findSalesByNuicResponsible(nuicResponsibleLong);
				//System.out.println("Se termina la busqueda XXX");

			} else if (searchTypeSelected.equals("saleData")) {

				final short productId;
				if (productSelected != null && productSelected.length() > 0) {
					productId = Short.parseShort(productSelected);
				} else {
					productId = 0;
				}
				
				final short bankId;
				if (bankSelected != null && bankSelected.length() > 0) {
					bankId = Short.parseShort(bankSelected);
				} else {
					bankId = 0;
				}
				final SaleStateEnum saleState;
				if (saleStateSelected != null && saleStateSelected.length() > 0) {
					saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
				} else {
					saleState = null;
				}

				salesFound = saleService.findSalesBySaleData(dateOfSaleStarted, dateOfSaleEnded, bankId, productId,
						saleState);

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

						salesFound = saleService.findSalesByNamesContractor(nuicContractorLong, firstnameContractor,
								lastnamePaternalContractor, lastnameMaternalContractor);

					} else {

						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
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

						salesFound = saleService.findSalesByNamesInsured(nuicInsuredLong, firstnameInsured,
								lastnamePaternalInsured, lastnameMaternalInsured);

					} else {
						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
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

						salesFound = saleService.findSalesByNamesResponsible(nuicResponsibleLong, firstnameResponsible,
								lastnamePaternalResponsible, lastnameMaternalResponsible);

					} else {
						facesUtil.sendErrorMessage("Debe ingresar al menos un dato");
					}
				}

			}

			////////////

			StringBuilder cadena = new StringBuilder();
			String separator = "|";
			String header = "";

			header += "COD UNICO" + separator;
			header += "TIPO DOC" + separator;
			header += "NUIC RESP PAGO" + separator;
			header += "AP PAT RESP PAGO" + separator;
			header += "AP MAT RESP PAGO" + separator;
			header += "NOMBRES RESP PAGO" + separator;
			header += "N� TARJETA DE CREDITO" + separator;
			header += "N� DE CUENTA" + separator;
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

			//search();

			SimpleDateFormat sdf1 = new SimpleDateFormat("MM/yyyy");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			int count=0;

			for (Sale sale : salesFound) {
				
				count++;
				System.out.println("count:"+count);

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
				cadena.append(sale.getCollectionPeriod().getName() + separator);
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
			response.setHeader("Content-Disposition", "attachment; filename=\"ventas.txt\"");

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

	@SuppressWarnings("unchecked")
	public void exportExcel() throws IOException {

		try {

			// List<Sale> sales = new ArrayList<Sale>();

			// search();

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
			row.createCell(6).setCellValue("N� TARJETA DE CREDITO");
			row.createCell(7).setCellValue("N� DE CUENTA");
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


			int lineNumber = 1;
			

			if (sales != null && sales.getWrappedData()!=null && ((List<Sale>)sales.getWrappedData()).size() > 1) {

				for (Sale sale : (List<Sale>)sales.getWrappedData()) {

					//System.out.println("Procesando a excel:" + lineNumber);
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
					rowBody.createCell(18).setCellValue(sale.getPhone1()!=null?sale.getPhone1().toString():"");
					rowBody.createCell(19).setCellValue(sale.getPhone2()!=null?sale.getPhone2().toString():"");
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

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

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
	
	public void showCreditCards() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			creditCardsHistory = creditCardService.findBySale(saleSelected.getId());

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}

	}
	
	public void showSaleStates() {

		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			saleStatesHistory = saleStateService.findBySale(saleSelected.getId());

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

	public String getPersonTypeSelected() {
		return personTypeSelected;
	}

	public void setPersonTypeSelected(String personTypeSelected) {
		this.personTypeSelected = personTypeSelected;
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

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<SelectItem> getPersonTypes() {
		return personTypes;
	}

	public void setPersonTypes(List<SelectItem> personTypes) {
		this.personTypes = personTypes;
	}

	public List<SelectItem> getSearchTypes() {
		return searchTypes;
	}

	public void setSearchTypes(List<SelectItem> searchTypes) {
		this.searchTypes = searchTypes;
	}

	public List<CreditCardHistory> getCreditCardsHistory() {
		return creditCardsHistory;
	}

	public void setCreditCardsHistory(List<CreditCardHistory> creditCardsHistory) {
		this.creditCardsHistory = creditCardsHistory;
	}

	public List<SaleStateHistory> getSaleStatesHistory() {
		return saleStatesHistory;
	}

	public void setSaleStatesHistory(List<SaleStateHistory> saleStatesHistory) {
		this.saleStatesHistory = saleStatesHistory;
	}

	



	
}
