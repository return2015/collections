package com.returnsoft.collection.exception;

public class SaleAuditUserOverflowException extends Exception{

	public SaleAuditUserOverflowException(int length) {
		super("El usuario de auditoría debe tener "+length+" dígitos.");
	}

}
