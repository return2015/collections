package com.returnsoft.collection.exception;

public class SaleAccountNumberOverflowException extends Exception{

	public SaleAccountNumberOverflowException(int length) {
		super("El n�mero de cuenta debe tener "+length+" d�gitos.");
	}

}
