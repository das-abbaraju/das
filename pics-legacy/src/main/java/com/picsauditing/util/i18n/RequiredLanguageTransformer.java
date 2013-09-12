package com.picsauditing.util.i18n;

import com.picsauditing.jpa.entities.RequiresLanguages;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.*;

public class RequiredLanguageTransformer {
	public List<String> getListFromJSON(String jsonArray) {
		if (Strings.isEmpty(jsonArray)) {
			return Collections.emptyList();
		}

		List<String> languages = new ArrayList<>();

		JSONArray JSONLanguages = (JSONArray) JSONValue.parse(jsonArray);
		languages.clear();
		for (Object language : JSONLanguages) {
			languages.add((String) language);
		}

		return languages;
	}

	@SuppressWarnings("unchecked")
	public String getJSONStringFrom(List<String> list) {
		if (list == null || list.isEmpty()) {
			return Strings.EMPTY_STRING;
		}

		JSONArray jsonArray = new JSONArray();

		for (String item : list) {
			jsonArray.add(item);
		}

		return jsonArray.toJSONString();
	}

	public void updateRequiredLanguages(RequiresLanguages requiresLanguages, List<String> add, List<String> remove) {
		if (requiresLanguages == null) {
			return;
		}

		if (add == null) {
			add = Collections.emptyList();
		}

		if (remove == null) {
			remove = Collections.emptyList();
		}

		Set<String> newLanguages = new TreeSet<>();
		if (requiresLanguages.getLanguages() != null) {
			newLanguages.addAll(requiresLanguages.getLanguages());
		}

		newLanguages.addAll(add);
		newLanguages.removeAll(remove);

		requiresLanguages.setLanguages(new ArrayList<>(newLanguages));
	}

	public List<String> getLanguagesToAdd(List<String> source, List<String> target) {
		if (source == null) {
			source = Collections.emptyList();
		}

		if (target == null) {
			target = Collections.emptyList();
		}

		List<String> languagesToAdd = new ArrayList<>(target);
		languagesToAdd.removeAll(source);
		return languagesToAdd;
	}

	public List<String> getLanguagesToRemove(List<String> source, List<String> target) {
		if (source == null) {
			source = Collections.emptyList();
		}

		if (target == null) {
			target = Collections.emptyList();
		}

		List<String> languagesToRemove = new ArrayList<>(source);
		languagesToRemove.removeAll(target);
		return languagesToRemove;
	}
}