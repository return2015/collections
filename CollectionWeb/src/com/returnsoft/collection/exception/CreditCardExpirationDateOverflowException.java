package com.returnsoft.collection.exception;

public class CreditCardExpirationDateOverflowException extends Exception {

	public CreditCardExpirationDateOverflowException(int length) {
		super("La fecha de expiraci�n de la tarjeta debe tener "+length+" d�gitos.");

	}

}
