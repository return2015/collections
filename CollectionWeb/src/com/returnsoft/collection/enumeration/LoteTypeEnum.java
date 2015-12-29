package com.returnsoft.collection.enumeration;

public enum LoteTypeEnum {
	
	CREATESALE((short)1,"Creación de ventas"),
	CREATECOLLECTION((short)2,"Creación de cobranzas"),
	UPDATECREDITCARD((short)3,"Actualización de TC"),
	UPDATESALESTATE((short)4,"Actualización de estado");
	
	
	private Short id;
	private String name;
	
	private LoteTypeEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static LoteTypeEnum findById(Short id){
		for(LoteTypeEnum loteTypeEnum: LoteTypeEnum.values()){
			if (loteTypeEnum.getId()==id) {
				return loteTypeEnum;
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
