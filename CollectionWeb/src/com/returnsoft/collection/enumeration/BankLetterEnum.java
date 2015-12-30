package com.returnsoft.collection.enumeration;

public enum BankLetterEnum {

	FALABELLA((short) 5), 
	GNB((short) 3);

	private Short id;
	
	private BankLetterEnum(short id) {
		this.id = id;
	}

	public static BankLetterEnum findById(Short id) {
		for (BankLetterEnum bankLetterEnum : BankLetterEnum.values()) {
			if (bankLetterEnum.getId() == id) {
				return bankLetterEnum;
			}
		}
		return null;
	}

	public Short getId() {
		return id;
	}

	
}
