package com.returnsoft.collection.exception;

public class SaleVendorNameOverflowException extends Exception{

	public SaleVendorNameOverflowException(int length) {
		super("El nombre de vendedor debe tener "+length+" dígitos.");
	}

}
