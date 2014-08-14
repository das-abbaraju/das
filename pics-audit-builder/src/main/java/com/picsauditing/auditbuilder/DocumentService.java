package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.dao.AuditCategoryMatrixDAO2;
import com.picsauditing.auditbuilder.entities.ContractorAccount;
import org.springframework.beans.factory.annotation.Autowired;

public class DocumentService {
    @Autowired
    private com.picsauditing.auditbuilder.dao.AuditDataDAO2 auditDataDao;
    @Autowired
    private AuditTypeRuleCache2 typeRuleCache2;

    public DocumentTypesBuilder documentTypesBuilder(int contractorId) {
        ContractorAccount contractor = auditDataDao.find(ContractorAccount.class, contractorId);
        AuditTypesBuilder documentTypesBuilder = new AuditTypesBuilder();
        documentTypesBuilder.setRuleCache(typeRuleCache2);
        documentTypesBuilder.setContractor(contractor);

        return documentTypesBuilder;
    }
}
