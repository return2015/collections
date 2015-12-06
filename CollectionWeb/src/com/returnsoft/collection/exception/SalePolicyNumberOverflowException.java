package com.returnsoft.collection.exception;

public class SalePolicyNumberOverflowException extends Exception{

	public SalePolicyNumberOverflowException(int length) {
		super("El número de poliza debe tener "+length+" dígitos.");
	}

}
