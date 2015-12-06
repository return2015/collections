package com.returnsoft.collection.exception;

public class CreditCardDateOverflowException extends Exception {

	public CreditCardDateOverflowException(int length) {
		super("la fecha de actualizaci�n de tarjeta debe tener "+length+" d�gitos.");
	}

}
