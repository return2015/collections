package com.returnsoft.collection.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.returnsoft.collection.enumeration.NotificationStateEnum;
@Converter(autoApply=true)
public class NotificationStateConverter implements AttributeConverter<NotificationStateEnum, Short> {

	@Override
	public Short convertToDatabaseColumn(NotificationStateEnum attribute) {
		return (attribute==null) ? null :  attribute.getId();
	}

	@Override
	public NotificationStateEnum convertToEntityAttribute(Short dbData) {
		return (dbData == null) ? null : NotificationStateEnum.findById(dbData);
	}

}
