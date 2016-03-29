package com.returnsoft.collection.enumeration;

public enum MoneyTypeEnum {
	
	SOLES((short)1,"SOLES"),
	DOLARES((short)2,"DOLARES");
	
	
	private Short id;
	private String name;
	
	private MoneyTypeEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static MoneyTypeEnum findById(Short id){
		for(MoneyTypeEnum loteTypeEnum: MoneyTypeEnum.values()){
			if (loteTypeEnum.getId()==id) {
				return loteTypeEnum;
			}
		}
		return null;
	}
	
	public static MoneyTypeEnum findByName(String name){
		for(MoneyTypeEnum moneyTypeEnum: MoneyTypeEnum.values()){
			
			if (moneyTypeEnum.getName().equals(name)) {
				
				return moneyTypeEnum;
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
