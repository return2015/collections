package com.returnsoft.collection.exception;

public class PayerAddressOverflowException extends Exception{

	public PayerAddressOverflowException(int length) {
		super("La direcci�n debe tener "+length+" d�gitos.");
	}

}
