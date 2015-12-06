package com.returnsoft.collection.exception;

public class SaleAuditDateOverflowException extends Exception{

	public SaleAuditDateOverflowException(int length) {
		super("La fecha de auditoría debe tener "+length+" dígitos.");
	}

}
