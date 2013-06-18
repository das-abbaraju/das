package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorTradeTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Trade = "Trade";
	
	public ContractorTradeTable() {
		super("contractor_trade");
		addFields(ContractorTrade.class);

        Field tradeName = new Field("TradeName","tradeID", FieldType.Trade);
        tradeName.setTranslationPrefixAndSuffix("Trade","name");
        tradeName.setImportance(FieldImportance.Required);
        tradeName.setCategory(FieldCategory.AccountInformation);
        tradeName.setVisible(false);
        addField(tradeName);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Contractor, new AccountTable(), new ReportOnClause("conID", "id",
				ReportOnClause.ToAlias + ".type = 'Contractor'")));

		addRequiredKey(new ReportForeignKey(Trade, new TradeTable(), new ReportOnClause("tradeID")));
	}
}