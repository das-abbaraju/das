package com.picsauditing.actions.converters;

import com.picsauditing.jpa.entities.PaymentMethod;

public class PaymentMethodConverter extends EnumConverter {
	public PaymentMethodConverter() {
		enumClass = PaymentMethod.class;
	}
}
