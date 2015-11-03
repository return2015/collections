package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;

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

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
		}
	}
	
	public void add() {
		try {
			
			notificationSelected.setType(notificationType);
			notificationSelected.setState(notificationState);
			
			notificationSelected.setCreatedAt(new Date());
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");
			int userId = sessionBean.getUser().getId();
			User user = new User();
			user.setId(userId);
			notificationSelected.setCreatedBy(user);
			
			notificationService.add(notificationSelected);
			
			// RETORNA LA VENTA ACTUALIZADA
			
			Sale saleUpdated = saleService.findById(notificationSelected.getSale().getId());
			RequestContext.getCurrentInstance().closeDialog(saleUpdated);

		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
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
