package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.InsuranceCriteriaContractorOperator;

import java.util.*;

public class InsuranceCriteriaDisplay {

    public static final Comparator<Integer> REVERSE_ORDER = new Comparator<Integer>() {
        @Override
        public int compare(Integer i1, Integer i2) {
            return i2 - i1;
        }
    };

    public static SortedMap<Integer, List<InsuranceCriteriaContractorOperator>> getInsuranceCriteriaMap(AuditQuestion question, ContractorAccount contractor) {
        SortedMap<Integer, List<InsuranceCriteriaContractorOperator>> results = new TreeMap<>(REVERSE_ORDER);

        for (InsuranceCriteriaContractorOperator criteria : contractor.getInsuranceCriteriaContractorOperators()) {
            if (criteria.getFlagCriteria().getQuestion().equals(question)) {
                List<InsuranceCriteriaContractorOperator> operatorsSet
                        = results.get(criteria.getInsuranceLimit());
                if (operatorsSet == null) {
                    operatorsSet = new ArrayList<>();
                    results.put(criteria.getInsuranceLimit(), operatorsSet);
                }
                operatorsSet.add(criteria);
            }
        }
        return results;
    }

}