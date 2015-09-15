package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserInactiveException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8091237813429530848L;
	
	public UserInactiveException() {
		super("Es usuario se encuentra inactivo");
	}


}
