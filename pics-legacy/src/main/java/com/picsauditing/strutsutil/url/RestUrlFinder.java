package com.picsauditing.strutsutil.url;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.util.CollectionUtils;

import com.picsauditing.util.Strings;

public class RestUrlFinder {

	static final String PICS_REST_PREFIXES_MAPPER = "picsRestful";

	public static boolean isRestActionMapping(ActionMapping mapping, String prefixActionMapperList) {
		Set<String> restUrls = getRestActionPrefixes(prefixActionMapperList);
		if (CollectionUtils.isEmpty(restUrls) || Strings.isEmpty(mapping.getNamespace())) {
			return false;
		}

		String namespace = mapping.getNamespace();
		for (String url : restUrls) {
			if (namespace.startsWith(url)) {
				return true;
			}
		}

		return false;
	}

	public static Set<String> getRestActionPrefixes(String prefixActionMapperList) {
		if (Strings.isEmpty(prefixActionMapperList)) {
			return Collections.emptySet();
		}

		String[] parsedPrefixes = prefixActionMapperList.split(",");
		if (ArrayUtils.isEmpty(parsedPrefixes)) {
			return Collections.emptySet();
		}

		Set<String> restPrefixes = new HashSet<String>();
		for (String parsedPrefix : parsedPrefixes) {
			String restPrefix = getRestPrefix(parsedPrefix);
			if (restPrefix != null) {
				restPrefixes.add(restPrefix.trim());
			}
		}

		return restPrefixes;
	}

	private static String getRestPrefix(String parsedPrefix) {
		if (Strings.isEmpty(parsedPrefix)) {
			return null;
		}

		int index = parsedPrefix.indexOf(':');
		if (index >= -1) {
			if (isRestPrefix(parsedPrefix.substring(index == parsedPrefix.length() - 1 ? index : index + 1))) {
				return parsedPrefix.substring(0, index);
			}
		}

		return null;
	}

	private static boolean isRestPrefix(String mapperName) {
		if (Strings.isNotEmpty(mapperName)) {
			mapperName = mapperName.trim();
		}

		return PICS_REST_PREFIXES_MAPPER.equalsIgnoreCase(mapperName);
	}
}
