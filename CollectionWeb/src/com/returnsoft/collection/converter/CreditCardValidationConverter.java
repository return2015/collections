package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.CreditCardValidationEnum;

@Converter(autoApply=true)
public class CreditCardValidationConverter implements AttributeConverter<CreditCardValidationEnum, Short>{

	@Override
	public Short convertToDatabaseColumn(CreditCardValidationEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public CreditCardValidationEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : CreditCardValidationEnum.findById(dbData);
	}

}
