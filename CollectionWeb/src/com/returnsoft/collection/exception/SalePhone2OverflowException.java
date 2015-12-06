package com.returnsoft.collection.exception;

public class SalePhone2OverflowException extends Exception{

	public SalePhone2OverflowException(int length) {
		super("El telefono 2 debe tener "+length+" dígitos.");
	}

}
