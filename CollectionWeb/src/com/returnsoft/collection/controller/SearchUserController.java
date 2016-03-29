package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;


@ManagedBean
@ViewScoped
public class SearchUserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nameSearch;
	private List<User> users;
	private User userSelected;

	@EJB
	private UserService userService;
	
	@Inject 
	private SessionBean sessionBean;
	
	@Inject
	private FacesUtil facesUtil;
	
	

	public SearchUserController() {
		//System.out.println("SearchUserController");
	}
	
	public String initialize(){
		try {
			
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

	public void search() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

			users = userService.find(nameSearch);

			// userSelected = null;

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void edit() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}
			

			/*return "edit_user?faces-redirect=true&userId="
					+ userSelected.getId();*/
			Map<String, List<String>> paramMap = new HashMap<String, List<String>>();
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(String.valueOf(userSelected.getId()));
			paramMap.put("userId", paramList);
			RequestContext.getCurrentInstance().openDialog("edit_user", null, paramMap);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			//return null;
		}
	}
	
	public void afterEdit(SelectEvent event){
		try {
			User userReturn = (User) event.getObject();
			
			System.out.println("after.."+userReturn.getUsername());
			System.out.println("after.."+userReturn.getFirstname());
			System.out.println("after.."+userReturn.getLastname());
			
			int i=0;
			for (User user : users) {
				//Sale sale = sales.get(i);
				if (user.getId().equals(userReturn.getId())) {
					users.set(i, userReturn);
					userSelected = userReturn;
					break;
				}
				i++;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			//return null;
		}
		
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
