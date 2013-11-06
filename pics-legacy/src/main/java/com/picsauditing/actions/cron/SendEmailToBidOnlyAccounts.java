package com.picsauditing.actions.cron;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class SendEmailToBidOnlyAccounts implements CronTask {
    @Autowired
    ContractorAccountDAO contractorAccountDAO;
    @Autowired
    EmailQueueDAO emailQueueDAO;

    @Override
    public String getDescription() {
        return "Sending No Action Email to Bid Only Accounts";
    }

    @Override
    public List<String> getSteps() {
        List<String> list = new ArrayList<>();
        for (ContractorAccount c : contractorAccountDAO.findBidOnlyContractors())
            list.add(c.getId() + " : " + c.getName());
        return list;
    }

    public CronTaskResult run() {
        CronTaskResult result = new CronTaskResult(true, "success");

        List<ContractorAccount> conList = contractorAccountDAO.findBidOnlyContractors();
        result.getLogger().append("Processing " + conList.size() + " contractors");
        for (ContractorAccount cAccount : conList) {
            try {
                EmailBuilder emailBuilder = new EmailBuilder();

                emailBuilder.setTemplate(EmailTemplate.NO_ACTION_EMAIL_TEMPLATE);
                // No Action Email Notification - Contractor
                emailBuilder.setContractor(cAccount, OpPerms.ContractorAdmin);
                emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
                EmailQueue email = emailBuilder.build();
                email.setLowPriority();
                email.setSubjectViewableById(Account.EVERYONE);
                email.setBodyViewableById(Account.EVERYONE);
                emailQueueDAO.save(email);

                stampNote(cAccount, "No Action Email Notification sent to " + cAccount.getPrimaryContact().getEmail(),
                        NoteCategory.General);
                result.getLogger().append(", " + cAccount.getId());
            } catch (Exception e) {
                result.getLogger().append("\n ERROR with contractorID = " + cAccount.getId() + "\n" + e.getMessage());
            }
        }
        return result;
    }

    private void stampNote(Account account, String text, NoteCategory noteCategory) {
        User system = new User(User.SYSTEM);
        Note note = new Note(account, system, text);
        note.setCanContractorView(true);
        note.setPriority(LowMedHigh.High);
        note.setNoteCategory(noteCategory);
        note.setAuditColumns(system);
        note.setViewableById(Account.PicsID);
        contractorAccountDAO.save(note);
    }


}
