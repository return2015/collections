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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.CommerceCodeException;
import com.returnsoft.collection.exception.NotificationLimit1Exception;
import com.returnsoft.collection.exception.NotificationLimit2Exception;
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
public class PrintNotificationsController implements Serializable {

	/**
	 * 
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
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;
	private Date affiliationDate;
	private String nuicResponsible;
	private Boolean searchByDocumentNumberResponsibleRendered;
	private Boolean searchByDateSaleRendered;

	private List<Commerce> commerces;
	
	private FacesUtil facesUtil;
	
	private Date sendingAt;
	
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
					
				if (sessionBean.getBank()!=null && sessionBean.getBank().getId()!=null) {
					Short bankId = sessionBean.getBank().getId();
					System.out.println("id de banco"+bankId);
					commerces = saleService.findCommercesByBankId(bankId);
					System.out.println("cantidad de commerce encontrados:"+commerces.size());
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
			row.createCell(6).setCellValue("E-MAIL");
			row.createCell(7).setCellValue("DEPARTAMENTO");
			row.createCell(8).setCellValue("PROVINCIA");
			row.createCell(9).setCellValue("DISTRITO");
			row.createCell(10).setCellValue("DIRECCION");
			
			row.createCell(11).setCellValue("FECHA DE VENTA");
			row.createCell(12).setCellValue("ESTADO");
			row.createCell(13).setCellValue("FECHA DE AFILIACION");
			row.createCell(14).setCellValue("NOTIFICACIONES VIRTUALES");
			row.createCell(15).setCellValue("NOTIFICACIONES FÍSICAS");
			row.createCell(16).setCellValue("TIPO");
			row.createCell(17).setCellValue("ESTADO");
			row.createCell(18).setCellValue("FECHA ENVÍO");
			row.createCell(19).setCellValue("FECHA RESPUESTA");
			row.createCell(20).setCellValue("NÚMERO DE ORDEN");
			row.createCell(21).setCellValue("NÚMERO CORRELATIVO");


			for (int i = 0; i < sales.size(); i++) {
				Sale sale = sales.get(i);
				XSSFRow rowBody = sheet.createRow(i + 1);

				rowBody.createCell(0).setCellValue(sale.getCode());
				rowBody.createCell(1).setCellValue(sale.getDocumentType());
				rowBody.createCell(2).setCellValue(sale.getPayer().getNuicResponsible());
				rowBody.createCell(3).setCellValue(sale.getPayer().getLastnamePaternalResponsible());
				rowBody.createCell(4).setCellValue(sale.getPayer().getLastnameMaternalResponsible());
				rowBody.createCell(5).setCellValue(sale.getPayer().getFirstnameResponsible());
				rowBody.createCell(6).setCellValue(sale.getPayer().getMail());
				rowBody.createCell(7).setCellValue(sale.getPayer().getDepartment());
				rowBody.createCell(8).setCellValue(sale.getPayer().getProvince());
				rowBody.createCell(9).setCellValue(sale.getPayer().getDistrict());
				rowBody.createCell(10).setCellValue(sale.getPayer().getAddress());
				
				rowBody.createCell(11).setCellValue(sdf2.format(sale.getDateOfSale()));
				rowBody.createCell(12).setCellValue(sale.getSaleState().getState().getName());
				rowBody.createCell(13).setCellValue(sale.getAffiliationDate()!=null?sdf2.format(sale.getAffiliationDate()):"");
				rowBody.createCell(14).setCellValue(sale.getVirtualNotifications());
				rowBody.createCell(15).setCellValue(sale.getPhysicalNotifications());
				
				if (sale.getNotification()!=null) {
					rowBody.createCell(16).setCellValue(sale.getNotification().getType()!=null?sale.getNotification().getType().getName():"");
					rowBody.createCell(17).setCellValue(sale.getNotification().getState()!=null?sale.getNotification().getState().getName():"");
					rowBody.createCell(18).setCellValue(sale.getNotification().getSendingAt()!=null?sdf2.format(sale.getNotification().getSendingAt()):"");
					rowBody.createCell(19).setCellValue(sale.getNotification().getAnsweringAt()!=null?sdf2.format(sale.getNotification().getAnsweringAt()):"");
					rowBody.createCell(20).setCellValue(sale.getNotification().getOrderNumber()!=null?sale.getNotification().getOrderNumber():"");
					rowBody.createCell(21).setCellValue(sale.getNotification().getCorrelativeNumber()!=null?sale.getNotification().getCorrelativeNumber():"");
					
				}else{
					rowBody.createCell(16).setCellValue("");
					rowBody.createCell(17).setCellValue("");
					rowBody.createCell(18).setCellValue("");
					rowBody.createCell(19).setCellValue("");
					rowBody.createCell(20).setCellValue("");
					rowBody.createCell(21).setCellValue("");
				}
				
				
			
				
			}

			/*
			 * HSSFWorkbook workbook = new HSSFWorkbook(); HSSFSheet sheet =
			 * workbook.createSheet(); HSSFRow row = sheet.createRow(0);
			 * HSSFCell cell = row.createCell(0); cell.setCellValue(0.0);
			 */

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext
					.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			externalContext.setResponseHeader("Content-Disposition",
					"attachment; filename=\"notificaciones_por_venta.xlsx\"");

			wb.write(externalContext.getResponseOutputStream());
			facesContext.responseComplete();

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
			  
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
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

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
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

	public Date getSendingAt() {
		return sendingAt;
	}

	public void setSendingAt(Date sendingAt) {
		this.sendingAt = sendingAt;
	}

	

}
