package com.rkp.topcore.canonical.definition;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CanonicalFieldDefinition {
	
private String name;

private DataType type;

private boolean isFieldValidationEnabled;

private boolean isMaskingRequired;

private boolean required;

private boolean copyToResponse;

private Integer length;

private Integer maxLength;

private Integer precision;

private MaskingConfiguration masking;


public String getName() {

    return name;
}

public void setName(String name) {

    this.name = name;
}


public DataType getType() {

    return type;
}

public void setType(DataType type) {

    this.type = type;
}


@JsonProperty("isFieldValidationEnabled")
public boolean isFieldValidationEnabled() {

    return isFieldValidationEnabled;
}

@JsonProperty("isFieldValidationEnabled")
public void setFieldValidationEnabled(
        boolean fieldValidationEnabled) {

    this.isFieldValidationEnabled =
            fieldValidationEnabled;
}


@JsonProperty("isMaskingRequired")
public boolean isMaskingRequired() {

    return isMaskingRequired;
}

@JsonProperty("isMaskingRequired")
public void setMaskingRequired(
        boolean maskingRequired) {

    this.isMaskingRequired =
            maskingRequired;
}


public boolean isRequired() {

    return required;
}

public void setRequired(
        boolean required) {

    this.required = required;
}


@JsonProperty("copyToResponse")
public boolean isCopyToResponse() {

    return copyToResponse;
}

@JsonProperty("copyToResponse")
public void setCopyToResponse(
        boolean copyToResponse) {

    this.copyToResponse =
            copyToResponse;
}


public Integer getLength() {

    return length;
}

public void setLength(Integer length) {

    this.length = length;
}


public Integer getMaxLength() {

    return maxLength;
}

public void setMaxLength(Integer maxLength) {

    this.maxLength = maxLength;
}


public Integer getPrecision() {

    return precision;
}

public void setPrecision(Integer precision) {

    this.precision = precision;
}


public MaskingConfiguration getMasking() {

    return masking;
}

public void setMasking(
        MaskingConfiguration masking) {

    this.masking = masking;
}

}
