package com.returnsoft.collection.exception;

public class CreditCardNumberOverflowException extends Exception{

	public CreditCardNumberOverflowException(int length) {
		super("El número de tarjeta de crédito debe tener "+length+" dígitos.");
	}

}
