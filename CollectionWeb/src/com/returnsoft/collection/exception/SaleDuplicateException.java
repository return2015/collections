package com.returnsoft.collection.exception;



public class SaleDuplicateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8068159312190645954L;

	/*public SaleDuplicateException(String nuicInsured, String dateOfSale, String product, String bank, String collectionPeriod) {
		super("La venta con NUIC de asegurado " + nuicInsured + ", fecha de venta "+dateOfSale+", producto "+product+", banco "+bank+" y periodo de cobranza "+collectionPeriod+" está duplicada");
	}*/
	
	public SaleDuplicateException() {
	super("La venta está duplicada.");
}

}
