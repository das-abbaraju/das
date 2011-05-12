package com.picsauditing.actions.audits;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.AuditBuilder.AuditCategoriesDetail;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class AuditBuilderDebugger extends ContractorActionSupport {

	private AuditBuilderController builder;

	public AuditBuilderDebugger(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditBuilderController auditBuilder) {
		this.builder = auditBuilder;
	}

	public String execute() throws Exception {
		findContractor();

		builder.setup(this.contractor, getUser());

		return SUCCESS;
	}

	public AuditBuilderController getBuilder() {
		return builder;
	}

	public Map<ContractorAudit, AuditCategoriesDetail> getAuditCategoriesDetail() {
		Map<ContractorAudit, AuditCategoriesDetail> list = new HashMap<ContractorAudit, AuditCategoriesDetail>();
		for (ContractorAudit conAudit : contractor.getAudits()) {
			if (!conAudit.isExpired()) {
				list.put(conAudit, builder.getAuditCategoryDetail(conAudit));
			}
		}
		return list;
	}
}
