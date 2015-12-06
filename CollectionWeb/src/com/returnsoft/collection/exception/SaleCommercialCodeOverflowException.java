package com.returnsoft.collection.exception;

public class SaleCommercialCodeOverflowException extends Exception{

	public SaleCommercialCodeOverflowException(int length) {
		super("El código de comercio debe tener "+length+" dígitos.");
	}

}
