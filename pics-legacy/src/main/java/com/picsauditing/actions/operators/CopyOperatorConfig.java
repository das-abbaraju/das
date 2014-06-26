package com.picsauditing.actions.operators;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("serial")
public class CopyOperatorConfig extends PicsActionSupport {

    @Autowired
    OperatorAccountDAO operatorAccountDAO;

    private int sourceID;
    private int targetID;

	@Override
	public String execute() throws Exception {
        permissions.tryPermission(OpPerms.ManageOperatorConfig);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);
		return SUCCESS;
	}

    public String copy() throws Exception {
        permissions.tryPermission(OpPerms.ManageOperatorConfig);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);

        operatorAccountDAO.copyAuditTypeRules(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Audit Type Rules");

        operatorAccountDAO.copyAuditCategoryRules(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Audit Category Rules");

        operatorAccountDAO.copyFlagCriteriaOperators(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Operator Flag Criteria");


        return SUCCESS;
    }

    public List<OperatorAccount> getOperatorList() {
        return operatorAccountDAO.findAll(OperatorAccount.class);
    }

    public int getSourceID() {
        return sourceID;
    }

    public void setSourceID(int sourceID) {
        this.sourceID = sourceID;
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
}