package com.picsauditing.breadcrumb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreadcrumbCache {
	private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbCache.class);

	private static transient BreadcrumbCache INSTANCE;
	private transient volatile SiteHierarchyDirectedTree root;

	private BreadcrumbCache() {
	}

	public static BreadcrumbCache getInstance() {
		BreadcrumbCache breadcrumbCache = INSTANCE;

		if (breadcrumbCache == null) {
			synchronized (BreadcrumbCache.class) {
				breadcrumbCache = INSTANCE;
				if (breadcrumbCache == null) {
					INSTANCE = new BreadcrumbCache();

					SiteHierarchyBuilder siteHierarchyBuilder = new SiteHierarchyBuilder();

					try {
						INSTANCE.root = siteHierarchyBuilder.getRoot();
					} catch (Exception exception) {
						LOG.error("Could not parse breadcrumbs.xml to get root breadcrumb", exception);
						INSTANCE.root = new SiteHierarchyDirectedTree(null);
					}
				}
			}
		}

		return INSTANCE;
	}

	public SiteHierarchyDirectedTree getRoot() {
		return root;
	}

	public void clear() {
		synchronized (this) {
			SiteHierarchyBuilder siteHierarchyBuilder = new SiteHierarchyBuilder();

			try {
				INSTANCE.root = siteHierarchyBuilder.getRoot();
			} catch (Exception exception) {
				LOG.error("Could not parse breadcrumbs.xml to get root breadcrumb", exception);
				INSTANCE.root = new SiteHierarchyDirectedTree(null);
			}
		}
	}
}
