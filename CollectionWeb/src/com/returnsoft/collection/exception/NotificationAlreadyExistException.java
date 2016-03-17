package com.returnsoft.collection.exception;

public class NotificationAlreadyExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 899339523931802885L;
	
	public NotificationAlreadyExistException(Long nuic, String orderNumber) {
		super("La notificación con nuic "+nuic+" y número de orden "+orderNumber+" ya existe");
	}
	

}
