package com.picsauditing.breadcrumb;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbVisitor implements Visitor {
	private List<Breadcrumb> path = new ArrayList<>();

	@Override
	public void visit(VisitableNode visitableNode) {
		Breadcrumb breadcrumb = new Breadcrumb.Builder().label(visitableNode.getLabel()).uri(visitableNode.getUri()).build();
		path.add(breadcrumb);
	}

	public List<Breadcrumb> getPath() {
		return path;
	}
}
