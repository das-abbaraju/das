package com.picsauditing.controller;

import com.picsauditing.util.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import java.util.ArrayList;
import java.util.List;

public class ViewNamespace implements ViewNamespaceAware {

	@Override
	public String getUniquePageId() {
		List<String> items = new ArrayList<String>();
		String namespace = getFormattedNamespace();

		if (!namespace.isEmpty()) {
			items.add(namespace);
		}

		items.add(getActionName());
		items.add(getMethodName());

		return buildPageId(items);
	}

	@Override
	public String getPageId() {
		List<String> items = new ArrayList<String>();
		String namespace = getFormattedNamespace();

		if (!namespace.isEmpty()) {
			items.add(namespace);
		}

		items.add(getActionName());

		return buildPageId(items);
	}

	private String buildPageId(List<String> items) {
		return StringUtils.join(items, "_").replaceAll("[/-]", "_");
	}

	/**
	 * Format namespace for unique page id generation
	 * 
	 * @return
	 */
	private String getFormattedNamespace() {
		String namespace = this.getNamespace();

		if (namespace.startsWith("/")) {
			namespace = namespace.replaceFirst("/", Strings.EMPTY_STRING);
		}

		return namespace;
	}

	private String getActionName() {
		return ServletActionContext.getActionMapping().getName();
	}

	private String getMethodName() {
		return ServletActionContext.getActionMapping().getMethod();
	}

	private String getNamespace() {
		return ServletActionContext.getActionMapping().getNamespace();
	}

}
