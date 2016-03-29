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
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.NotificationService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class EditNotificationController implements Serializable{

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
	
	@Inject
	private SessionBean sessionBean;
	
	@Inject
	private FacesUtil facesUtil;
	
	
	//@PostConstruct
	public String initialize() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			//System.out.println("Ingreso a initialize");
			
			String notificationId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("notificationId");
			
			notificationSelected = notificationService.findById(Integer.parseInt(notificationId));
			
			notificationStates = new ArrayList<SelectItem>();
			for (NotificationStateEnum notificationStateEnum : NotificationStateEnum.values()) {
				SelectItem item = new SelectItem();
				item.setValue(notificationStateEnum.getId());
				item.setLabel(notificationStateEnum.getName());
				notificationStates.add(item);
			}
			
			if (notificationSelected.getState()!=null) {
				notificationStateSelected = notificationSelected.getState().getId().toString();
			}
			
			return null;
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}
	
	public void edit() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			if (notificationStateSelected != null && notificationStateSelected.length() > 0) {
				notificationSelected.setState(NotificationStateEnum.findById(Short.parseShort(notificationStateSelected)));
			}

			notificationSelected.setUpdatedAt(new Date());
			
			/*SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");*/
			User user = sessionBean.getUser();
			
			notificationSelected.setUpdatedBy(user);
			notificationSelected = notificationService.update(notificationSelected);
			
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

	public List<SelectItem> getNotificationStates() {
		return notificationStates;
	}

	public void setNotificationStates(List<SelectItem> notificationStates) {
		this.notificationStates = notificationStates;
	}

	

}
