package com.returnsoft.collection.enumeration;

public enum UserTypeEnum {
	
	ADMIN((short)2,"Administrador"),
	//SUPERVISOR((short)2,"Supervisor"),
	AGENT((short)1,"Agente");
	
	private Short id;
	private String name;
	
	private UserTypeEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static UserTypeEnum findById(Short id){
		for(UserTypeEnum userTypeEnum: UserTypeEnum.values()){
			if (userTypeEnum.getId()==id) {
				return userTypeEnum;
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
