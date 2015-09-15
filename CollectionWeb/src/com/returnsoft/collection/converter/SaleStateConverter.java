package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.SaleStateEnum;
@Converter(autoApply=true)
public class SaleStateConverter implements AttributeConverter<SaleStateEnum, Short> {

	@Override
	public Short convertToDatabaseColumn(SaleStateEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public SaleStateEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : SaleStateEnum.findById(dbData);
	}

}
