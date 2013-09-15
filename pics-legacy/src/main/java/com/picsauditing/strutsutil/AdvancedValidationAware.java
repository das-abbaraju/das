package com.picsauditing.strutsutil;

import java.util.Collection;

import com.opensymphony.xwork2.ValidationAware;

public interface AdvancedValidationAware extends ValidationAware {

	void addAlertMessage(String alertMessage);

	Collection<String> getAlertMessages();

	void setAlertMessages(Collection<String> messages);

	boolean hasAlertMessages();

	String getActionMessageHeader();

	void setActionMessageHeader(String header);

	String getAlertMessageHeader();

	void setAlertMessageHeader(String header);

	String getActionErrorHeader();

	void setActionErrorHeader(String header);
}