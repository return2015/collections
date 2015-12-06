package com.returnsoft.collection.exception;

public class PayerProvinceOverflowException extends Exception{

	public PayerProvinceOverflowException(int length) {
		super("La provincia debe tener "+length+" dígitos.");
	}

}
