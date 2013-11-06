package com.picsauditing.breadcrumb;

import com.picsauditing.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BreadcrumbPathBuilder {
	private static BreadcrumbCache breadcrumbCache = BreadcrumbCache.getInstance();

	public static List<Breadcrumb> getBreadcrumbs(String uri) {
		if (Strings.isEmpty(uri)) {
			return Collections.emptyList();
		}

		uri = cleanUri(uri);
		String[] tokens = uri.split("/");
		BreadcrumbVisitor visitor = new BreadcrumbVisitor();

		SiteHierarchyDirectedTree root = breadcrumbCache.getRoot();
		root.traverse(visitor, tokens);

		return visitor.getPath();
	}

	private static String[] removeDynamicParameters(String[] tokens) {
		List<String> nonDynamicParameters = new ArrayList<>();

		for (String token : tokens) {
			if (!token.startsWith("{") && !token.endsWith("}")) {
				nonDynamicParameters.add(token);
			}
		}

		return nonDynamicParameters.toArray(new String[0]);
	}

	private static String cleanUri(String uri) {
		if (uri.indexOf("?") > 0) {
			uri = uri.substring(0, uri.indexOf("?"));
		}

		while (uri.indexOf("/") == 0) {
			uri = uri.substring(1);
		}

		return uri;
	}
}
