package com.picsauditing.actions.dev;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.validator.VATValidator;

import com.picsauditing.validator.ValidationException;
import edu.emory.mathcs.backport.java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ContractorVATMigration extends ActionSupport{
private static final Integer[] VAT_AUDITQUESTION_IDS = {11111, 10459, 11072, 12631, 12632};

    @Autowired
    private ContractorAccountDAO accountDAO;
    @Autowired
    private AuditDataDAO auditDataDAO;

    private VATValidator validator = new VATValidator();

    @SuppressWarnings("unchecked")
	@Anonymous
    public String execute() {
        List<AuditData> questionList = auditDataDAO.findByQuestionIDs(Arrays.asList(VAT_AUDITQUESTION_IDS));
        for (AuditData data : questionList) {
            migrate(data);
        }
        return SUCCESS;
    }

    private void migrate(AuditData data) {
        if (data.isAnswered()) {
            try {
                String vat = vatFrom(data);
                ContractorAccount contractor = data.getAudit().getContractorAccount();
                contractor.setVatId(vat);
                accountDAO.save(contractor);
            } catch (ValidationException not_valid_vat) {
                // Do nothing.
            }
        }
    }

    private String vatFrom(AuditData data) throws ValidationException {
        return validator.validatedVATfromAudit(data);
    }
}
