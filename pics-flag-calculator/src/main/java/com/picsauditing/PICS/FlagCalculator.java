package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.*;

import java.util.*;

/**
 * Created by kchase on 4/18/14.
 */
public class FlagCalculator {

    private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;

    public List<FlagData> calculate() {
//        flagCriteriaDao = flagCriteriaDao();
//        dao = basicDAO();

        Map<FlagCriteria, FlagData> dataSet = new HashMap<FlagCriteria, FlagData>();

//        boolean flaggable = isFlaggableContractor();
//        for (FlagCriteria key : operatorCriteria.keySet()) {
//            for (FlagCriteriaOperator fco : operatorCriteria.get(key)) {
//                FlagColor flag = FlagColor.Green;
//                if (flaggable && contractorCriteria.containsKey(key)) {
//                    Boolean flagged = isFlagged(fco, contractorCriteria.get(key));
//                    if (flagged != null) {
//                        if (overrides != null) {
//                            FlagDataOverride override = hasForceDataFlag(key, operator);
//                            if (override != null) {
//                                flag = override.getForceflag();
//                            } else if (flagged) {
//                                flag = fco.getFlag();
//                            }
//                        } else if (flagged) {
//                            flag = fco.getFlag();
//                        }
//
//                        FlagData data = new FlagData();
//                        data.setCriteria(key);
//                        data.setContractor(contractorCriteria.get(key).getContractor());
//                        data.setCriteriaContractor(contractorCriteria.get(key));
//                        data.setOperator(operator);
//                        data.setFlag(flag);
//                        data.setAuditColumns(new User(User.SYSTEM));
//
//						/*
//						 * This logic is intended, if the criteria is an AU then
//						 * we only add if the account is full and not a sole
//						 * proprietor
//						 */
//                        if (data.getCriteria().getAuditType() != null
//                                && !data.getCriteria().getAuditType().isAnnualAddendum()
//                                || (data.getContractor().getAccountLevel().isFull() && !data.getContractor()
//                                .getSoleProprietor())) {
//                            if (dataSet.get(key) == null) {
//                                dataSet.put(key, data);
//                            } else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
//                                dataSet.put(key, data);
//                            }
//                        } else if (data.getContractor().getAccountLevel().isFull()) {
//                            if (dataSet.get(key) == null) {
//                                dataSet.put(key, data);
//                            } else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
//                                dataSet.put(key, data);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        return new ArrayList<FlagData>(dataSet.values());
    }

    private boolean isFlaggableContractor() {
        if (contractorCriteria.size() == 0)
            return false;
        Collection<FlagCriteriaContractor> criteriaList = contractorCriteria.values();
        if (criteriaList.size() == 0)
            return false;
        ContractorAccount contractor = criteriaList.iterator().next().getContractor();

        if (contractor == null) {
            return false;
        }

        if (contractor.getStatus().equals(AccountStatus.Pending) ||
                contractor.getStatus().equals(AccountStatus.Deactivated) ||
                contractor.getStatus().equals(AccountStatus.Requested)) {
            return false;
        }
        return true;
    }

    private void setContractorCriteria(Collection<FlagCriteriaContractor> list) {
        contractorCriteria = new HashMap<>();
        for (FlagCriteriaContractor value : list) {
            contractorCriteria.put(value.getCriteria(), value);
        }
    }

}
