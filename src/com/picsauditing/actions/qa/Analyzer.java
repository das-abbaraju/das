package com.picsauditing.actions.qa;

import java.sql.SQLException;
import java.util.HashMap;

import com.picsauditing.search.SelectSQL;

public abstract class Analyzer {
	protected String leftDatabase = "pics_qa";
	protected String rightDatabase = "pics_live";

	private HashMap<String, String> dbLabel = new HashMap<String, String>() {
		private static final long serialVersionUID = -1058261188011397147L;
		{
			put("pics_qa", "QA");
			put("pics_live", "Stable");
			put("pics_alpha", "Alpha");
			put("pics_alpha1", "Alpha");
			put("pics_alpha2", "Alpha");
		}
	};

	protected String leftLabel = dbLabel.get(leftDatabase);
	protected String rightLabel = dbLabel.get(rightDatabase);

	public Analyzer() {
	}

	public Analyzer(String leftDatabase, String rightDatabase) {
		this.leftDatabase = leftDatabase;
		this.rightDatabase = rightDatabase;
		leftLabel = (dbLabel.containsKey(leftDatabase)) ? dbLabel.get(leftDatabase) : leftDatabase;
		rightLabel = (dbLabel.containsKey(rightDatabase)) ? dbLabel.get(rightDatabase) : rightDatabase;
	}

	public abstract void run() throws SQLException;

}
