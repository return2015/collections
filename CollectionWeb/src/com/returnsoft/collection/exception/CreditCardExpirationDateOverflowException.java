package com.returnsoft.collection.exception;

public class CreditCardExpirationDateOverflowException extends Exception {

	public CreditCardExpirationDateOverflowException(int length) {
		super("La fecha de expiración de la tarjeta debe tener "+length+" dígitos.");

	}

}
