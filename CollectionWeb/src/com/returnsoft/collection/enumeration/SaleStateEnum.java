package com.returnsoft.collection.enumeration;

public enum SaleStateEnum {
	
	ACTIVE((short)1,"ACTIVO"),
	DOWN((short)2,"BAJA");
	
	private Short id;
	private String name;
	
	private SaleStateEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static SaleStateEnum findById(Short id){
		for(SaleStateEnum saleStateEnum: SaleStateEnum.values()){
			if (saleStateEnum.getId()==id) {
				return saleStateEnum;
			}
		}
		return null;
	}
	
	public static SaleStateEnum findByName(String name){
		for(SaleStateEnum saleStateEnum: SaleStateEnum.values()){
			if (saleStateEnum.getName().equals(name)) {
				return saleStateEnum;
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
