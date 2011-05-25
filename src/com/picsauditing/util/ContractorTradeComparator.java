package com.picsauditing.util;

import java.util.Comparator;

import com.picsauditing.jpa.entities.ContractorTrade;

public class ContractorTradeComparator  implements Comparator<ContractorTrade>{
	@Override
	public int compare(ContractorTrade o1, ContractorTrade o2) {
		if (o1 == null || o2 == null)
			return 0;
		return o1.getTrade().getName().toString().compareTo(o2.getTrade().getName().toString());
	}

}
