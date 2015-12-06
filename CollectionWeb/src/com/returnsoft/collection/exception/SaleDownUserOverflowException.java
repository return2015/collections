package com.returnsoft.collection.exception;

public class SaleDownUserOverflowException extends Exception {

	public SaleDownUserOverflowException(int length) {
		super("El usuario de baja debe tener "+length+" dígitos.");

	}

}
