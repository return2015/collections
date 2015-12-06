package com.returnsoft.collection.exception;

public class PayerMailOverflowException extends Exception{

	public PayerMailOverflowException(int length) {
		super("El correo debe tener "+length+" dígitos.");
	}

}
