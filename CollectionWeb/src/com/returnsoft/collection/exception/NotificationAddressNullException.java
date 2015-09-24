package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationAddressNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1230752065971511469L;

	
	public NotificationAddressNullException() {
		super("La dirección esta vacía.");
	}
	
}
