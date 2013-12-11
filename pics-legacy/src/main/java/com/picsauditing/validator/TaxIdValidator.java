package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;

public abstract class TaxIdValidator {

    abstract public String validated(Country country, String vatCode) throws ValidationException;

    abstract public String getLabel();
}
