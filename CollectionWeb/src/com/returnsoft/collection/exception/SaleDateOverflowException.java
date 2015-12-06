package com.returnsoft.collection.exception;

public class SaleDateOverflowException extends Exception {

	public SaleDateOverflowException(int length) {
		super("La fecha de venta debe tener "+length+" dígitos.");

	}

}
