package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserNotFoundException extends Exception implements Serializable {
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6793254603900438499L;

	public UserNotFoundException() {
		super("No se encontró el usuario");
	}


}
