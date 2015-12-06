package com.returnsoft.collection.exception;

public class SaleFirstnameInsuredOverflowException extends Exception{

	public SaleFirstnameInsuredOverflowException(int length) {
		super("El nombre del asegurado debe tener "+length+" dígitos.");
	}

}
