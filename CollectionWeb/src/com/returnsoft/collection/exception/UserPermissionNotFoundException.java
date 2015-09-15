package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserPermissionNotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3009554571398359286L;
	
	public UserPermissionNotFoundException(){
		super("No tiene permisos para acceder a esta página");
	}

}
