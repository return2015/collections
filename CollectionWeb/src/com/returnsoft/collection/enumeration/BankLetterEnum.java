package com.returnsoft.collection.enumeration;

public enum BankLetterEnum {

	FALABELLA((short) 5, "plantillaFalabella.jrxml", "signatureFalabella.jpg", "Asistencia Falabella",
			"mailFalabella.html"), GNB((short) 3, "plantillaGNB.jrxml", "signatureGNB.jpg", "Asistencia GNB",
					"mailGNB.html");

	private Short id;
	private String templateLetter;
	private String signature;
	private String templateMail;
	private String subject;

	private BankLetterEnum(short id, String templateLetter,String signature, String subject, String templateMail) {
		this.id = id;
		this.templateLetter = templateLetter;
		this.signature = signature;
		this.subject = subject;
		this.templateMail = templateMail;

	}

	public static BankLetterEnum findById(Short id) {
		for (BankLetterEnum bankLetterEnum : BankLetterEnum.values()) {
			if (bankLetterEnum.getId() == id) {
				return bankLetterEnum;
			}
		}
		return null;
	}

	public Short getId() {
		return id;
	}

	public String getSignature() {
		return signature;
	}

	public String getSubject() {
		return subject;
	}

	public String getTemplateLetter() {
		return templateLetter;
	}

	public String getTemplateMail() {
		return templateMail;
	}

}
