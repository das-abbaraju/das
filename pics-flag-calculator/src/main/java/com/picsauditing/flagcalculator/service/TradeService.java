package com.picsauditing.flagcalculator.service;

import com.picsauditing.flagcalculator.entities.ContractorAccount;
import com.picsauditing.flagcalculator.entities.ContractorTrade;
import com.picsauditing.flagcalculator.entities.Trade;

import java.util.ArrayList;
import java.util.List;

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

    public static Float getNaicsLWCRI(Trade trade) {
        if (trade.getNaicsLWCR() != null && trade.getNaicsLWCR() != 0.0) {
            return trade.getNaicsLWCR();
        } else if (trade.getParent() != null) {
            return getNaicsLWCRI(trade.getParent());
        } else {
            return Float.valueOf(4);
        }
    }

    public static ContractorTrade getTopTrade(ContractorAccount contractorAccount) {
        ContractorTrade topTrade = null;
        List<ContractorTrade> listOfSameTradeActivity = new ArrayList<ContractorTrade>();

        for (ContractorTrade trade : contractorAccount.getTrades()) {
            if (!trade.isSelfPerformed())
                continue;

            if (topTrade == null || trade.getActivityPercent() > topTrade.getActivityPercent()) {
                topTrade = trade;
                listOfSameTradeActivity.clear();
                listOfSameTradeActivity.add(trade);
            } else if (trade.getActivityPercent() == topTrade.getActivityPercent()) {
                listOfSameTradeActivity.add(trade);
            }
        }

        if (listOfSameTradeActivity.size() > 1) {
            topTrade = null;
            for (ContractorTrade trade : listOfSameTradeActivity) {
                if (topTrade == null || getNaicsTRIRI(trade.getTrade()) > getNaicsTRIRI(topTrade.getTrade())) {
                    topTrade = trade;
                }
            }
        }

        return topTrade;
    }
}
