package com.returnsoft.collection.exception;

public class SalePhone1OverflowException extends Exception{

	public SalePhone1OverflowException(int length) {
		super("El telefono 1 debe tener "+length+" dígitos.");
	}

}
