package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.SessionTypeInvalidException;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@RequestScoped
public class LoginController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	
	@EJB
	private UserService userService;
	
	private FacesUtil facesUtil;

	public LoginController() {
		
		facesUtil = new FacesUtil();

	}

	public String doLogin() {

		try {
			System.out.println("ingreso a login");
			
			// BUSCA USUARIO Y CLAVE
			User user = userService.loginUser(username, password);
			
			SessionBean sessionBean = null;
				
			/*FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("name", user.getFirstname() + " "
					+ user.getLastname());
			FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("username", username);
			FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("userId", user.getId());*/
			
			switch (user.getUserType()) {
			case ADMIN:
				sessionBean = new SessionBean();
				sessionBean.setUser(user);
				sessionBean.setIsAdmin(true);
				/*FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("isAdmin", true);*/
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("sessionBean", sessionBean);
				return "home?faces-redirect=true";
			case AGENT:
				sessionBean = new SessionBean();
				sessionBean.setUser(user);
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("sessionBean", sessionBean);
				
				return "select_bank?faces-redirect=true";
				
			default:
				throw new SessionTypeInvalidException();
			}
			

		

		}  catch (Exception e) {
			
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
			return null;
		}

	}

	public String doLogout() {
		try {
			
			//System.out.println("ingresoa logout");
			FacesContext.getCurrentInstance().getExternalContext()
					.invalidateSession();

			return "login?faces-redirect=true";
		}  catch (Exception e) {
			//System.out.println(e.getMessage());
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),e.getMessage());
			return null;
		}

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
