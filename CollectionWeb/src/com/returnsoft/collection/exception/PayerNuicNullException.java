package com.returnsoft.collection.exception;

public class PayerNuicNullException extends Exception {

	public PayerNuicNullException() {
		super("El NUIC de responsable de pago está vacío");
	}

}
