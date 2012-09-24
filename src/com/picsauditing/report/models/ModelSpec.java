package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

public class ModelSpec {
	String key;
	String alias;
	FieldCategory category;
	FieldImportance minimumImportance;
	List<ModelSpec> joins = new ArrayList<ModelSpec>();

	public ModelSpec(String key, String alias) {
		this.key = key;
		this.alias = alias;
	}

	public ModelSpec join(String key) {
		ModelSpec join = new ModelSpec(key, this.alias + key);
		return join;
	}

	public ModelSpec join(String key, String alias) {
		ModelSpec join = new ModelSpec(key, alias);
		return join;
	}
}
