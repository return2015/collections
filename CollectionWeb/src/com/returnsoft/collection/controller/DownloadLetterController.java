package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.NullException;
import com.returnsoft.collection.exception.SaleStateNoActiveException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@Named
@RequestScoped
public class DownloadLetterController implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 658708909322502187L;

	private String code;

	@EJB
	private SaleService saleService;
	
	@EJB
	private NotificationService notificationService;

	@Inject
	private FacesUtil facesUtil;

	public void start() {

		try {

			//System.out.println("Ingreso a start: " + code);
			//facesUtil = new FacesUtil();
			//List<Exception> errors = new ArrayList<Exception>();
			
			if (code == null || code.length() == 0) {
				throw new Exception();
			}
			
			Sale sale = saleService.findByCode(code);
			
			if (sale==null || sale.getId()==null) {
				throw new Exception();
			}
			
			if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
				throw new SaleStateNoActiveException(sale.getCode());
			}
			if (sale.getPayer().getFirstnameResponsible() == null
					|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
				throw new NullException("NOMBRE", sale.getCode());
			}
			if (sale.getPayer().getLastnamePaternalResponsible() == null
					|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
				throw new NullException("APELLIDO PATERNO", sale.getCode());
			}
			if (sale.getPayer().getLastnameMaternalResponsible() == null
					|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
				throw new NullException("APELLIDO MATERNO", sale.getCode());
			}
			if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
				throw new NullException("DIRECCIÓN", sale.getCode());
			}
			if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
				throw new NullException("PROVINCIA", sale.getCode());//
			}
			if (sale.getPayer().getDepartment() == null
					|| sale.getPayer().getDepartment().trim().length() == 0) {
				throw new NullException("DEPARTAMENTO", sale.getCode());//
			}
			if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
				throw new NullException("DISTRITO", sale.getCode());//
			}
			
			if (sale.getBank()==null || sale.getBank().getId()==null) {
				throw new Exception();
			}
			
			BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getBank().getId());
			
			if (bankLetterEnum==null) {
				throw new BankLetterNotFoundException(sale.getBank().getName());
			}
			
			Map<String, Object> parameters = new HashMap<String, Object>();

			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
					.getExternalContext().getContext();
			String separator = System.getProperty("file.separator");
			String rootPath = servletContext.getRealPath(separator);
			String rootDir = rootPath + "resources" + separator + "templates" + separator;
			
			String fileName = "";
			if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
				fileName = rootDir + "letterFalabella.jrxml";
				parameters.put("names",sale.getPayer().getFirstnameResponsible() + " "
						+ sale.getPayer().getLastnamePaternalResponsible() + " "
						+ sale.getPayer().getLastnameMaternalResponsible());
				
			}else if(bankLetterEnum.equals(BankLetterEnum.GNB)) {
				fileName = rootDir + "letterGNB.jrxml";
				parameters.put("names",sale.getPayer().getFirstnameResponsible() + " "
						+ sale.getPayer().getLastnamePaternalResponsible() + " "
						+ sale.getPayer().getLastnameMaternalResponsible());
				parameters.put("address",sale.getPayer().getAddress());
				parameters.put("department",sale.getPayer().getDepartment()+ " - " +sale.getPayer().getProvince());
			}

			
			parameters.put("ROOT_DIR", rootDir);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "pe"));
			//System.out.println(rootDir);

			JasperReport report = JasperCompileManager.compileReport(fileName);
			JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

			FacesContext facesContext = FacesContext.getCurrentInstance();
			ExternalContext externalContext = facesContext.getExternalContext();
			externalContext.setResponseContentType("application/pdf");
			externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"Carta.pdf\"");

			JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
			facesContext.responseComplete();
			
			Notification notification = sale.getNotification();
			if (notification.getType().equals(NotificationTypeEnum.MAIL)
					&& notification.getState().equals(NotificationStateEnum.SENDING)) {
				notification.setState(NotificationStateEnum.DELIVERED);
				notification.setAnsweringAt(new Date());
				notificationService.update(notification);
			}
			


		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
