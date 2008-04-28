package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;

public class AuditBuilder {
	@Autowired
	AuditTypeDAO auditTypeDAO;
	@Autowired
	ContractorAccountDAO contractorDAO;
	@Autowired
	ContractorAuditDAO cAuditDAO;

	public void getAudits(int conID) {
		ContractorAccount contractor = contractorDAO.find(conID);
		
		List<AuditType> list = auditTypeDAO.findWhere("t IN (SELECT auditType " +
				"FROM AuditOperator WHERE operatorAccount IN (" +
					"SELECT operatorAccount FROM ContractorOperator " +
					"WHERE contractorAccount.id = "+conID+
				"))");
		for(AuditType auditType : list) {
			System.out.println("AuditType: "+ auditType.getAuditTypeID() + auditType.getAuditName());
			boolean found = false;
			for(ContractorAudit conAudit : contractor.getAudits()) {
				if (conAudit.getAuditType().getAuditTypeID() == auditType.getAuditTypeID()) {
					if (conAudit.getAuditStatus() != AuditStatus.Expired) {
						found = true;
					}
				}
			}
			if (!found) {
				System.out.println(" is missing");
				ContractorAudit cAudit = new ContractorAudit();
				cAudit.setContractorAccount(contractor);
				cAudit.setAuditType(auditType);
				cAuditDAO.save(cAudit);
			}
		}
	}

}
