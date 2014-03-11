package com.picsauditing.service.account;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.account.events.ContractorEventPublisher;
import com.picsauditing.service.account.events.ContractorEventType;

public class AccountService {

    private final ContractorEventPublisher eventPublisher;
    private final ContractorAccountDAO accountDAO;

    public AccountService(
            ContractorEventPublisher publisher,
            ContractorAccountDAO accountDAO
    ) {
        this.eventPublisher = publisher;
        this.accountDAO = accountDAO;
    }

    public void publishEvent(ContractorAccount account, ContractorEventType type) {
        eventPublisher.publishEvent(account, type);
    }

    public void persist(ContractorAccount account) {
        accountDAO.save(account);
        if (account.getId() == 0) {
            accountDAO.refresh(account);
        }
    }
}
