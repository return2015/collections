package com.returnsoft.collection.enumeration;

public enum BankLetterEnum {
	
	FALABELLA((short)5,"plantillaFalabella.jrxml","signatureFalabella.jpg","Asistencia Falabella",
	"<br/>"
	+"<br/>"
	+"Mediante la presente agradecemos su preferencia por haber contratado el  Servicio de Asistencia GNB, a través de GNB Servicios Generales S.A.C., con el respaldo del Grupo Especializado de Asistencia del Perú S.A.C. (GEA)."
	+"<br/>"
	+"<br/>"
	+"Se le hace llegar el Condicionado y la Carta de Servicio de Asistencia GNB."
	+"<br/>"
	+"<br/>"
	+"Gracias.","falabella_asistencia@pe.geainternacional.com"),
	GNB((short)3,"plantillaGNB.jrxml","signatureGNB.jpg","Asistencia GNB",
	"<br/>"
	+"<br/>"
	+"Mediante la presente agradecemos su preferencia por haber contratado el  Servicio de Asistencia Falabella, a través de Falabella Servicios Generales S.A.C., con el respaldo del Grupo Especializado de Asistencia del Perú S.A.C. (GEA)."
	+"<br/>"
	+"<br/>"
	+"Se le hace llegar el Condicionado y la Carta de Servicio de Asistencia Falabella."
	+"<br/>"
	+"<br/>"
	+"Gracias.","gnb_asistencia@pe.geainternacional.com");
	
	private Short id;
	private String template;
	private String signature;
	private String subject;
	private String body;
	private String mail;
	
	private BankLetterEnum(short id, String template, String signature, String subject, String body, String mail){
		this.id=id;
		this.template=template;
		this.signature=signature;
		this.subject=subject;
		this.body=body;
		this.mail=mail;
	}
	
	public static BankLetterEnum findById(Short id){
		for(BankLetterEnum bankLetterEnum: BankLetterEnum.values()){
			if (bankLetterEnum.getId()==id) {
				return bankLetterEnum;
			}
		}
		return null;
	}
	

	public Short getId() {
		return id;
	}

	public String getTemplate() {
		return template;
	}

	public String getSignature() {
		return signature;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMail() {
		return mail;
	}
	
	

}
