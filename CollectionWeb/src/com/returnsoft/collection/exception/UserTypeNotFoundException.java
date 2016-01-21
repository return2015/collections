package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserTypeNotFoundException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1912783374641106987L;
	
	public UserTypeNotFoundException() {
		super("Tipo de usuario no encontrado.");
	}

	

	

}
