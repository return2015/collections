package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.UserService;


@ManagedBean
@ViewScoped
public class SearchUserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String documentNumberSearch;
	private String nameSearch;
	private List<User> users;
	private User userSelected;

	@EJB
	private UserService userService;

	public SearchUserController() {
		System.out.println("SearchUserController");
	}

	public void search() {
		try {

			users = userService.find(documentNumberSearch, nameSearch);

			// userSelected = null;

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
			

			return "edit_user?faces-redirect=true&userId="
					+ userSelected.getId();

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

	public String getDocumentNumberSearch() {
		return documentNumberSearch;
	}

	public void setDocumentNumberSearch(String documentNumberSearch) {
		this.documentNumberSearch = documentNumberSearch;
	}

	public String getNameSearch() {
		return nameSearch;
	}

	public void setNameSearch(String nameSearch) {
		this.nameSearch = nameSearch;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getUserSelected() {
		return userSelected;
	}

	public void setUserSelected(User userSelected) {
		this.userSelected = userSelected;
	}

}
