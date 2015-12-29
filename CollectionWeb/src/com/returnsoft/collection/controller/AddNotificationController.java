package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class AddNotificationController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Notification notificationSelected;
	
	private String notificationStateSelected;
	
	private NotificationStateEnum notificationState = NotificationStateEnum.SENDING;
	
	private NotificationTypeEnum notificationType = NotificationTypeEnum.PHYSICAL;
	
	@EJB
	private NotificationService notificationService;
	
	@EJB
	private SaleService saleService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;
	
	public String initialize() {
		try {

			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = saleService.findById(Long.parseLong(saleId));
			
			notificationSelected = new Notification();
			notificationSelected.setSale(saleSelected);
			
			return null;

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}
	}
	
	public void add() {
		try {
			
			notificationSelected.setType(notificationType);
			notificationSelected.setState(notificationState);
			
			
			/*SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");*/
			User user = sessionBean.getUser();
			
			notificationSelected.setCreatedBy(user);
			notificationSelected.setCreatedAt(new Date());
			notificationService.add(notificationSelected);
			
			// RETORNA LA VENTA ACTUALIZADA
			
			Sale saleUpdated = saleService.findById(notificationSelected.getSale().getId());
			RequestContext.getCurrentInstance().closeDialog(saleUpdated);

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			
		}

	}

	public Notification getNotificationSelected() {
		return notificationSelected;
	}

	public void setNotificationSelected(Notification notificationSelected) {
		this.notificationSelected = notificationSelected;
	}

	public String getNotificationStateSelected() {
		return notificationStateSelected;
	}

	public void setNotificationStateSelected(String notificationStateSelected) {
		this.notificationStateSelected = notificationStateSelected;
	}

	public NotificationStateEnum getNotificationState() {
		return notificationState;
	}

	public NotificationTypeEnum getNotificationType() {
		return notificationType;
	}

	
	

}
