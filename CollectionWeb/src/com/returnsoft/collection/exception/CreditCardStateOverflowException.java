package com.returnsoft.collection.exception;

public class CreditCardStateOverflowException extends Exception{

	public CreditCardStateOverflowException(int length) {
		super("El estado de tarjeta de crédito debe tener "+length+" dígitos.");
	}

}
