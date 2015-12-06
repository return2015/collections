package com.returnsoft.collection.exception;

public class PayerLastnameMaternalNullException extends Exception{

	public PayerLastnameMaternalNullException() {
		super("El apellido materno del responsable de pago está vacío.");
	}

}
