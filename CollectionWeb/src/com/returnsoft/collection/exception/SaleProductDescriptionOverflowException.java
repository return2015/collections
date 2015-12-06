package com.returnsoft.collection.exception;

public class SaleProductDescriptionOverflowException extends Exception{

	public SaleProductDescriptionOverflowException(int length) {
		super("La descripci�n del producto debe tener "+length+" d�gitos.");
	}

}
