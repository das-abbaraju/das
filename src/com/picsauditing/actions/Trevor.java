package com.picsauditing.actions;

import java.util.Iterator;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagData;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {

	private int id = 0;
	private ContractorOperatorDAO contractorOperatorDAO;
	private ContractorOperator co;

	public Trevor(ContractorOperatorDAO contractorOperatorDAO) {
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	@Override
	public String execute() {
		co = contractorOperatorDAO.find(id);

		if (button != null) {
//			Iterator<FlagData> iterator = co.getFlagDatas().iterator();
//			while(iterator.hasNext()) {
//				FlagData next = iterator.next();
//				co.getFlagDatas().remove(next);
//				contractorOperatorDAO.remove(next);
//				iterator.remove();
//			}
			contractorOperatorDAO.remove(co);
		}
		return SUCCESS;
	}

	public ContractorOperator getCo() {
		return co;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
