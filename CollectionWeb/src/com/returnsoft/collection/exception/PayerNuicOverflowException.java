package com.returnsoft.collection.exception;

public class PayerNuicOverflowException extends Exception{

	public PayerNuicOverflowException(int length) {
		super("El Nuic de responsable de pago debe tener "+length+" dígitos.");
	}
	
	

}
