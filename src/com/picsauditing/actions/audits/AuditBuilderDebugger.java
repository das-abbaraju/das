package com.picsauditing.actions.audits;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;

@SuppressWarnings("serial")
public class AuditBuilderDebugger extends ContractorActionSupport {

	private int catID;

	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;

	public String category() {

		AuditCategoriesBuilder categoriesBuilder = new AuditCategoriesBuilder(auditCategoryRuleCache, contractor);
		// categoriesBuilder.calculate(conAudit);
		return "category";
	}

	public int getCatID() {
		return catID;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}

}
