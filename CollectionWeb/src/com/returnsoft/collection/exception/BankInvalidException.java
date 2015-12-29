package com.returnsoft.collection.exception;

public class BankInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8861333525434924314L;

	public BankInvalidException() {
		super("El banco es inválido.");
	}
}
