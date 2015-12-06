package com.returnsoft.collection.exception;

public class CreditCardDaysOfDefaultOverflowException extends Exception{

	public CreditCardDaysOfDefaultOverflowException(int length) {
		super("Los días de mora deben tener "+length+" dígitos.");
	}

}
