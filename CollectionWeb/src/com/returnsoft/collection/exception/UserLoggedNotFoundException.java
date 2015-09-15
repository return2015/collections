package com.returnsoft.collection.exception;

import java.io.Serializable;

public class UserLoggedNotFoundException  extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5246221711189055417L;
	
	public UserLoggedNotFoundException(){
		super("No se encontró un usuario logueado");
	}

}
