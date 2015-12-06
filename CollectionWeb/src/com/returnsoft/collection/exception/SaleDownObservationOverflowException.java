package com.returnsoft.collection.exception;

public class SaleDownObservationOverflowException extends Exception{

	public SaleDownObservationOverflowException(int length) {
		super("La observaci�n de baja debe tener "+length+" d�gitos.");
	}

}
