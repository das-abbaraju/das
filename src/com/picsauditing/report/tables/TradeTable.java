package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Trade;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class TradeTable extends AbstractTable {

	public static final String Trade = "Parent";

	public TradeTable() {
		super("ref_trade");
		addFields(Trade.class);
		
		Field trade = new Field("Name","id",FieldType.Trade);
		trade.setTranslationPrefixAndSuffix("Trade", "name");
		trade.setCategory(FieldCategory.Classification);
		trade.setImportance(FieldImportance.Required);
		addField(trade);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(Trade, new TradeTable(), new ReportOnClause("parentID")));
	}
}