package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ElementComponent extends Component {

	// generic field name to enable auto generating id and name
	protected String fieldName;

	private String readonly;
	private String disabled;

	// dynamic attributes
	protected Map<String, Object> dynamicAttributes = new HashMap<String, Object>();

	public ElementComponent(ValueStack stack) {
		super(stack);
	}

	protected Map<String, Object> buildParameters() {
		Map<String, Object> parameters = new HashMap<String, Object>();

		parameters = addReadonlyAttribute(parameters);

		parameters = addDisabledAttribute(parameters);

		parameters.putAll(dynamicAttributes);

		return parameters;
	}

	private Map<String, Object> addReadonlyAttribute(Map<String, Object> parameters) {
		if (readonly == "true") {
			parameters.put("readonly", null);
		}

		return parameters;
	}

	private Map<String, Object> addDisabledAttribute(Map<String, Object> parameters) {
		if (disabled == "true") {
			parameters.put("disabled", null);
		}

		return parameters;
	}

	protected List<String> buildAttributes(Map<String, Object> parameters) {
		List<String> attributes = new ArrayList<String>();

		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			attributes.add(formatAttribute(entry.getKey(), entry.getValue()));
		}

		return attributes;
	}

	private String formatAttribute(String key, Object value) {
		if (value == null) {
			return key;
		}

		// TODO: need to escape html
		return new StringBuilder(key).append("=\"").append(value.toString().replace("\"", "&#34;")).append("\"").toString();
	}

	// auto generate id and name fields based on the fieldName passed in.
	private String generateAttribute(String fieldName, String separator) {
		Form form = (Form) findAncestor(Form.class);
		String[] inputParts = new String[] { form.getFormName(), fieldName };

		return StringUtils.join(inputParts, separator);
	}

	protected String generateId() {
		return this.generateAttribute(this.fieldName, "_");
	}

	protected String generateId(String fieldName) {
		return this.generateAttribute(fieldName, "_");
	}

	protected String generateName() {
		return this.generateAttribute(this.fieldName, ".");
	}

	protected String generateName(String fieldName) {
		return this.generateAttribute(fieldName, ".");
	}

	public void setDynamicAttributes(Map<String, Object> dynamicAttributes) {
		this.dynamicAttributes.putAll(dynamicAttributes);
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}
}
