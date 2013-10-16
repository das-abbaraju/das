package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;

import java.io.Writer;
import java.util.Map;

public class Label extends ElementComponent {

	public static final String TEMPLATE = "label";

	private String labelName;

	private String labelFor;

	public Label(ValueStack stack) {
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

		return new StringBuilder("<").append(TEMPLATE).append(Strings.SINGLE_SPACE).append(StringUtils.join(buildAttributes(parameters).toArray(), " ")).append(">").toString();
	}

	private String buildCloseTag() {
		return new StringBuilder("</").append(TEMPLATE).append(">").toString();
	}

	@Override
	protected Map<String, Object> buildParameters() {
		Map<String, Object> parameters = super.buildParameters();

		parameters = addClassAttribute(parameters);

		if (getLabelName() == null) {
			return parameters;
		}

		parameters = addForAttribute(parameters);

		return parameters;
	}

	private Map<String, Object> addClassAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("class")) {
			parameters.put("class", "control-label");
		}

		return parameters;
	}

	private Map<String, Object> addForAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("for")) {
			setLabelFor(generateId());

			parameters.put("for", getLabelFor());
		}

		return parameters;
	}

	public String getLabelName() {
		return this.labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getLabelFor() {
		return this.labelFor;
	}

	public void setLabelFor(String labelFor) {
		this.labelFor = labelFor;
	}
}
