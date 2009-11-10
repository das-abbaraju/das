package com.picsauditing.actions.operators;

import java.util.List;

import com.picsauditing.util.Strings;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public class OperatorTags extends OperatorActionSupport implements Preparable {
	private OperatorTagDAO operatorTagDAO;

	private List<OperatorTag> tags;

	public OperatorTags(OperatorAccountDAO operatorDao, OperatorTagDAO operatorTagDAO) {
		super(operatorDao);
		this.operatorTagDAO = operatorTagDAO;
		this.subHeading = "Contractor Tags";
	}

	@Override
	public void prepare() throws Exception {
		loadPermissions();
		findOperator();
		tags = operatorTagDAO.findByOperator(id, false);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ContractorTags);

		if ("save".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.ContractorTags, OpType.Edit);
			for (OperatorTag tag : tags) {
				if (tag != null) {
					if (tag.getId() == 0) {
						if (!Strings.isEmpty(tag.getTag())) {
							// Add a new tag
							tag.setActive(true);
							tag.setOperator(operator);
							tag.setAuditColumns(permissions);
							operatorTagDAO.save(tag);
						}
					} else {
						if (Strings.isEmpty(tag.getTag())) {
							addActionError("Tag names cannot be blank");
						} else {
							// Update existing tag
							tag.setAuditColumns(permissions);
							operatorTagDAO.save(tag);
						}
					}
				}
			}
			if (getActionErrors().size() == 0)
				addActionMessage("Successfully saved tag" + (tags.size() > 1 ? "s" : ""));
			tags = operatorTagDAO.findByOperator(id, false);
		}

		return SUCCESS;
	}

	public List<OperatorTag> getTags() {
		return tags;
	}

	public void setTags(List<OperatorTag> tags) {
		this.tags = tags;
	}

}
