package com.returnsoft.collection.exception;

public class PayerAddressOverflowException extends Exception{

	public PayerAddressOverflowException(int length) {
		super("La dirección debe tener "+length+" dígitos.");
	}

}
