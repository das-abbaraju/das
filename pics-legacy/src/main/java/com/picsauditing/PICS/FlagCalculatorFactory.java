package com.picsauditing.PICS;

import com.picsauditing.featuretoggle.Features;
import com.picsauditing.flagcalculator.FlagCalculator;
import com.picsauditing.flagcalculator.dao.FlagCalculatorDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SpringUtils;

import javax.persistence.EntityManager;
import java.util.*;

public class FlagCalculatorFactory {

    public static FlagCalculator flagCalculator(ContractorOperator co) {
        if (newFlagCalculatorIsEnabled()) {
            com.picsauditing.flagcalculator.FlagDataCalculator.setEntityManager(entityManager());
            FlagCalculator flagCalculator = new com.picsauditing.flagcalculator.FlagDataCalculator(co.getId());
            return flagCalculator;
        } else {
            FlagDataCalculator flagDataCalculator =  new FlagDataCalculator(co.getContractorAccount().getFlagCriteria());
            flagDataCalculator.setOperator(co.getOperatorAccount());
            flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());
            return flagDataCalculator;
        }
    }

    public static FlagCalculator flagCalculator(ContractorOperator co, Map<FlagCriteria, List<FlagDataOverride>> overrides) {
        if (newFlagCalculatorIsEnabled()) {
            com.picsauditing.flagcalculator.FlagDataCalculator.setEntityManager(entityManager());
            com.picsauditing.flagcalculator.FlagDataCalculator flagCalculator = new com.picsauditing.flagcalculator.FlagDataCalculator(co.getId());
            flagCalculator.setOverrides(overrides);
            return flagCalculator;
        } else {
            FlagDataCalculator flagDataCalculator=  new FlagDataCalculator(co.getContractorAccount().getFlagCriteria());
            flagDataCalculator.setOperator(co.getOperatorAccount());
            flagDataCalculator.setOperatorCriteria(co.getOperatorAccount().getFlagCriteriaInherited());
            flagDataCalculator.setOverrides(overrides);
            return flagDataCalculator;
        }
    }

    private Map<Integer, List<Integer>> convertOverridesToIDMap(Map<FlagCriteria, List<FlagDataOverride>> overrides) {
        Map<Integer, List<Integer>> overrideIDMap = new HashMap<>();
        return overrideIDMap;
    }
//    private static Map<FlagCriteria, List<FlagDataOverride>> calculateOverrides(ContractorOperator co) {
//        Map<FlagCriteria, List<FlagDataOverride>> overridesMap = new HashMap<FlagCriteria, List<FlagDataOverride>>();
//
//        Set<OperatorAccount> corporates = new HashSet<OperatorAccount>();
//        for (Facility f : co.getOperatorAccount().getCorporateFacilities()) {
//            corporates.add(f.getCorporate());
//        }
//
//        Iterator<FlagDataOverride> itr = co.getContractorAccount().getFlagDataOverrides().iterator();
//        while (itr.hasNext()) {
//            FlagDataOverride override = itr.next();
//            if (override.getOperator().equals(co.getOperatorAccount())) {
//                if (!overridesMap.containsKey(override.getCriteria())) {
//                    overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
//                }
//                ((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addFirst(override);
//            } else if (corporates.contains(override.getOperator())) {
//                if (!overridesMap.containsKey(override.getCriteria())) {
//                    overridesMap.put(override.getCriteria(), new LinkedList<FlagDataOverride>());
//                }
//                ((LinkedList<FlagDataOverride>) overridesMap.get(override.getCriteria())).addLast(override);
//            }
//        }
//
//        return overridesMap;
//    }

    private static boolean newFlagCalculatorIsEnabled() {
        try {
            return Features.USE_NEW_FLAGCALCULATOR.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private static FlagCalculatorDAO flagCalculatorDAO() {
        return SpringUtils.getBean("AppPropertyDAO");
    }

    private static EntityManager entityManager() {
        return SpringUtils.getBean("entityManagerFactory");
    }

}
