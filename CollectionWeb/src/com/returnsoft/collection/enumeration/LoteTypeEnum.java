package com.returnsoft.collection.enumeration;

public enum LoteTypeEnum {
	
	CREATESALE((short)1,"Creaci�n de ventas"),
	CREATECOLLECTION((short)2,"Creaci�n de cobranzas"),
	UPDATECREDITCARD((short)3,"Actualizaci�n de TC"),
	UPDATESALESTATE((short)4,"Actualizaci�n de estado"),
	
	///////
	
	CREATEPHYSICALNOTIFICACION((short)5,"Creaci�n de notificaciones f�sicas"),
	CREATEVIRTUALNOTIFICACION((short)6,"Creaci�n de notificaciones virtuales"),
	UPDATEPHYSICALNOTIFICACION((short)7,"Actualizaci�n de notificaciones f�sicas"),
	
	CREATEREPAYMENTS((short)8,"Creaci�n de extornos");
	
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
