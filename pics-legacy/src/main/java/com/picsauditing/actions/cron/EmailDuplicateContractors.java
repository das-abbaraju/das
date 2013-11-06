package com.picsauditing.actions.cron;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

public class EmailDuplicateContractors {

    private ContractorAccountDAO contractorAccountDAO;
    private EmailQueueDAO emailQueueDAO;

    public EmailDuplicateContractors(ContractorAccountDAO contractorAccountDAO, EmailQueueDAO emailQueueDAO){
        this.contractorAccountDAO = contractorAccountDAO;
        this.emailQueueDAO = emailQueueDAO;
    }

    public boolean duplicationCheck(BaseTable contractor, String nameIndex) throws IOException, EmailBuildErrorException {
        List<ContractorAccount> duplicateContractors = contractorAccountDAO
                .findWhere(whereDuplicateNameIndex(nameIndex));

        if (CollectionUtils.isEmpty(duplicateContractors)) {

            EmailBuilder emailBuilder = new EmailBuilder();

            emailBuilder.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);
            emailBuilder.setToAddresses(EmailAddressUtils.PICS_REGISTRATION_EMAIL_ADDRESS);
            emailBuilder.addToken("contractor", contractor);
            emailBuilder.addToken("duplicates", duplicateContractors);
            emailBuilder.addToken("type", "Pending Account");
            emailBuilder.setTemplate(EmailTemplate.POSSIBLE_DUPLICATE_EMAIL_TEMPLATE);

            EmailQueue email = emailBuilder.build();
            email.setLowPriority();
            email.setSubjectViewableById(Account.EVERYONE);
            email.setBodyViewableById(Account.EVERYONE);
            emailQueueDAO.save(email);
            return true;
        }
        return false;
    }

    private String whereDuplicateNameIndex(String name) {
        return "a.status = 'Active' "
                + "AND LENGTH(REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','')) > LENGTH(REPLACE(REPLACE(REPLACE('"
                + StringUtils.defaultIfEmpty(name, "")
                + "','CORP',''),'INC',''),'LTD',''))-3 "
                + "AND LENGTH(REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','')) < LENGTH(REPLACE(REPLACE(REPLACE('"
                + StringUtils.defaultIfEmpty(name, "")
                + "','CORP',''),'INC',''),'LTD',''))+3 "
                + "AND (REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD','') LIKE CONCAT('%',REPLACE(REPLACE(REPLACE('"
                + StringUtils.defaultIfEmpty(name, "")
                + "','CORP',''),'INC',''),'LTD',''),'%') "
                + "OR REPLACE(REPLACE(REPLACE('"
                + StringUtils.defaultIfEmpty(name, "")
                + "','CORP',''),'INC',''),'LTD','') LIKE CONCAT('%',REPLACE(REPLACE(REPLACE(a.nameIndex,'CORP',''),'INC',''),'LTD',''),'%'))";
    }


}
