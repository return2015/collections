package com.returnsoft.collection.exception;

public class SaleAccountNumberOverflowException extends Exception{

	public SaleAccountNumberOverflowException(int length) {
		super("El número de cuenta debe tener "+length+" dígitos.");
	}

}
