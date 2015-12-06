package com.returnsoft.collection.exception;

public class SaleAuditDateOverflowException extends Exception{

	public SaleAuditDateOverflowException(int length) {
		super("La fecha de auditor�a debe tener "+length+" d�gitos.");
	}

}
