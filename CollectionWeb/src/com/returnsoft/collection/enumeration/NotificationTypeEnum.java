package com.returnsoft.collection.enumeration;

public enum NotificationTypeEnum {
	
	MAIL((short)1,"Correo"),
	PHYSICAL((short)2,"Físico");
	
	private Short id;
	private String name;
	
	private NotificationTypeEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static NotificationTypeEnum findById(Short id){
		for(NotificationTypeEnum mailingTypeEnum: NotificationTypeEnum.values()){
			if (mailingTypeEnum.getId()==id) {
				return mailingTypeEnum;
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
