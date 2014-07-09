package com.picsauditing.breadcrumb;

import com.picsauditing.employeeguard.msgbundle.EGI18n;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class BreadcrumbVisitor implements Visitor {
	private List<Breadcrumb> path = new ArrayList<>();

	@Override
	public void visit(VisitableNode visitableNode) {
		String breadcrumbLabel = EGI18n.getBreadCrumbResourceBundle(visitableNode.getLabel());
		Breadcrumb breadcrumb = new Breadcrumb.Builder().label(breadcrumbLabel).uri(visitableNode.getUri()).build();
		path.add(breadcrumb);
	}

	public List<Breadcrumb> getPath() {
		return path;
	}
}
