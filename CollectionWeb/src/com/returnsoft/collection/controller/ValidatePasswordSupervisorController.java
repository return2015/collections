package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

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
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;

	//@PostConstruct
	public String initialize() {
		try {

			//System.out.println("Ingreso a initialize");
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
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

	public void validate() {

		try {
			
			/*SessionBean sessionBean = (SessionBean) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("sessionBean");*/
			
			/*Integer userId = (Integer) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userId");*/
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
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

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			
		}

	}

	public String getPasswordSupervisor() {
		return passwordSupervisor;
	}

	public void setPasswordSupervisor(String passwordSupervisor) {
		this.passwordSupervisor = passwordSupervisor;
	}

}
