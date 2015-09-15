package com.returnsoft.collection.enumeration;

public enum NotificationStateEnum {
	
	SENDING((short)1,"Entregado"),
	CONDITIONED((short)2,"Condicionado"),
	UNTRACEABLE((short)3,"Inubicable"),
	INVALID_ADDRESS((short)4,"Direccion incompleta"),
	OUT_ZONE((short)5,"Fuera de zona"),
	REACHABLE((short)6,"Rechazado"),
	DOWN_ACCOUNT((short)7,"Cuenta en baja");
	
	
	private Short id;
	private String name;
	
	private NotificationStateEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static NotificationStateEnum findById(Short id){
		for(NotificationStateEnum mailingStateEnum: NotificationStateEnum.values()){
			if (mailingStateEnum.getId()==id) {
				return mailingStateEnum;
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
