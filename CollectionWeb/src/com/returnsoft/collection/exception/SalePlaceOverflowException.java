package com.returnsoft.collection.exception;

public class SalePlaceOverflowException extends Exception {

	public SalePlaceOverflowException(int length) {
		super("El lugar de venta debe tener "+length+" dígitos.");
	}

}
