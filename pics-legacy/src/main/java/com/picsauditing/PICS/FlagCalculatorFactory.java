package com.picsauditing.PICS;

import com.picsauditing.featuretoggle.Features;
import com.picsauditing.flagcalculator.FlagCalculator;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.messaging.MessagePublisherService;
import com.picsauditing.util.SpringUtils;

import javax.persistence.EntityManager;
import java.util.*;

public class FlagCalculatorFactory {

    private static EntityManager entityManager;

    public static FlagCalculator flagCalculator(ContractorOperator co,MessagePublisherService messagePublisherService) {
        Map<FlagCriteria, List<FlagDataOverride>> overrides = calculateOverrides(co);

        if (newFlagCalculatorIsEnabled()) {
            com.picsauditing.flagcalculator.FlagDataCalculator.setEntityManager(entityManager());
            FlagCalculator flagCalculator = new com.picsauditing.flagcalculator.FlagDataCalculator(co.getId(), convertOverridesToIDMap(overrides));
            return flagCalculator;
        } else {
            FlagDataCalculator flagDataCalculator =  new FlagDataCalculator(co.getContractorAccount().getFlagCriteria(), overrides);
            flagDataCalculator.setOperator(co.getOperatorAccount());
            flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());
            flagDataCalculator.setContractorOperator(co);
            flagDataCalculator.setMessagePublisherService(messagePublisherService);
            return flagDataCalculator;
        }
    }

//    public static FlagCalculator flagCalculator(ContractorOperator co, Map<FlagCriteria, List<FlagDataOverride>> overrides) {
//        if (newFlagCalculatorIsEnabled()) {
//
//            com.picsauditing.flagcalculator.FlagDataCalculator.setEntityManager(entityManager());
//            com.picsauditing.flagcalculator.FlagDataCalculator flagCalculator = new com.picsauditing.flagcalculator.FlagDataCalculator(co.getId());
//            flagCalculator.setOverrides(overrides);
//            return flagCalculator;
//        } else {
//            FlagDataCalculator flagDataCalculator=  new FlagDataCalculator(co.getContractorAccount().getFlagCriteria());
//            flagDataCalculator.setOperator(co.getOperatorAccount());
//            flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());
//            flagDataCalculator.setOverrides(overrides);
//            return flagDataCalculator;
//        }
//    }
//
    private static Map<Integer, List<Integer>> convertOverridesToIDMap(Map<FlagCriteria, List<FlagDataOverride>> overrides) {
        Map<Integer, List<Integer>> overrideIDMap = new HashMap<>();
        for (FlagCriteria flagCriteria : overrides.keySet()) {
            List<FlagDataOverride> flagDataOverrides = overrides.get(flagCriteria);
            List<Integer> flagDataOverrideIds = new ArrayList<>();
            for (FlagDataOverride flagDataOverride : flagDataOverrides) {
                flagDataOverrideIds.add(flagDataOverride.getId());
            }
            overrideIDMap.put(flagCriteria.getId(), flagDataOverrideIds);
        }
        return overrideIDMap;
    }

    private static Map<FlagCriteria, List<FlagDataOverride>> calculateOverrides(ContractorOperator co) {
        Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<>();

        Set<OperatorAccount> corporates = new HashSet<>();
        for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
            corporates.add(f.getCorporate());
        }

        Iterator<FlagDataOverride> itr = co.getContractorAccount().getFlagDataOverrides().iterator();
        while (itr.hasNext()) {
            FlagDataOverride override = itr.next();
            if (override.getOperator().equals(co.getOperatorAccount())) {
                if (!overridesMap.containsKey(override.getCriteria())) {
                    overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
                }
                ((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addFirst(override);
            } else if (corporates.contains(override.getOperator())) {
                if (!overridesMap.containsKey(override.getCriteria())) {
                    overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
                }
                ((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addLast(override);
            }
        }

        return overridesMap;
    }

    private static boolean newFlagCalculatorIsEnabled() {
        try {
            return Features.USE_NEW_FLAGCALCULATOR.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private static EntityManager entityManager() {
        if (entityManager == null)
            return SpringUtils.getBean("entityManagerFactory");
        else
            return entityManager;
    }

    public static void setEntityManager(EntityManager entityManager) {
        FlagCalculatorFactory.entityManager = entityManager;
    }
}
