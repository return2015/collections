package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLastnameMaternalNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1521683863187719402L;
	
	public NotificationLastnameMaternalNullException() {
		super("El apellido materno esta vacío.");
	}

}
