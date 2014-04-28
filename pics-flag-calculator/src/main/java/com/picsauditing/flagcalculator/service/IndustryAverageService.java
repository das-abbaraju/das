package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.dao.FlagCalculatorDAO;
import com.picsauditing.flagcalculator.entities.ContractorAccount;
import com.picsauditing.flagcalculator.entities.ContractorTrade;
import com.picsauditing.flagcalculator.entities.Naics;

public class IndustryAverageService {
    public static float getTrirIndustryAverage(ContractorAccount contractor) {
        float answer = 0f;
        ContractorTrade trade = contractor.getTopTrade();

        if (trade == null) {
            return answer;
        }

        answer = trade.getTrade().getNaicsTRIRI();

        return answer;
    }

    public static float getLwcrIndustryAverage(ContractorAccount contractor) {
        float answer = 0f;
        ContractorTrade trade = contractor.getTopTrade();

        if (trade == null) {
            return answer;
        }

        answer = trade.getTrade().getNaicsLWCRI();

        return answer;
    }

    public static float getDartIndustryAverage(Naics naics, FlagCalculatorDAO flagCalculatorDAO) {
        return flagCalculatorDAO.getDartIndustryAverage(naics);
    }
}
