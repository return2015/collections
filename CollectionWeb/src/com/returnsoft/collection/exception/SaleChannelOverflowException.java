package com.returnsoft.collection.exception;

public class SaleChannelOverflowException extends Exception{

	public SaleChannelOverflowException(int length) {
		super("El canal debe tener "+length+" dígitos.");
	}

}
