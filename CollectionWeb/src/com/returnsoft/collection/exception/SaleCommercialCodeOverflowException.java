package com.returnsoft.collection.exception;

public class SaleCommercialCodeOverflowException extends Exception{

	public SaleCommercialCodeOverflowException(int length) {
		super("El c�digo de comercio debe tener "+length+" d�gitos.");
	}

}
