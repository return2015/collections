package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationPayerNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6438460715923837537L;
	
	public NotificationPayerNullException() {
		super("El responsable de pago esta vacío.");
	}

}
