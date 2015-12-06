package com.returnsoft.collection.exception;

public class CreditCardStateOverflowException extends Exception{

	public CreditCardStateOverflowException(int length) {
		super("El estado de tarjeta de cr�dito debe tener "+length+" d�gitos.");
	}

}
