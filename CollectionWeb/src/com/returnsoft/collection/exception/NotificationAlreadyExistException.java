package com.returnsoft.collection.exception;

public class NotificationAlreadyExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 899339523931802885L;
	
	public NotificationAlreadyExistException() {
		super("La notificación ya existe");
	}
	

}
