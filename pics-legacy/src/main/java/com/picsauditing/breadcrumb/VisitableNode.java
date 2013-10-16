package com.picsauditing.breadcrumb;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class VisitableNode implements Visitable {
	public static final String ID_HINT = "{id}";

	private VisitableNode parent;
	private final Map<String, VisitableNode> children = Collections.synchronizedMap(new HashMap<String, VisitableNode>());
	private String hint;
	private String label;
	private String uri;

	public VisitableNode getParent() {
		return parent;
	}

	public Map<String, VisitableNode> getChildren() {
		return children;
	}

	public String getHint() {
		return hint;
	}

	public String getLabel() {
		return label;
	}

	public String getUri() {
		return uri;
	}

	public VisitableNode getChild(String hint) {
		if (NumberUtils.isNumber(hint)) {
			hint = ID_HINT;
		}

		if (MapUtils.isEmpty(children) || !children.containsKey(hint)) {
			return null;
		}

		return children.get(hint);
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", hint, label, uri);
	}

	public static class Builder {
		private VisitableNode parent;
		private String hint;
		private String label;
		private String uri;

		public Builder parent(VisitableNode parent) {
			this.parent = parent;
			return this;
		}

		public Builder hint(String hint) {
			this.hint = hint;
			return this;
		}

		public Builder label(String label) {
			this.label = label;
			return this;
		}

		public Builder uri(String uri) {
			this.uri = uri;
			return this;
		}

		public VisitableNode build() {
			VisitableNode visitableNode = new VisitableNode();

			visitableNode.parent = parent;
			visitableNode.hint = hint;
			visitableNode.label = label;
			visitableNode.uri = uri;

			return visitableNode;
		}
	}
}
