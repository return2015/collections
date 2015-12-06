package com.returnsoft.collection.exception;

public class PayerDistrictOverflowException extends Exception{

	public PayerDistrictOverflowException(int length) {
		super("El distrito debe tener "+length+" dígitos.");
	}

}
