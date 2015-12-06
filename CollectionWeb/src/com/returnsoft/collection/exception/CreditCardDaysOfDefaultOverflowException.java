package com.returnsoft.collection.exception;

public class CreditCardDaysOfDefaultOverflowException extends Exception{

	public CreditCardDaysOfDefaultOverflowException(int length) {
		super("Los d�as de mora deben tener "+length+" d�gitos.");
	}

}
