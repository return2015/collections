package com.returnsoft.collection.exception;

public class SaleBankOverflowException extends Exception{

	public SaleBankOverflowException(int length) {
		super("El banco debe tener "+length+" dígitos.");
	}

}
