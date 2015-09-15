package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserAlredyExistsException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -79500933188697686L;
	
	public UserAlredyExistsException(String username) {
		super("Es usuario "+username+" ya se encuentra registrado.");
	}

}
