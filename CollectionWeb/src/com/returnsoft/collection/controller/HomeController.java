package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.util.FacesUtil;



@ManagedBean
@RequestScoped
public class HomeController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2342401227309760416L;
	
	private FacesUtil facesUtil;
	
	public HomeController() {

		System.out.println("Construyendo HomeController");
		facesUtil = new FacesUtil();
	}
	
	public String initialize(){
		
		System.out.println("Inicializando HomeController");
		
		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}
					
					//sessionBean.setCurrentPage("home");
					/*FacesContext.getCurrentInstance().getExternalContext()
							.getSessionMap().put("sessionBean", sessionBean);*/
					
					return null;
				
			} else{
				throw new UserLoggedNotFoundException();
			}
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return null;
		}
		
	}


}
