package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLimitException extends Exception implements Serializable{
	
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8488114964182484306L;

	public NotificationLimitException() {
		super("No se puede agregar porque ya tiene 3 envíos físicos ó ya tiene 3 envíos virtuales y 1 físico");
	}

}
