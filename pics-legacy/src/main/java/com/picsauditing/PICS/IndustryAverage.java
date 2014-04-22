package com.picsauditing.PICS;

import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.SpringUtils;

public class IndustryAverage {
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
        NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
        return naicsDAO.getDartIndustryAverage(naics);
    }
}
