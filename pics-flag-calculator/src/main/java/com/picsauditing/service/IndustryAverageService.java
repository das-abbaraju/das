package com.picsauditing.service;

import com.picsauditing.model.entities.ContractorAccount;
import com.picsauditing.model.entities.ContractorTrade;
import com.picsauditing.model.entities.Naics;

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

    public static float getDartIndustryAverage(Naics naics) {
//        NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
//        return naicsDAO.getDartIndustryAverage(naics);
        return 4f; //TODO remove
    }
}
