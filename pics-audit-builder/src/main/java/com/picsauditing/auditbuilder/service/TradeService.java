package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.LowMedHigh;
import com.picsauditing.auditbuilder.entities.Trade;
import com.google.common.base.Objects;

public class TradeService {
	public static LowMedHigh getSafetyRiskI(Trade trade) {
		if (trade.getSafetyRisk() == null) {
			if (Objects.equal(trade.getParent(), Trade.TOP)) {
				return LowMedHigh.Low;
			} else {
				return getSafetyRiskI(trade.getParent());
			}
		}
		return trade.getSafetyRisk();
	}

	public static boolean childOf(Trade child, Trade candidateParent) {
		if (child.getParent() == null) {
			// Gone all the way up and didn't find our parent
			return false;
		}

		if (candidateParent == null) {
			return false;
		}

		if (child.equals(candidateParent)) {
			// We don't consider you to be a child of yourself
			return false;
		}

		// Parent Level,Start,End = 1,1,12
		// Child Level,Start,End = 2,6,9
		return candidateParent.getIndexLevel() < child.getIndexLevel() && candidateParent.getIndexStart() < child.getIndexStart()
				&& candidateParent.getIndexEnd() > child.getIndexEnd();
	}

}
