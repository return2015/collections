package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserWrongPasswordException extends Exception implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2177747288646406347L;

	public UserWrongPasswordException() {
		super("Password incorrecto");
	}

	

}
