package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.LoteTypeEnum;
@Converter(autoApply=true)
public class LoteTypeConverter implements AttributeConverter<LoteTypeEnum, Short> {
	@Override
	public Short convertToDatabaseColumn(LoteTypeEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public LoteTypeEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : LoteTypeEnum.findById(dbData);
	}

}
