package com.picsauditing.tags.twitterbootstrap;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;

import java.io.Writer;
import java.util.List;
import java.util.Map;

public class Error extends ElementComponent {

	public static final String TEMPLATE = "span";

	private String errorName;

	private List<String> errors;

	public Error(ValueStack stack) {
		super(stack);
	}

	@Override
	public boolean start(Writer writer) {
		fetchErrors();

		if (CollectionUtils.isEmpty(this.errors)) return false;

		try {
			writer.write(buildErrorIconTag());

			writer.write(buildOpenTag());
		} catch (Exception e) {
			throw new StrutsException(e);
		}

		return true;
	}

	@Override
	public boolean end(Writer writer, String body) {
		if (CollectionUtils.isEmpty(this.errors)) return false;

		try {
			writer.write(buildErrorsTag());

			writer.write(buildCloseTag());
		} catch (Exception e) {
			throw new StrutsException(e);
		} finally {
			popComponentStack();
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private void fetchErrors() {
		Map<String, List<String>> errors = (Map<String, List<String>>) stack.findValue("fieldErrors");

		this.errors = errors.get(generateName());
	}

	private String buildErrorIconTag() {
		String[] errorIconTag = new String[] { "<span class=\"help-inline\">", "<i class=\"icon-asterisk\"></i>", "</span>" };

		return StringUtils.join(errorIconTag);
	}

	private String buildOpenTag() {
		Map<String, Object> parameters = buildParameters();

		return new StringBuilder("<")
				.append(TEMPLATE)
				.append(Strings.SINGLE_SPACE)
				.append(StringUtils.join(buildAttributes(parameters).toArray(),
						Strings.SINGLE_SPACE)).append(">").toString();
	}

	private String buildErrorsTag() {
		StringBuilder errors = new StringBuilder("");

		errors.append("<ul class=\"unstyled\">");

		for (String error : this.errors) {
			errors.append("<li>" + error + "</li>");
		}

		errors.append("</ul>");

		return errors.toString();
	}

	private String buildCloseTag() {
		return new StringBuilder("</").append(TEMPLATE).append(">").toString();
	}

	@Override
	protected Map<String, Object> buildParameters() {
		Map<String, Object> parameters = super.buildParameters();

		parameters = addClassAttribute(parameters);

		return parameters;
	}

	private Map<String, Object> addClassAttribute(Map<String, Object> parameters) {
		if (!parameters.containsKey("class")) {
			parameters.put("class", "help-block errors");
		}

		return parameters;
	}

	public String getErrorName() {
		return this.errorName;
	}

	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}
}
