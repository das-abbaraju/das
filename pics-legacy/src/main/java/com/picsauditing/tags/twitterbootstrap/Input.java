package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;

import java.io.Writer;
import java.util.Map;

public class Input extends ElementComponent {

	public static final String TEMPLATE = "input";

	private String inputName;

	private String id;
	private String name;

	private String checked;

	public Input(ValueStack stack) {
		super(stack);
	}

	@Override
	public boolean start(Writer writer) {
		try {
			writer.write(buildOpenTag());
		} catch (Exception e) {
			throw new StrutsException(e);
		}

		return true;
	}

	@Override
	public boolean end(Writer writer, String body) {
		try {
			writer.write(buildCloseTag());
		} catch (Exception e) {
			throw new StrutsException(e);
		} finally {
			popComponentStack();
		}

		return false;
	}

	private String buildOpenTag() {
		Map<String, Object> parameters = buildParameters();

		return new StringBuilder("<")
				.append(TEMPLATE)
				.append(Strings.SINGLE_SPACE)
				.append(StringUtils.join(buildAttributes(parameters).toArray(),
						Strings.SINGLE_SPACE)).toString();
	}

	private String buildCloseTag() {
		return " />";
	}

	@Override
	protected Map<String, Object> buildParameters() {
		Map<String, Object> parameters = super.buildParameters();

		if (getInputName() == null) {
			return parameters;
		}

		parameters = addIdAttribute(parameters);

		parameters = addNameAttribute(parameters);

		parameters = addCheckedAttribute(parameters);

		parameters = addValueAttribute(parameters);

		return parameters;
	}

	private Map<String, Object> addIdAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("id")) {
			setId(generateId());

			parameters.put("id", getId());
		}

		return parameters;
	}

	private Map<String, Object> addNameAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("name")) {
			setName(generateName());

			parameters.put("name", getName());
		}

		return parameters;
	}

	private Map<String, Object> addCheckedAttribute(Map<String, Object> parameters) {
		Object type = this.dynamicAttributes.get("type");
		Object value = this.dynamicAttributes.get("value");
		
		if (type != null && !type.equals("checkbox")) {
			return parameters;
		}

		if (value != null && value.equals(getStack().findValue(generateName()))) {
			parameters.put("checked", null);
		}

		return parameters;
	}

	private Map<String, Object> addValueAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("value") || parameters.get("value") == null) {
			parameters.put("value", getValue(generateName()));
		}

		return parameters;
	}
	
	private Object getValue(String name) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = (Map<String, Object>) getStack().getContext().get(ActionContext.PARAMETERS);
		if (MapUtils.isEmpty(params)) {
			return null;
		}
		
		String[] value = (String[]) params.get(name); 
		if (ArrayUtils.isEmpty(value)) {
			return null;
		}
		
		return value[0];
	}

	public String getInputName() {
		return this.inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public String getChecked() {
		return this.checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
