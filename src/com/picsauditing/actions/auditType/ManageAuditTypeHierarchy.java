package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ManageAuditTypeHierarchy extends PicsActionSupport {

	private AuditTypeDAO auditTypeDAO;
	private AuditCategoryDAO auditCategoryDAO;
	private AuditQuestionDAO auditQuestionDAO;

	private List<AuditType> auditTypeList;
	private AuditType auditType;
	private int id;

	public ManageAuditTypeHierarchy(AuditTypeDAO auditTypeDAO, AuditCategoryDAO auditCategoryDAO,
			AuditQuestionDAO auditQuestionDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		auditType = auditTypeDAO.find(id);
		return SUCCESS;
	}

	public List<AuditType> getAuditTypeList() {
		if (auditTypeList == null) {
			auditTypeList = auditTypeDAO.findAll();

		}
		return auditTypeList;
	}

	public AuditType getAuditType() {
		return auditType;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
