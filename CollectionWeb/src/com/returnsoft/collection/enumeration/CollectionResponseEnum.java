package com.returnsoft.collection.enumeration;

public enum CollectionResponseEnum {
	
	
	ALLOW((short)1,"APROBADO"),
	DENY((short)2,"DENEGADO");
	
	private Short id;
	private String name;
	
	private CollectionResponseEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static CollectionResponseEnum findById(Short id){
		for(CollectionResponseEnum collectionResponseEnum: CollectionResponseEnum.values()){
			if (collectionResponseEnum.getId()==id) {
				return collectionResponseEnum;
			}
		}
		return null;
	}
	
	public static CollectionResponseEnum findByName(String name){
		for(CollectionResponseEnum collectionResponseEnum: CollectionResponseEnum.values()){
			if (collectionResponseEnum.getName().equals(name)) {
				return collectionResponseEnum;
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
