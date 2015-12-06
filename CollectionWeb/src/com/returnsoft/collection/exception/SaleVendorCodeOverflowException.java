package com.returnsoft.collection.exception;

public class SaleVendorCodeOverflowException extends Exception{

	public SaleVendorCodeOverflowException(int length) {
		super("El c�digo de vendedor debe tener "+length+" d�gitos.");
	}

}
