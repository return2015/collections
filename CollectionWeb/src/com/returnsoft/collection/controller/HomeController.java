package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.util.FacesUtil;



@Named
@RequestScoped
public class HomeController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2342401227309760416L;

	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;
	
	
	
	public HomeController() {

		System.out.println("Construyendo HomeController");
	}
	
	public String initialize(){
		
		System.out.println("Inicializando HomeController");
		try {
			if (sessionBean == null && sessionBean.getUser() == null && sessionBean.getUser().getId() < 0) {
				throw new UserLoggedNotFoundException();
			}
			return null;

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			return null;
		}
		
	}


}
