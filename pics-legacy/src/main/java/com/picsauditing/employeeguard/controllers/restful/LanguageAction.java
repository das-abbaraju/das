package com.picsauditing.employeeguard.controllers.restful;

import com.google.gson.Gson;
import com.picsauditing.access.Anonymous;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.viewmodel.IdNameModel;
import com.picsauditing.model.i18n.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class LanguageAction extends PicsRestActionSupport {

	@Anonymous
	public String index() {
		List<KeyValue<String, String>> visibleLanguagesSansDialect = supportedLanguages.getVisibleLanguagesSansDialect();
		List idNameModels = new ArrayList<>();
		for (KeyValue<String, String> keyValue : visibleLanguagesSansDialect) {
			idNameModels.add(new IdNameModel.Builder().id(keyValue.getKey()).name(keyValue.getValue()).build());
		}

		jsonString = new Gson().toJson(idNameModels);

		return JSON_STRING;
	}
}
