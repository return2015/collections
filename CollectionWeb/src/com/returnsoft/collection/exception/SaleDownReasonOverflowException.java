package com.returnsoft.collection.exception;

public class SaleDownReasonOverflowException extends Exception{

	public SaleDownReasonOverflowException(int length) {
		super("El motivo de baja debe tener "+length+" dígitos.");

	}

}
