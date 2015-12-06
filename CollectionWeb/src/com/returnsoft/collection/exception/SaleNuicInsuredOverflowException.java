package com.returnsoft.collection.exception;

public class SaleNuicInsuredOverflowException extends Exception{

	public SaleNuicInsuredOverflowException(int length) {
		super("El Nuic de asegurado debe tener "+length+" dígitos.");
	}

}
