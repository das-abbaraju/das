package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class RulesRunner {

    private final Logger logger = LoggerFactory.getLogger(RulesRunner.class);

    private ContractorAccount contractor;

    @Autowired
    private ContractorModel contractorModel;

    public void runInsuranceCriteriaRulesForOperator(OperatorAccount operator) {
        if (!operator.hasRulesBasedInsuranceCriteria()) {
            return;
        }
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        String filePath = operator.getId() + "_rbic.drl";
        kbuilder.add(ResourceFactory.newClassPathResource(filePath, getClass()), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            logger.error(kbuilder.getErrors().toString());
            throw new RuntimeException("Unable to compile \"" + filePath + "\".");
        }

        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(pkgs);

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        contractorModel.setContractor(contractor);
        ksession.insert(contractorModel);

        ksession.fireAllRules();

        ksession.dispose();
    }

    public ContractorAccount getContractor() {
        return contractor;
    }

    public void setContractor(ContractorAccount contractor) {
        this.contractor = contractor;
    }
}