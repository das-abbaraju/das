package com.picsauditing.actions.audits;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.auditBuilder.AuditCategoriesBuilder;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypesBuilder;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;

@SuppressWarnings("serial")
public class AuditBuilderDebugger extends ContractorActionSupport {

	private int catID;
	private Set<AuditTypeDetail> auditTypeDetails;

	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;
	public String execute() throws Exception {
		findContractor();
		AuditTypesBuilder builder = new AuditTypesBuilder(auditTypeRuleCache, contractor);
		auditTypeDetails = builder.calculate();
		builder.getRules();
		return SUCCESS;
	}
	
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

	public Set<AuditTypeDetail> getAuditTypeDetails() {
		return auditTypeDetails;
	}
	
}
