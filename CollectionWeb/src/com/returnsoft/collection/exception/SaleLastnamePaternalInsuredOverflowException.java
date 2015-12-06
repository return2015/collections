package com.returnsoft.collection.exception;

public class SaleLastnamePaternalInsuredOverflowException extends Exception{

	public SaleLastnamePaternalInsuredOverflowException(int length) {
		super("El apellido paterno del asegurado debe tener "+length+" dígitos.");
	}

}
