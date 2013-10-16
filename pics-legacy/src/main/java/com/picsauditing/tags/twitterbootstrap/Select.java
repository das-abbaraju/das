package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;

import java.io.Writer;
import java.util.Map;

public class Select extends ElementComponent {

	public static final String TEMPLATE = "select";

	private String selectName;

	private String id;
	private String name;

	private String multiple;

	public Select(ValueStack stack) {
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

		parameters = addMultipleAttribute(parameters);

		if (getSelectName() == null) {
			return parameters;
		}

		parameters = addIdAttribute(parameters);

		parameters = addNameAttribute(parameters);

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

	private Map<String, Object> addMultipleAttribute(Map<String, Object> parameters) {
		if (multiple == "true") {
			parameters.put("multiple", null);
		}

		return parameters;
	}

	public String getSelectName() {
		return this.selectName;
	}

	public void setSelectName(String selectName) {
		this.selectName = selectName;
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

	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
}
