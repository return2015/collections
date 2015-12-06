package com.returnsoft.collection.exception;

public class PayerDocumentTypeOverflowException extends Exception{

	public PayerDocumentTypeOverflowException(int length) {
		super("El tipo de documento debe tener "+length+" dígitos.");
	}

}
