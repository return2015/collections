package com.returnsoft.collection.exception;

public class SaleDownObservationOverflowException extends Exception{

	public SaleDownObservationOverflowException(int length) {
		super("La observación de baja debe tener "+length+" dígitos.");
	}

}
