package com.picsauditing.breadcrumb;

import com.picsauditing.util.Strings;

public class Breadcrumb {
	public static final String ID_HINT_REGEX = "\\{id\\}";
	public static final String NAME_HINT = "{name}";
	public static final String NAME_HINT_REGEX = "\\{name\\}";

	private String hint;
	private String name;
	private String uri;

	public Breadcrumb() {
	}

	public Breadcrumb(String hint) {
		this.hint = hint;
	}

	public String getHint() {
		return hint;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public String name(String displayName) {
		if (Strings.isNotEmpty(name) && name.contains(NAME_HINT)) {
			return name.replaceAll(NAME_HINT_REGEX, displayName);
		}

		return name;
	}

	public String uri(String id) {
		if (Strings.isNotEmpty(uri) && uri.contains(VisitableNode.ID_HINT)) {
			return uri.replaceAll(ID_HINT_REGEX, id);
		}

		return uri;
	}

	@Override
	public String toString() {
		return String.format("%s: %s %s", hint, name, uri);
	}

	public static class Builder {
		private String label;
		private String uri;

		public Builder label(String label) {
			this.label = label;
			return this;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}

		public Breadcrumb build() {
			Breadcrumb breadcrumb = new Breadcrumb();
			breadcrumb.name = label;
			breadcrumb.uri = uri;
			return breadcrumb;
		}
	}
}
