package com.returnsoft.collection.exception;

public class SaleStateDateOverflowException extends Exception{

	public SaleStateDateOverflowException(int length) {
		super("La fecha de estado de venta debe tener "+length+" dígitos.");
	}
	

}
