package com.returnsoft.collection.exception;

public class SaleCertificateNumberOverflowException extends Exception{

	public SaleCertificateNumberOverflowException(int length) {
		super("El n�mero de certificado debe tener "+length+" d�gitos.");
	}

}
