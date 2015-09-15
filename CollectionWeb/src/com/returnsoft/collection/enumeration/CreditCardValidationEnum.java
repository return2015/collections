package com.returnsoft.collection.enumeration;

public enum CreditCardValidationEnum {
	
	
	UPDATE((short)1,"ACTUALIZADO"),
	SAME((short)2,"MISMA TARJETA"),
	NOTFOUND((short)3,"NO ENCONTRADO");
	
	private Short id;
	private String name;
	
	private CreditCardValidationEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static CreditCardValidationEnum findById(Short id){
		for(CreditCardValidationEnum creditCardValidationEnum: CreditCardValidationEnum.values()){
			if (creditCardValidationEnum.getId()==id) {
				return creditCardValidationEnum;
			}
		}
		return null;
	}
	
	public static CreditCardValidationEnum findByName(String name){
		for(CreditCardValidationEnum creditCardValidationEnum: CreditCardValidationEnum.values()){
			if (creditCardValidationEnum.getName().equals(name)) {
				return creditCardValidationEnum;
			}
		}
		return null;
	}

	public Short getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	

}
