package com.returnsoft.collection.exception;

public class CreditCardExpirationDateFormatException extends Exception{

	public CreditCardExpirationDateFormatException() {
		super("La fecha de expiración tiene formato inválido.");
	}

}
