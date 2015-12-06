package com.returnsoft.collection.exception;

public class SalePolicyNumberOverflowException extends Exception{

	public SalePolicyNumberOverflowException(int length) {
		super("El n�mero de poliza debe tener "+length+" d�gitos.");
	}

}
