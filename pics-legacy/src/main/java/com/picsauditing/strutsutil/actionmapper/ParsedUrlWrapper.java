package com.picsauditing.strutsutil.actionmapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.picsauditing.util.Strings;

final class ParsedUrlWrapper implements Iterable<String> {

	private final String uri;
	private List<String> parsedUrl = Collections.emptyList();

	public ParsedUrlWrapper(String uri) {
		this.uri = uri;
	}

	public void parse() {
		if (Strings.isEmpty(uri)) {
			return;
		}

		parsedUrl = new ArrayList<>(Arrays.asList(uri.split("/")));
	}

	public boolean isEmpty() {
		return parsedUrl.isEmpty();
	}

	public boolean isNextValue(String value) {
		if (isEmpty()) {
			return false;
		}

		return parsedUrl.get(0).equals(value);
	}

	@Override
	public String toString() {
		return uri;
	}

	@Override
	public Iterator<String> iterator() {
		return parsedUrl.iterator();
	}

}