package com.picsauditing.report.models;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

public class ModelSpec {
	String fromAlias;
	String key;
	String alias;
	FieldCategory category;
	FieldImportance minimumImportance;
	List<ModelSpec> joins = new ArrayList<ModelSpec>();

	public ModelSpec(String key, String alias, FieldCategory category) {
		this(key, alias);
		this.category = category;
	}

	public ModelSpec(String key, String alias) {
		this.key = key;
		this.fromAlias = "";
		this.alias = alias;
	}

	public ModelSpec(String key, String fromAlias, String alias) {
		this.key = key;
		this.fromAlias = fromAlias;
		this.alias = alias;
	}

	public ModelSpec join(String key) {
		String joinAlias = alias + key;

		ModelSpec join = new ModelSpec(key, alias, joinAlias);

		joins.add(join);
		
		return join;
	}
}
