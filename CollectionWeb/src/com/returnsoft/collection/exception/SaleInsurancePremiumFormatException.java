package com.returnsoft.collection.exception;

public class SaleInsurancePremiumFormatException extends Exception {

	public SaleInsurancePremiumFormatException() {
		super("La prima tiene formato inv�lido.");
	}
	
	public SaleInsurancePremiumFormatException(int decimals) {
		super("La prima debe tener menos de "+decimals+" decimales.");
	}

}
