package com.dataus.template.securitycomplex.config.security.oauth2.info.enums.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.dataus.template.securitycomplex.config.security.oauth2.info.enums.ProviderType;

@Converter
public class ProviderTypeConverter implements AttributeConverter<ProviderType, String> {

    @Override
    public String convertToDatabaseColumn(ProviderType attribute) {
       return attribute.getCode();
    }

    @Override
    public ProviderType convertToEntityAttribute(String dbData) {
        return ProviderType.ofCode(dbData);
    }    
    
}
