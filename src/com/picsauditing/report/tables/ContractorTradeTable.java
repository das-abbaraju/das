package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorTrade;

public class ContractorTradeTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Trade = "Trade";
	
	public ContractorTradeTable() {
		super("contractor_trade");
		addFields(ContractorTrade.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Contractor, new AccountTable(), new ReportOnClause("conID", "id",
				ReportOnClause.ToAlias + ".type = 'Contractor'")));

		addRequiredKey(new ReportForeignKey(Trade, new TradeTable(), new ReportOnClause("tradeID")));
	}
}