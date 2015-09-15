package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.UserService;

@ManagedBean
@ViewScoped
public class ValidatePasswordSupervisorController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String passwordSupervisor;
	//private String saleId;

	@EJB
	private UserService userService;

	@PostConstruct
	public void initialize() {
		try {

			System.out.println("Ingreso a initialize");


		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public void validate() {

		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("sessionBean");
			
			/*Integer userId = (Integer) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userId");*/
			Integer userId = sessionBean.getUser().getId();
			
			User user = userService.findById(userId);

			if (user.getPasswordSupervisor().equals(passwordSupervisor)) {
				// CORRECT
				RequestContext.getCurrentInstance().closeDialog("ValidatePasswordCorrect");
				 				
			} else {
				FacesMessage msg = new FacesMessage(
						"Password supervisor incorrecto");
				msg.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, msg);
			}

		} catch (Exception e) {
			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}

	}

	public String getPasswordSupervisor() {
		return passwordSupervisor;
	}

	public void setPasswordSupervisor(String passwordSupervisor) {
		this.passwordSupervisor = passwordSupervisor;
	}

}
