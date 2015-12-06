package com.returnsoft.collection.exception;

public class SaleVendorCodeOverflowException extends Exception{

	public SaleVendorCodeOverflowException(int length) {
		super("El código de vendedor debe tener "+length+" dígitos.");
	}

}
