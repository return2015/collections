package com.returnsoft.collection.exception;

public class SaleProductDescriptionOverflowException extends Exception{

	public SaleProductDescriptionOverflowException(int length) {
		super("La descripción del producto debe tener "+length+" dígitos.");
	}

}
