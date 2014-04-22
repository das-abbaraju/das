package com.picsauditing.service;

import com.picsauditing.model.entities.ContractorAccount;
import com.picsauditing.model.entities.ContractorTrade;
import com.picsauditing.model.entities.Trade;

public class TradeService {
    public static float getWeightedIndustryAverage(ContractorAccount contractorAccount) {
        float sum = 0;
        int activitySum = 0;

        if (contractorAccount.getTrades().size() > 0) {
            if (hasSelfPerformedTrades(contractorAccount)) {
                for (ContractorTrade t : contractorAccount.getTrades()) {
                    if (t.isSelfPerformed()) {
                        sum += t.getActivityPercent() * getNaicsTRIRI(t.getTrade());
                        activitySum += t.getActivityPercent();
                    }
                }
            } else {
                for (ContractorTrade t : contractorAccount.getTrades()) {
                    sum += t.getActivityPercent() * getNaicsTRIRI(t.getTrade());
                    activitySum += t.getActivityPercent();
                }
            }
            return sum / activitySum;
        } else {
            return 0;
        }
    }

    public static boolean hasSelfPerformedTrades(ContractorAccount contractorAccount) {
        for (ContractorTrade t : contractorAccount.getTrades()) {
            if (t.isSelfPerformed()) {
                return true;
            }
        }
        return false;
    }

	public static Float getNaicsTRIRI(Trade trade) {
		if (trade.getNaicsTRIR() != null && trade.getNaicsTRIR() != 0.0) {
			return trade.getNaicsTRIR();
		} else if (trade.getParent() != null) {
			return getNaicsTRIRI(trade.getParent());
		} else {
			return Float.valueOf(4);
		}
	}
}
