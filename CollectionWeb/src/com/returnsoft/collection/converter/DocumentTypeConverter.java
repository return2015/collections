package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.DocumentTypeEnum;

@Converter(autoApply=true)
public class DocumentTypeConverter implements AttributeConverter<DocumentTypeEnum, Short>{

	@Override
	public Short convertToDatabaseColumn(DocumentTypeEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public DocumentTypeEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : DocumentTypeEnum.findById(dbData);
	}
}
