package com.returnsoft.collection.exception;

public class SaleDownChannelOverflowException extends Exception{

	public SaleDownChannelOverflowException(int length) {
		super("El canal de baja debe tener "+length+" dígitos.");
	}

}
