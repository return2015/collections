package com.returnsoft.collection.exception;

public class SaleProductOverflowException extends Exception{

	public SaleProductOverflowException(int length) {
		super("El producto debe tener "+length+" dígitos.");
	}

}
