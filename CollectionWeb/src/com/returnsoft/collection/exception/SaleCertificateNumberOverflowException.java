package com.returnsoft.collection.exception;

public class SaleCertificateNumberOverflowException extends Exception{

	public SaleCertificateNumberOverflowException(int length) {
		super("El número de certificado debe tener "+length+" dígitos.");
	}

}
