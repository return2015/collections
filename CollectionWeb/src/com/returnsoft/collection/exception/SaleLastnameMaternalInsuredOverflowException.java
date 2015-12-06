package com.returnsoft.collection.exception;

public class SaleLastnameMaternalInsuredOverflowException extends Exception{

	public SaleLastnameMaternalInsuredOverflowException(int length) {
		super("El apellido materno del asegurado debe tener "+length+" dígitos.");
	}

}
