package com.returnsoft.collection.exception;

public class CreditCardDaysOfDefaultFormatException extends Exception{

	public CreditCardDaysOfDefaultFormatException() {
		super("Los días de mora tiene formato inválido");
	}

}
