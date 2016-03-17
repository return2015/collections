package com.returnsoft.collection.exception;

public class NotificationAlreadyExistException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 899339523931802885L;
	
	public NotificationAlreadyExistException(Long nuic, String orderNumber) {
		super("La notificaci�n con nuic "+nuic+" y n�mero de orden "+orderNumber+" ya existe");
	}
	

}
