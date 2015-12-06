package com.returnsoft.collection.exception;

public class PayerDepartmentOverflowException extends Exception {

	public PayerDepartmentOverflowException(int length) {
		super("El departamento debe tener "+length+" dígitos.");
	}

}
