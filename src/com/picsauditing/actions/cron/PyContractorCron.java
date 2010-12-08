package com.picsauditing.actions.cron;

import java.util.HashSet;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class PyContractorCron extends PicsActionSupport {

	private ContractorAccountDAO dao;
	private Integer radix = null;

	public PyContractorCron(ContractorAccountDAO dao) {
		this.dao = dao;
	}

	public String execute() {
		List<Integer> ids = dao.findContractorsNeedingRecalculation(15, new HashSet<Integer>(), radix);
		if (ids != null && !ids.isEmpty())
			output = Strings.implode(ids);
		return BLANK;
	}

	public Integer getRadix() {
		return radix;
	}

	public void setRadix(Integer radix) {
		this.radix = radix;
	}

}
