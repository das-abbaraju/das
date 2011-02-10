package com.picsauditing.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;

@SuppressWarnings("serial")
public class AutocompleteTag extends ComponentTagSupport {

	private String action;
	private String name;
	private String htmlId;
	private String htmlName;
	private String value;
	private String hiddenValue;
	private String textValue;

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new Autocomplete(stack);
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		Autocomplete autocomplete = (Autocomplete) component;
		autocomplete.setAction(action);
		autocomplete.setName(name);
		autocomplete.setHtmlId(htmlId);
		autocomplete.setHtmlName(htmlName);
		autocomplete.setValue(value);
		autocomplete.setHiddenValue(hiddenValue);
		autocomplete.setTextValue(textValue);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId(String id) {
		this.htmlId = id;
	}

	public String getHtmlName() {
		return htmlName;
	}

	public void setHtmlName(String htmlName) {
		this.htmlName = htmlName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getHiddenValue() {
		return hiddenValue;
	}

	public void setHiddenValue(String hiddenString) {
		this.hiddenValue = hiddenString;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String valueString) {
		this.textValue = valueString;
	}

}
