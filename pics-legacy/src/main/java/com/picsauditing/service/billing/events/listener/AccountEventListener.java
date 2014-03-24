package com.picsauditing.service.billing.events.listener;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.account.events.ContractorEventType;
import com.picsauditing.service.account.events.SpringContractorEvent;
import com.picsauditing.util.SapAppPropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class AccountEventListener implements ApplicationListener<SpringContractorEvent> {

    @Autowired
    private ContractorAccountDAO dao;

    private SapAppPropertyUtil SAPProperty = SapAppPropertyUtil.factory();

    @Override
    public void onApplicationEvent(SpringContractorEvent event) {
        if (event.getEvent().equals(ContractorEventType.Registration)) {
            ContractorAccount registrant = event.getContractor();
            if (!registrant.isDemo()) {
                registrant.setQbSync(true);
                if (SAPProperty.isSAPBusinessUnitEnabledForObject(registrant))
                    registrant.setSapSync(true);
                dao.save(registrant);
            }
        }
    }
}