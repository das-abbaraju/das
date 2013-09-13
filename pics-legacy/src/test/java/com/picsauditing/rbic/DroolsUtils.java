package com.picsauditing.rbic;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import java.util.Collection;

public class DroolsUtils {
    public static void runDroolsFileWith(String file, Object modelToBind) {
        final KnowledgeBase kbase = createKnowledgeBase(file);
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.insert(modelToBind);

        ksession.fireAllRules();
    }

    public static KnowledgeBase createKnowledgeBase(String filePath) {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newClassPathResource(filePath, RulesRunner.class), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Unable to compile \"" + filePath + "\".");
        }

        final Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(pkgs);
        return kbase;
    }
}
