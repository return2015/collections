package com.returnsoft.collection.exception;

public class PayerLastnameMaternalOverflowException extends Exception{

	public PayerLastnameMaternalOverflowException(int length) {
		super("El apellido materno de responsable de pago debe tener "+length+" dígitos.");
	}
	
	

}
