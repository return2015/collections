package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;

@ManagedBean
@ViewScoped
public class EditNotificationBySaleController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Notification notificationSelected;
	
	private String notificationStateSelected;
	private List<SelectItem> notificationStates;
	
	@EJB
	private NotificationService notificationService;
	
	@EJB
	private SaleService saleService;
	
	private List<Notification> notifications;
	
	
	@PostConstruct
	public void initialize() {
		try {

			System.out.println("Ingreso a initialize");
			
			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");

			notifications = saleService.findNotifications(Long.parseLong(saleId));
			
			notificationStates = new ArrayList<SelectItem>();
			
			for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
				
				SelectItem item = new SelectItem();
				item.setValue(notificationStateEnum.getId());
				item.setLabel(notificationStateEnum.getName());
				notificationStates.add(item);
			}
			
			
			//updateNotificationStates();


		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}
	
	public void beforeEditNotification(){
		System.out.println("beforeEditNotification...");
		if (notificationSelected!=null) {
			if (notificationSelected.getState()!=null) {
				notificationStateSelected = notificationSelected.getState().getId().toString();
				
			}
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('editNotificationDialog').show();");
			
		}else{
			FacesMessage msg = new FacesMessage("Seleccione notificación");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null,msg);
		}
		
	}
	


	

	public void edit() {
		try {
			
			if (notificationStateSelected != null && notificationStateSelected.length() > 0) {
				notificationSelected.setState(NotificationStateEnum.findById(Short.parseShort(notificationStateSelected)));
			}
			
			//notificationSelected.setType(NotificationTypeEnum.PHYSICAL);
			
			
			notificationSelected.setUpdatedAt(new Date());
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");
			
			int userId = sessionBean.getUser().getId();
			
			User user = new User();
			user.setId(userId);
			notificationSelected.setUpdatedBy(user);
			
			notificationSelected = notificationService.update(notificationSelected);
			
			notifications = saleService.findNotifications(notificationSelected.getSale().getId());
			
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('editNotificationDialog').hide();");
			
			
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

	public List<SelectItem> getNotificationStates() {
		return notificationStates;
	}

	public void setNotificationStates(List<SelectItem> notificationStates) {
		this.notificationStates = notificationStates;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}



	
	
	

}
