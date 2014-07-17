package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.picsauditing.access.Anonymous;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.viewmodel.IdNameModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialectAction extends PicsRestActionSupport {

	private String language;

	@Anonymous
	public String index() {
		Map<String, String> countriesBasedOn = supportedLanguages.getCountriesBasedOn(language);
		List idNameModels = new ArrayList<>();
		for (String language : countriesBasedOn.keySet()) {
			idNameModels.add(new IdNameModel.Builder().id(language).name(countriesBasedOn.get(language)).build());
		}

		jsonString = new Gson().toJson(idNameModels);

		return JSON_STRING;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
