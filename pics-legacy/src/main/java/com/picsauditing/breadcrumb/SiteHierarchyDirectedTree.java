package com.picsauditing.breadcrumb;

import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;

final class SiteHierarchyDirectedTree {

	private final VisitableNode root;

	public SiteHierarchyDirectedTree(VisitableNode root) {
		this.root = root;
	}

	public VisitableNode getRoot() {
		return root;
	}

	public void traverse(Visitor visitor, String[] tokens) {
		if (root == null || MapUtils.isEmpty(root.getChildren())) {
			return;
		}

		int index = 1;
		String token = null;
		VisitableNode root = this.root;
		// token array = {"employee-guard", "contractor", "employee", "56", "edit"}
		while (Strings.isNotEmpty((token = nextToken(tokens, index)))) {
			VisitableNode visitableNode = root.getChild(token);
			if (visitableNode == null) {
				return; // maybe throw exception?
			}

			visitableNode.accept(visitor);
			root = visitableNode;
			index++;
		}
	}

	private String nextToken(String[] tokens, int index) {
		if (index >= tokens.length) {
			return null;
		}

		return tokens[index];
	}
}
