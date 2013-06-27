package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class TradeTable extends AbstractTable {

	public static final String Children = "Children";
    public static final String Parent = "Parent";

	public TradeTable() {
		super("ref_trade");
		addFields(Trade.class);

        Field tradeName = new Field("Name","id", FieldType.Trade);
        tradeName.setTranslationPrefixAndSuffix("Trade","name");
        tradeName.setImportance(FieldImportance.Required);
        tradeName.setCategory(FieldCategory.AccountInformation);
        tradeName.setVisible(false);
        addField(tradeName);
	}

	protected void addJoins() {
        ReportOnClause childrenOnClause = new ReportOnClause(null, null, ReportOnClause.FromAlias + ".indexLevel <= " + ReportOnClause.ToAlias + ".indexLevel " +
                "AND " + ReportOnClause.FromAlias + ".indexStart <= " + ReportOnClause.ToAlias + ".indexStart " +
                "AND " + ReportOnClause.FromAlias + ".indexEnd >= " + ReportOnClause.ToAlias + ".indexEnd");
        ReportForeignKey children = new ReportForeignKey(Children, new TradeTable(), childrenOnClause);
        addOptionalKey(children);
		addOptionalKey(new ReportForeignKey(Parent, new TradeTable(), new ReportOnClause("parentID")));
	}
}