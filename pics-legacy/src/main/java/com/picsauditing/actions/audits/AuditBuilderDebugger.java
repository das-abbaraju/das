package com.picsauditing.actions.audits;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.audits.AuditCategoriesBuilder;
import com.picsauditing.audits.AuditCategoryRuleCache;
import com.picsauditing.audits.AuditTypesBuilder;
import com.picsauditing.audits.AuditTypesBuilder.AuditTypeDetail;

@SuppressWarnings("serial")
public class AuditBuilderDebugger extends ContractorActionSupport {

	private int catID;
	private Set<AuditTypeDetail> auditTypeDetails;

	@Autowired
	private AuditCategoryRuleCache auditCategoryRuleCache;
	public String execute() throws Exception {
        if (id == -1) {
            addActionMessage("Change the '-1' in the web address to the contractor ID.");
            return SUCCESS;
        }

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
