package com.picsauditing.strutsutil.url;

import java.util.Set;

import org.apache.struts2.views.util.DefaultUrlHelper;
import org.apache.struts2.views.util.UrlHelper;
import org.springframework.util.CollectionUtils;

import com.picsauditing.util.Strings;

public class UrlHelperFactory {

	private static UrlHelper picsUrlHelper = new PicsRestUrlHelper();
	private static UrlHelper strutsDefaultUrlHelper = new DefaultUrlHelper();

	public static UrlHelper getUrlHelper(String action, String prefixActionMapperList) {
		if (isRestUrl(action, prefixActionMapperList)) {
			return picsUrlHelper;
		}

		return strutsDefaultUrlHelper;
	}

	private static boolean isRestUrl(String action, String prefixActionMapperList) {
		Set<String> restUrls = RestUrlFinder.getRestActionPrefixes(prefixActionMapperList);
		if (CollectionUtils.isEmpty(restUrls) || Strings.isEmpty(action)) {
			return false;
		}

		for (String restUrl : restUrls) {
			if (action.contains(restUrl)) {
				return true;
			}
		}

		return false;
	}

}
