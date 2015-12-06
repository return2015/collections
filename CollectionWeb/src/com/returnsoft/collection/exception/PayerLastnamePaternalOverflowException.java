package com.returnsoft.collection.exception;

public class PayerLastnamePaternalOverflowException extends Exception{

	public PayerLastnamePaternalOverflowException(int length) {
		super("El apellido paterno de responsable de pago debe tener "+length+" dígitos.");
	}
	
	

}
