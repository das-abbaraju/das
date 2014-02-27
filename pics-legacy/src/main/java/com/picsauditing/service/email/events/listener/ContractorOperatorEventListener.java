package com.picsauditing.service.email.events.listener;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import org.springframework.context.ApplicationListener;

public class ContractorOperatorEventListener  implements ApplicationListener<SpringContractorOperatorEvent> {

    @Override
    public void onApplicationEvent(SpringContractorOperatorEvent event) {
        ContractorOperator contractorOperator = event.getContractorOperator();
    }
}
