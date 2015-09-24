package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLastnamePaternalNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4982385821352074606L;
	
	public NotificationLastnamePaternalNullException() {
		super("El apellido paterno esta vacío.");
	}


}
