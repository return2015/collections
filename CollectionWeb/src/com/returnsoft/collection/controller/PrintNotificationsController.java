package com.returnsoft.collection.controller;

import java.io.Serializable;
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
import javax.servlet.ServletContext;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.CommerceCodeException;
import com.returnsoft.collection.exception.DataCommerceCodeException;
import com.returnsoft.collection.exception.PayerDataNullException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
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
public class PrintNotificationsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private SaleService saleService;
	
	@EJB
	private UserService userService;

	private String searchTypeSelected;

	private List<Sale> sales;
	//private Sale saleSelected;

	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;

	private Date affiliationDate;

	private String nuicResponsible;

	/*private List<SelectItem> banks;
	private String bankSelected;

	private List<SelectItem> products;
	private String productSelected;*/
	
	private List<SelectItem> saleStates;
	private String saleStateSelected;

	private Boolean searchByDocumentNumberResponsibleRendered;
	private Boolean searchByDateSaleRendered;

	//private List<CreditCard> updates;
	//private List<Collection> collections;
	//private List<SaleState> maintenances;
	//private List<Notification> notifications;
	//private List<Payer> payers;
	//private List<Repayment> repayments;

	private List<Commerce> commerces;
	
	private FacesUtil facesUtil;
	
	//private String passwordSupervisor;
	
	//private Boolean supervisorAccess;
	
	private Integer salesCount;

	public PrintNotificationsController() {
		System.out.println("Se construye PrintNotificationsController");
		facesUtil = new FacesUtil();
	}

	public String initialize() {
		
		System.out.println("inicializando PrintNotificationsController");

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
					System.out.println("cantidad de commerce encontrados:"+commerces.size());
				}
				
				/*List<Bank> banksEntity = saleService.getBanks();
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setValue(bank.getId().toString());
					item.setLabel(bank.getName());
					banks.add(item);
				}*/

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
			
			//saleSelected=null;
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");
			Short bankId = sessionBean.getBank().getId();
			
			
			if (bankId!=null) {
				BankLetterEnum bankLetterEnum = BankLetterEnum.findById(bankId);
				if (bankLetterEnum!=null) {
					if (searchTypeSelected.equals("dni")) {
						Long nuicResponsibleLong = Long.parseLong(nuicResponsible);
						sales = saleService
								.findSalesByNuicResponsible(nuicResponsibleLong);
					} else if (searchTypeSelected.equals("saleData")) {
						Short productId = null;

						/*SaleStateEnum saleState = null;
						if (saleStateSelected != null && saleStateSelected.length() > 0) {
							saleState = SaleStateEnum.findById(Short.parseShort(saleStateSelected));
						}*/
						sales = saleService.findSalesBySaleData(dateOfSaleStarted,
								dateOfSaleEnded, affiliationDate, bankId, productId,
								SaleStateEnum.ACTIVE);
					}
					
				}else{
					facesUtil.sendErrorMessage("","No se encontró formato de carta para el banco seleccionado");
				}
				
			}else{
				facesUtil.sendErrorMessage("","Debe seleccionar banco");
			}
						

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}

	
	
	
	
	public void printNotification(){
		
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
					
					SessionBean sessionBean = (SessionBean) FacesContext
							.getCurrentInstance().getExternalContext()
							.getSessionMap().get("sessionBean");
					
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
					externalContext
							.setResponseContentType("application/pdf");
					externalContext.setResponseHeader("Content-Disposition",
							"attachment; filename=\"carta.pdf\"");
					
					JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
					
					facesContext.responseComplete();
					
				}
				
				// VALIDA SI LA VENTA NO ESTA ACTIVA
					//System.out.println("bankId:"+saleSelected.getCommerce().getBank().getId());
					
					/*BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getCommerce().getBank().getId());
					
					if (bankLetterEnum!=null) {
						
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
						
					}*/
				
				
				
			}else{
				System.out.println("No existen ventas para imprimir...");
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

	

}
