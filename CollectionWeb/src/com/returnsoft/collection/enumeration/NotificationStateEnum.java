package com.returnsoft.collection.enumeration;

public enum NotificationStateEnum {
	
	SENDING((short)1,"Enviado",true),
	
	DELIVERED((short)2,"Entregado",false),
	UNTRACEABLE((short)3,"Inubicable",false),
	RETURNED((short)4,"Devuelto",false);
	
	private Short id;
	private String name;
	private Boolean pending;
	
	private NotificationStateEnum(short id, String name, boolean pending){
		this.id=id;
		this.name=name;
		this.pending=pending;
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

	public Boolean getPending() {
		return pending;
	}
	
}
