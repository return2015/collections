package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.BankLetterEnum;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.exception.PayerDataNullException;
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

@ManagedBean
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

	private FacesUtil facesUtil;

	public void start() {

		try {

			//System.out.println("Ingreso a start: " + code);
			facesUtil = new FacesUtil();
			List<Exception> errors = new ArrayList<Exception>();

			if (code != null && code.length() > 0) {
				Sale sale = saleService.findByCode(code);
				if (sale != null && sale.getId() != null) {

					Short bankId = sale.getCommerce().getBank().getId();
					String bankName = sale.getCommerce().getBank().getName();
					BankLetterEnum bankLetterEnum = BankLetterEnum.findById(bankId);

					if (bankLetterEnum == null) {
						errors.add(new BankLetterNotFoundException(sale.getCode(), bankName));
					}
					if (sale.getSaleState().getState().equals(SaleStateEnum.DOWN)) {
						errors.add(new SaleStateNoActiveException(sale.getCode()));
					}
					if (sale.getPayer().getFirstnameResponsible() == null
							|| sale.getPayer().getFirstnameResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El nombre", sale.getCode()));
					}
					if (sale.getPayer().getLastnamePaternalResponsible() == null
							|| sale.getPayer().getLastnamePaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido paterno", sale.getCode()));
					}
					if (sale.getPayer().getLastnameMaternalResponsible() == null
							|| sale.getPayer().getLastnameMaternalResponsible().trim().length() == 0) {
						errors.add(new PayerDataNullException("El apellido materno", sale.getCode()));
					}
					if (sale.getPayer().getAddress() == null || sale.getPayer().getAddress().trim().length() == 0) {
						errors.add(new PayerDataNullException("La dirección", sale.getCode()));
					}
					if (sale.getPayer().getProvince() == null || sale.getPayer().getProvince().trim().length() == 0) {
						errors.add(new PayerDataNullException("La provincia", sale.getCode()));//
					}
					if (sale.getPayer().getDepartment() == null
							|| sale.getPayer().getDepartment().trim().length() == 0) {
						errors.add(new PayerDataNullException("El departamento", sale.getCode()));//
					}
					if (sale.getPayer().getDistrict() == null || sale.getPayer().getDistrict().trim().length() == 0) {
						errors.add(new PayerDataNullException("El distrito", sale.getCode()));//
					}

					// VALIDA SI LA VENTA NO ESTA ACTIVA
					if (errors.size() > 0) {
						for (Exception e : errors) {
							facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
						}
					} else {

						Map<String, Object> parameters = new HashMap<String, Object>();

						ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
								.getExternalContext().getContext();
						String separator = System.getProperty("file.separator");
						String rootPath = servletContext.getRealPath(separator);
						String fileName = rootPath + "resources" + separator + "templates" + separator + "letters_2.jrxml";
						String rootDir = rootPath + "resources" + separator + "templates" + separator;

						parameters.put("names",sale.getPayer().getFirstnameResponsible() + " "
										+ sale.getPayer().getLastnamePaternalResponsible() + " "
										+ sale.getPayer().getLastnameMaternalResponsible());
						parameters.put("ROOT_DIR", rootDir);
						parameters.put(JRParameter.REPORT_LOCALE, new Locale("es", "pe"));
						System.out.println(rootDir);

						JasperReport report = JasperCompileManager.compileReport(fileName);
						JasperPrint print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

						FacesContext facesContext = FacesContext.getCurrentInstance();
						ExternalContext externalContext = facesContext.getExternalContext();
						externalContext.setResponseContentType("application/pdf");
						externalContext.setResponseHeader("Content-Disposition", "attachment; filename=\"cartas.pdf\"");

						JasperExportManager.exportReportToPdfStream(print, externalContext.getResponseOutputStream());
						facesContext.responseComplete();
						
						Notification notification = sale.getNotification();
						if (notification.getType().equals(NotificationTypeEnum.MAIL)
								&& notification.getState().equals(NotificationStateEnum.SENDING)) {
							notification.setState(NotificationStateEnum.DELIVERED);
							notification.setAnsweringAt(new Date());
							notificationService.update(notification);
						}
						
						

					}

				}
			}
			// return null;

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
