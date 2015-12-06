package com.returnsoft.collection.exception;

public class CreditCardNumberFormatException extends Exception {

	public CreditCardNumberFormatException() {
		super("El número de tarjeta tiene formato inválido.");
	}

}
