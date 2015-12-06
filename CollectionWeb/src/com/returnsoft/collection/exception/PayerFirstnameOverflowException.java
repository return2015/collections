package com.returnsoft.collection.exception;

public class PayerFirstnameOverflowException extends Exception {

	public PayerFirstnameOverflowException(int length) {
		super("El nombre de responsable de pago debe tener "+length+" dígitos.");
	}

}
