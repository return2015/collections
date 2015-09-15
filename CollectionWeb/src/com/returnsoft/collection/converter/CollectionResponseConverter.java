package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;
@Converter(autoApply=true)
public class CollectionResponseConverter implements AttributeConverter<CollectionResponseEnum, Short>{

	@Override
	public Short convertToDatabaseColumn(CollectionResponseEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public CollectionResponseEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : CollectionResponseEnum.findById(dbData);
	}

}
