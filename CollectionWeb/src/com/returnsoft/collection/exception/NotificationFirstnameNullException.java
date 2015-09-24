package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationFirstnameNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 565008243929697540L;

	
	public NotificationFirstnameNullException() {
		super("El nombre esta vacío.");
	}
	
}
