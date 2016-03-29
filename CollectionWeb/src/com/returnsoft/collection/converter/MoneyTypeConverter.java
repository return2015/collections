package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.MoneyTypeEnum;
@Converter(autoApply=true)
public class MoneyTypeConverter implements AttributeConverter<MoneyTypeEnum, Short> {
	
	@Override
	public Short convertToDatabaseColumn(MoneyTypeEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public MoneyTypeEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : MoneyTypeEnum.findById(dbData);
	}

}
