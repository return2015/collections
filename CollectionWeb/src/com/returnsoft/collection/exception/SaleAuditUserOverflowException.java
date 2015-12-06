package com.returnsoft.collection.exception;

public class SaleAuditUserOverflowException extends Exception{

	public SaleAuditUserOverflowException(int length) {
		super("El usuario de auditor�a debe tener "+length+" d�gitos.");
	}

}
