package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class SearchNotificationController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6040059196229923398L;
	
	private Date sendingDate;
	private Date createdDate;
	
	private List<SelectItem> notificationTypes;
	private String notificationTypeSelected;
	private List<SelectItem> notificationStates;
	private String notificationStateSelected;
	
	private Notification notificationSelected;
	
	private List<Notification> notifications;
	
	private FacesUtil facesUtil;
	
	@EJB
	private NotificationService notificationService;
	
	public SearchNotificationController(){
		facesUtil = new FacesUtil();
	}
	
	
	
	public String initialize(){
		//System.out.println("ingreso a initialize");
		
		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				/*if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.SUPERVISOR)) {
					throw new UserPermissionNotFoundException();
				}*/
				
				
				/*if (sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)) {
					serverRendered=true;
					updateServers();
				}else{
					serverRendered=false;
				}*/
					
				updateNotificationTypes();
				updateNotificationStates();
					
				/*sessionBean.setCurrentPage("supervisorBoard");
				FacesContext.getCurrentInstance().getExternalContext()
						.getSessionMap().put("sessionBean", sessionBean);*/
				
				return null;
			}else{
				throw new UserLoggedNotFoundException();
			}
			
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";
		/*} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";*/		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return null;
		}
		
		
	}
	
	
	public void updateNotificationTypes(){
		
		notificationTypes = new ArrayList<SelectItem>();

		for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
				
			SelectItem item = new SelectItem();
			item.setValue(notificationTypeEnum.getId());
			item.setLabel(notificationTypeEnum.getName());
			notificationTypes.add(item);
		}
		
	}
	
	public void updateNotificationStates(){
		
		notificationStates = new ArrayList<SelectItem>();

		for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
				
			SelectItem item = new SelectItem();
			item.setValue(notificationStateEnum.getId());
			item.setLabel(notificationStateEnum.getName());
			notificationStates.add(item);
		}
		
	}
	
	
	
	
	public void search(){
		
		try {
			
		
		NotificationStateEnum notificationState = null;
		if (notificationStateSelected != null && notificationStateSelected.length() > 0) {
			Short notificationStateId = Short.parseShort(notificationStateSelected);
			notificationState = NotificationStateEnum.findById(notificationStateId);
		}
		
		NotificationTypeEnum notificationType = null;
		if (notificationTypeSelected != null && notificationTypeSelected.length() > 0) {
			Short notificationTypeId = Short.parseShort(notificationTypeSelected);
			notificationType = NotificationTypeEnum.findById(notificationTypeId);
		}
		
		notifications = notificationService.findNotificationsByData(sendingDate, createdDate, notificationType, notificationState);
		
		
		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		
	}
	
	public String edit() {
		try {
			

			return "edit_notification?faces-redirect=true&notificationId="
					+ notificationSelected.getId();

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

	public Date getSendingDate() {
		return sendingDate;
	}

	public void setSendingDate(Date sendingDate) {
		this.sendingDate = sendingDate;
	}

	

	public Date getCreatedDate() {
		return createdDate;
	}



	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}



	public List<SelectItem> getNotificationTypes() {
		return notificationTypes;
	}

	public void setNotificationTypes(List<SelectItem> notificationTypes) {
		this.notificationTypes = notificationTypes;
	}

	public String getNotificationTypeSelected() {
		return notificationTypeSelected;
	}

	public void setNotificationTypeSelected(String notificationTypeSelected) {
		this.notificationTypeSelected = notificationTypeSelected;
	}

	public List<SelectItem> getNotificationStates() {
		return notificationStates;
	}

	public void setNotificationStates(List<SelectItem> notificationStates) {
		this.notificationStates = notificationStates;
	}

	public String getNotificationStateSelected() {
		return notificationStateSelected;
	}

	public void setNotificationStateSelected(String notificationStateSelected) {
		this.notificationStateSelected = notificationStateSelected;
	}

	public Notification getNotificationSelected() {
		return notificationSelected;
	}

	public void setNotificationSelected(Notification notificationSelected) {
		this.notificationSelected = notificationSelected;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
	
	
	
	

}
