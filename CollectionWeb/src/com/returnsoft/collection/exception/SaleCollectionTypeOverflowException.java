package com.returnsoft.collection.exception;

public class SaleCollectionTypeOverflowException extends Exception{

	public SaleCollectionTypeOverflowException(int length) {
		super("El tipo  de cobro debe tener "+length+" dígitos.");
	}

}
