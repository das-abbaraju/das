package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public class OperatorTags extends OperatorActionSupport {
	private OperatorTagDAO operatorTagDAO;
	
	private List<OperatorTag> tags;
	
	public OperatorTags(OperatorAccountDAO operatorDao, OperatorTagDAO operatorTagDAO) {
		super(operatorDao);
		this.operatorTagDAO = operatorTagDAO;
	}

	@Override
	public String execute() throws Exception {
		tags = operatorTagDAO.findByOperator(this.id);
		return SUCCESS;
	}

	public List<OperatorTag> getTags() {
		return tags;
	}

	public void setTags(List<OperatorTag> tags) {
		this.tags = tags;
	}
	
}
