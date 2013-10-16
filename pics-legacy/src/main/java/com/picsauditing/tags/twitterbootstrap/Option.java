package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;

import java.io.Writer;
import java.util.Map;

public class Option extends ElementComponent {

	public static final String TEMPLATE = "option";

	private String selected;

	public Option(ValueStack stack) {
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
						Strings.SINGLE_SPACE)).append(">").toString();
	}

	private String buildCloseTag() {
		return new StringBuilder("</").append(TEMPLATE).append(">").toString();
	}

	@Override
	protected Map<String, Object> buildParameters() {
		Map<String, Object> parameters = super.buildParameters();

		parameters = addSelectedAttribute(parameters);

		return parameters;
	}

	private Map<String, Object> addSelectedAttribute(Map<String, Object> parameters) {
		if (selected == "true") {
			parameters.put("selected", null);

			return parameters;
		}

		Select select = (Select) findAncestor(Select.class);
		Object selectValue = getStack().findValue(generateName(select.getSelectName()));
		
		if (selectValue == null) {
			return parameters;
		}

		String[] valueArray = StringUtils.split(selectValue.toString(), ", ");
		int valueArrayLength = valueArray.length;

		for (int i = 0; i < valueArrayLength; i++) {
			Object optionValue = this.dynamicAttributes.get("value");
			
			if (optionValue != null && optionValue.toString().equals(valueArray[i])) {
				parameters.put("selected", null);
			}
		}

		return parameters;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
