package com.picsauditing.actions.audits;

import java.util.Set;

import com.picsauditing.audits.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.contractors.ContractorActionSupport;

@SuppressWarnings("serial")
public class AuditBuilderDebugger extends ContractorActionSupport {

	private int catID;
	private Set<AuditTypeDetail> auditTypeDetails;

    @Autowired
    private AuditBuilderFactory auditBuilderFactory;

	public String execute() throws Exception {
        if (id == -1) {
            addActionMessage("Change the '-1' in the web address to the contractor ID.");
            return SUCCESS;
        }

		findContractor();
        auditTypeDetails = auditBuilderFactory.getContractorAuditTypeDetails(contractor);
		return SUCCESS;
	}
	
	public String category() {
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
