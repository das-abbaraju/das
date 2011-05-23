package com.picsauditing.actions.auditType;

public enum AuditRuleColumn {
	id("id", 1),
	Include("Include", 1),
	Priority("Priority", 1),
	AuditType("Audit Type", 1),
	Category("Category", 1),
	RootCategory("Root Category", 1),
	ContractorType("Contractor Type", 1),
	Operator("Operator", 1),
	SafetyRisk("Safety Risk", 1),
	ProductRisk("Product Risk", 1),
	Tag("Tag", 1),
	BidOnly("List Only", 1),
	DependentAudit("Dependent Audit", 2),
	Question("Question", 3),
	Trade("Trade", 1),
	SoleProprietor("Sole Proprietor", 1),
	CreatedBy("Created By", 2),
	UpdatedBy("Updated By", 2);

	private String name;
	private int colspan;

	AuditRuleColumn(String name, int colspan) {
		this.name = name;
		this.colspan = colspan;
	}

	public String getName() {
		return name;
	}

	public int getColspan() {
		return colspan;
	}
}
