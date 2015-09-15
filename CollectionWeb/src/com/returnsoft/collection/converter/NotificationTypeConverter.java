package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.NotificationTypeEnum;

@Converter(autoApply=true)
public class NotificationTypeConverter implements AttributeConverter<NotificationTypeEnum, Short>{

	@Override
	public Short convertToDatabaseColumn(NotificationTypeEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public NotificationTypeEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : NotificationTypeEnum.findById(dbData);
	}

}
