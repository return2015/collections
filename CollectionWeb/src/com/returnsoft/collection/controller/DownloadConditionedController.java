package com.returnsoft.collection.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
<<<<<<< HEAD
import javax.enterprise.context.RequestScoped;
=======
import javax.faces.bean.RequestScoped;
>>>>>>> branch 'master' of https://github.com/return2015/collections.git
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
import com.returnsoft.collection.exception.BankLetterNotFoundException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@Named
@RequestScoped
public class DownloadConditionedController implements Serializable {

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

			if (sale.getBank()==null || sale.getBank().getId()==null) {
				throw new Exception();
			}
			
			BankLetterEnum bankLetterEnum = BankLetterEnum.findById(sale.getBank().getId());
			
			if (bankLetterEnum==null) {
				throw new BankLetterNotFoundException(sale.getBank().getName());
			}

						ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance()
								.getExternalContext().getContext();
						String separator = System.getProperty("file.separator");
						String rootPath = servletContext.getRealPath(separator);
						String fileName = "";
						if (bankLetterEnum.equals(BankLetterEnum.FALABELLA)) {
							fileName = rootPath + "resources" + separator + "templates" + separator
									+ "condicionado_asistencia_falabella.pdf";	
						}else if(bankLetterEnum.equals(BankLetterEnum.GNB)){
							fileName = rootPath + "resources" + separator + "templates" + separator
									+ "condicionado_asistencia_gnb.pdf";
						}
						
						
						File file = new File(fileName);
						InputStream pdfInputStream = new FileInputStream(file);

						FacesContext facesContext = FacesContext.getCurrentInstance();
						ExternalContext externalContext = facesContext.getExternalContext();
						externalContext.setResponseContentType("application/pdf");
						externalContext.setResponseHeader("Content-Disposition",
								"attachment; filename=\"condicionado.pdf\"");

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
