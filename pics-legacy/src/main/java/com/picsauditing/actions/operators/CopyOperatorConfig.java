package com.picsauditing.actions.operators;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@SuppressWarnings("serial")
public class CopyOperatorConfig extends PicsActionSupport {

    @Autowired
    OperatorAccountDAO operatorAccountDAO;

    private int sourceID;
    private int targetID;

    private boolean isCopyParents = false;

	@Override
	public String execute() throws Exception {
        permissions.tryPermission(OpPerms.ManageOperatorConfig);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);
		return SUCCESS;
	}

    public String copy() throws Exception {
        permissions.tryPermission(OpPerms.ManageOperatorConfig);
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);

        copyOperator(sourceID);

        if (isCopyParents) {
            copyOperatorParents();
        }

        return SUCCESS;
    }

    private void copyOperator(int sourceID) {
        operatorAccountDAO.copyAuditTypeRules(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Audit Type Rules");

        operatorAccountDAO.copyAuditCategoryRules(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Audit Category Rules");

        operatorAccountDAO.copyFlagCriteriaOperators(sourceID, targetID, permissions.getUserId());
        addActionMessage("Copied Operator Flag Criteria");
    }

    private void copyOperatorParents() {
        OperatorAccount source = operatorAccountDAO.find(sourceID);
        List<Facility> facilities = source.getCorporateFacilities();
        for (Facility facility : facilities) {
            copyOperator(facility.getCorporate().getId());
        }
    }

    public List<OperatorAccount> getOperatorList() {
        return operatorAccountDAO.findWhere(OperatorAccount.class, "1=1", 0, "t.name");
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

    public boolean isCopyParents() {
        return isCopyParents;
    }

    public void setCopyParents(boolean isCopyParents) {
        this.isCopyParents = isCopyParents;
    }
}