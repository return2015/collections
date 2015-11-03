package com.returnsoft.collection.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Product;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.CommerceService;
import com.returnsoft.collection.service.ProductService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class SearchSalesController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB
	private CommerceService commerceService;
	
	@EJB
	private BankService bankService;
	
	@EJB
	private ProductService productService;

	@EJB
	private SaleService saleService;
	
	@EJB
	private UserService userService;

	private String searchTypeSelected;
	private String personTypeSelected;

	private List<Sale> sales;
	private Sale saleSelected;

	private String creditCardNumber;
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;

	private Date affiliationDate;

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

	private List<Commerce> commerces;
	
	private FacesUtil facesUtil;
		
	private Boolean supervisorAccess;
	
	private Integer salesCount;

	public SearchSalesController() {
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
					commerces = commerceService.findByBank(bankId);
					System.out.println("cantidad de commerce encontrados:"+commerces.size());
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
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	public void onChangePersonType() {
		try {
			// if (fromRequest) {
			personTypeSelected = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
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
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}

	public void search() {

		try {
			
			saleSelected=null;

			if (searchTypeSelected.equals("creditCard")) {
				Long creditCardNumberLong = Long.parseLong(creditCardNumber);
				sales = saleService
						.findSalesByCreditCardNumber(creditCardNumberLong);
			} else if (searchTypeSelected.equals("dni")) {
				Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
				sales = saleService
						.findSalesByNuicResponsible(nuicResponsibleLong);
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
				sales = saleService.findSalesBySaleData(dateOfSaleStarted,
						dateOfSaleEnded, affiliationDate, bankId, productId,
						saleState);

			} else if (searchTypeSelected.equals("personalData")) {
				if (personTypeSelected.equals("contractor")) {

					if ((nuicContractor != null && nuicContractor.length() > 0)
							|| (firstnameContractor != null && firstnameContractor
									.length() > 0)
							|| (lastnamePaternalContractor != null && lastnamePaternalContractor
									.length() > 0)
							|| (lastnameMaternalContractor != null && lastnameMaternalContractor
									.length() > 0)) {

						Long nuicContractorLong = null;
						if (nuicContractor != null
								&& nuicContractor.length() > 0) {
							nuicContractorLong = Long.parseLong(nuicContractor);
						}

						sales = saleService.findSalesByNamesContractor(
								nuicContractorLong, firstnameContractor,
								lastnamePaternalContractor,
								lastnameMaternalContractor);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("insured")) {

					if ((nuicInsured != null && nuicInsured.length() > 0)
							|| (firstnameInsured != null && firstnameInsured
									.length() > 0)
							|| (lastnamePaternalInsured != null && lastnamePaternalInsured
									.length() > 0)
							|| (lastnameMaternalInsured != null && lastnameMaternalInsured
									.length() > 0)) {

						Long nuicInsuredLong = null;
						if (nuicInsured != null && nuicInsured.length() > 0) {
							nuicInsuredLong = Long.parseLong(nuicInsured);
						}

						sales = saleService.findSalesByNamesInsured(
								nuicInsuredLong, firstnameInsured,
								lastnamePaternalInsured,
								lastnameMaternalInsured);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}

				} else if (personTypeSelected.equals("responsible")) {

					if ((nuicResponsible != null && nuicResponsible.length() > 0)
							|| (firstnameResponsible != null && firstnameResponsible
									.length() > 0)
							|| (lastnamePaternalResponsible != null && lastnamePaternalResponsible
									.length() > 0)
							|| (lastnameMaternalResponsible != null && lastnameMaternalResponsible
									.length() > 0)) {

						Long nuicResponsibleLong = null;
						if (nuicResponsible != null
								&& nuicResponsible.length() > 0) {
							nuicResponsibleLong = Long
									.parseLong(nuicResponsible);
						}

						sales = saleService.findSalesByNamesResponsible(
								nuicResponsibleLong, firstnameResponsible,
								lastnamePaternalResponsible,
								lastnameMaternalResponsible);

					} else {
						FacesMessage msg = new FacesMessage(
								"Debe ingresar al menos un dato");
						msg.setSeverity(FacesMessage.SEVERITY_ERROR);
						FacesContext.getCurrentInstance().addMessage(null, msg);
					}
				}
				
				

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

	public void setLastnamePaternalResponsible(
			String lastnamePaternalResponsible) {
		this.lastnamePaternalResponsible = lastnamePaternalResponsible;
	}

	public String getLastnameMaternalResponsible() {
		return lastnameMaternalResponsible;
	}

	public void setLastnameMaternalResponsible(
			String lastnameMaternalResponsible) {
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

	public void setSearchByCreditCardNumberRendered(
			Boolean searchByCreditCardNumberRendered) {
		this.searchByCreditCardNumberRendered = searchByCreditCardNumberRendered;
	}

	public Boolean getSearchByDocumentNumberResponsibleRendered() {
		return searchByDocumentNumberResponsibleRendered;
	}

	public void setSearchByDocumentNumberResponsibleRendered(
			Boolean searchByDocumentNumberResponsibleRendered) {
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

	public void setSearchByResponsibleRendered(
			Boolean searchByResponsibleRendered) {
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

	

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
	}


	public List<SelectItem> getSaleStates() {
		return saleStates;
	}

	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}

	

	public Boolean getSupervisorAccess() {
		return supervisorAccess;
	}

	public void setSupervisorAccess(Boolean supervisorAccess) {
		this.supervisorAccess = supervisorAccess;
	}

	public Integer getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}
	

}
