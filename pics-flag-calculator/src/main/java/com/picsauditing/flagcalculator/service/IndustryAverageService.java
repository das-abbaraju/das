package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.dao.FlagCalculatorDAO;
import com.picsauditing.flagcalculator.entities.ContractorAccount;
import com.picsauditing.flagcalculator.entities.ContractorTrade;
import com.picsauditing.flagcalculator.entities.Naics;

public class IndustryAverageService {
    public static float getTrirIndustryAverage(ContractorAccount contractor) {
        float answer = 0f;
        ContractorTrade trade = TradeService.getRandomTopTrade(contractor);

        if (trade == null) {
            return answer;
        }

        answer = TradeService.getNaicsTRIRI(trade.getTrade());

        return answer;
    }

    public static float getLwcrIndustryAverage(ContractorAccount contractor) {
        float answer = 0f;
        ContractorTrade trade = TradeService.getRandomTopTrade(contractor);

        if (trade == null) {
            return answer;
        }

        answer = TradeService.getNaicsLWCRI(trade.getTrade());

        return answer;
    }

    public static float getDartIndustryAverage(Naics naics, FlagCalculatorDAO flagCalculatorDAO) {
        return flagCalculatorDAO.getDartIndustryAverage(naics);
    }
}
