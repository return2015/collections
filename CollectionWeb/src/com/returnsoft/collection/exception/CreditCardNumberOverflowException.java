package com.returnsoft.collection.exception;

public class CreditCardNumberOverflowException extends Exception{

	public CreditCardNumberOverflowException(int length) {
		super("El n�mero de tarjeta de cr�dito debe tener "+length+" d�gitos.");
	}

}
