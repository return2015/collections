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

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.NotificationService;

@ManagedBean
@ViewScoped
public class AddNotificationController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Notification notificationSelected;
	
	private String notificationStateSelected;
	
	//private List<SelectItem> notificationStates;
	
	private NotificationStateEnum notificationState = NotificationStateEnum.SENDING;
	
	private NotificationTypeEnum notificationType = NotificationTypeEnum.PHYSICAL;
	
	@EJB
	private NotificationService notificationService;
	
	public String initialize() {
		try {

			System.out.println("Ingreso a initialize");
			
			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = notificationService.findSaleById(Long.parseLong(saleId));
			
			System.out.println("saleSelected");
			
			System.out.println("getId"+saleSelected.getId());
			System.out.println("getCode"+saleSelected.getCode());
			
			/*System.out.println("getFirstnameContractor"+saleSelected.getFirstnameResponsible());
			System.out.println("getLastnamePaternalContractor"+saleSelected.getLastnamePaternalResponsible());
			System.out.println("getLastnameMaternalContractor"+saleSelected.getLastnameMaternalResponsible());*/
			
			notificationSelected = new Notification();
			notificationSelected.setSale(saleSelected);
			
			/*maintenanceSelected.setDownUser(saleSelected.getDownUser());
			maintenanceSelected.setDownChannel(saleSelected.getDownChannel());
			maintenanceSelected.setDownObservation(saleSelected.getDownObservation());
			maintenanceSelected.setDownReason(saleSelected.getDownReason());
			maintenanceSelected.setStateDate(saleSelected.getStateDate());
			maintenanceSelected.setCode(saleSelected.getCode());*/
			
			//if (saleSelected.getSaleState()!=null && saleSelected.getSaleState().length()>0) {
				//saleStateSelected = saleSelected.getSaleState();
				//System.out.println(saleStateSelected);
				//if (saleSelected.getSaleState().equals(saleStates[1])) {
					//System.out.println("INGRESO!!");
					// CHECK PASSWORD SUPERVISOR
					 /*Map<String,Object> options = new HashMap<String, Object>();
				        options.put("modal", true);
				        options.put("draggable", false);
				        options.put("resizable", false);
				        options.put("contentHeight", 220);
				        options.put("contentWidth", 280);*/
					/*Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
					ArrayList<String> paramList = new ArrayList<>();
					paramList.add(String.valueOf(saleSelected.getId()));
					paramMap.put("saleId", paramList);*/
					/*RequestContext.getCurrentInstance().openDialog(
							"validate_password_supervisor", null, null);
					System.out.println("INGRESO!!");*/
				//}
			//}
			
			//updateNotificationStates();

			/*List<SaleState> saleStatesEntity = maintenanceService.getSaleStates();
			saleStates = new ArrayList<SelectItem>();
			for (SaleState saleState : saleStatesEntity) {
				SelectItem item = new SelectItem();
				item.setLabel(saleState.getName());
				item.setValue(saleState.getId());
				saleStates.add(item);
			}
			*/
			
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
	
	/*public void updateNotificationStates(){
		
		notificationStates = new ArrayList<SelectItem>();
		for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
			SelectItem item = new SelectItem();
			item.setValue(notificationStateEnum.getId());
			item.setLabel(notificationStateEnum.getName());
			notificationStates.add(item);
		}
		
	}*/
	

	public void add() {
		try {
			
			/*if (notificationStateSelected != null && notificationStateSelected.length() > 0) {
				notificationSelected.setState(NotificationStateEnum.findById(Short.parseShort(notificationStateSelected)));
			}*/
			
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
			RequestContext.getCurrentInstance().closeDialog(notificationSelected.getSale());

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

	



	/*public Maintenance getMaintenanceSelected() {
		return maintenanceSelected;
	}



	public void setMaintenanceSelected(Maintenance maintenanceSelected) {
		this.maintenanceSelected = maintenanceSelected;
	}*/



	/*public String[] getSaleStates() {
		return saleStates;
	}



	public String getSaleStateSelected() {
		return saleStateSelected;
	}*/



	/*public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}*/
	
	
	

}
