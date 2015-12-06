package com.returnsoft.collection.enumeration;

public enum DocumentTypeEnum {
	
	DNI((short)1,"DNI"),
	RUC((short)2,"RUC");
	
	private Short id;
	private String name;
	
	private DocumentTypeEnum(short id, String name){
		this.id=id;
		this.name=name;
	}
	
	public static DocumentTypeEnum findById(Short id){
		for(DocumentTypeEnum payerDocumentTypeEnum: DocumentTypeEnum.values()){
			if (payerDocumentTypeEnum.getId()==id) {
				return payerDocumentTypeEnum;
			}
		}
		return null;
	}
	
	public static DocumentTypeEnum findByName(String name){
		for(DocumentTypeEnum payerDocumentTypeEnum: DocumentTypeEnum.values()){
			if (payerDocumentTypeEnum.getName().equals(name)) {
				return payerDocumentTypeEnum;
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
