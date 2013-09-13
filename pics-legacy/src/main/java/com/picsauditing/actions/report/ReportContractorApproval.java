package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.jpa.entities.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.util.Strings;
import com.picsauditing.util.excel.ExcelCellType;
import com.picsauditing.util.excel.ExcelColumn;

@SuppressWarnings("serial")
public class ReportContractorApproval extends ReportAccount {
	@Autowired
	protected ContractorAccountDAO contractorAccountDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected OperatorAccountDAO operatorAccountDAO;
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDAO;

	protected List<Integer> conids = null;
	protected String operatorNotes = "";
	protected ApprovalStatus workStatus = ApprovalStatus.P;

	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
        String where = "1";

        if (filterOn(getFilter().getWorkStatus()))
            where = "gc.workStatus LIKE '" + getFilter().getWorkStatus() + "%'";

        if (permissions.isOperatorCorporate()) {
            sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID=" + permissions.getAccountId());
            sql.addField("gc.creationDate as dateAdded");
            sql.addField("gc.flag");
            sql.addField("gc.workStatus");

            sql.addWhere(where);
        }

		sql.addWhere("c.accountLevel != 'BidOnly'");

        addContractorStatus();

        orderByDefault = "a.creationDate";

		filteredDefault = true;
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowFlagStatus(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowWorkStatus(true);
		getFilter().setShowLocation(false);
		getFilter().setWorkStatus(ApprovalStatus.P);
	}

    private void addContractorStatus() {
        OperatorAccount usersAccount = operatorAccountDAO.find(permissions.getAccountId());
        if (usersAccount != null && usersAccount.isDemo()) {
		    sql.addWhere("a.status IN ('Active','Demo')");
        } else {
            sql.addWhere("a.status IN ('Active')");
        }
    }

    @RequiredPermission(value = OpPerms.ContractorApproval, type = OpType.Edit)
	public String save() {
		if (conids != null && conids.size() > 0) {
			List<ContractorAccount> cAccounts = contractorAccountDAO.findWhere("a.id IN (" + Strings.implode(conids)
					+ ")");
			for (ContractorAccount cAccount : cAccounts) {
				approveContractor(cAccount, permissions.getAccountId(), getWorkStatus());

				cAccount.incrementRecalculation();
				cAccount.setAuditColumns(permissions);
				contractorAccountDAO.save(cAccount);

				String summary = "Changed workStatus to " + getWorkStatusDesc(getWorkStatus()) + " for "
						+ permissions.getAccountName();
				Note note = new Note(cAccount, getUser(), summary);
				if (!Strings.isEmpty(operatorNotes)) {
					note.setBody(operatorNotes);
				}
				note.setNoteCategory(NoteCategory.OperatorChanges);
				note.setCanContractorView(true);
				note.setViewableById(permissions.getAccountId());
				noteDAO.save(note);
			}
		}

		operatorNotes = "";
		return BLANK;
	}

	public List<Integer> getConids() {
		return conids;
	}

	public void setConids(List<Integer> conids) {
		this.conids = conids;
	}

	public String getOperatorNotes() {
		return operatorNotes;
	}

	public void setOperatorNotes(String operatorNotes) {
		this.operatorNotes = operatorNotes;
	}

	public ApprovalStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(ApprovalStatus workStatus) {
		this.workStatus = workStatus;
	}

	public void approveContractor(ContractorAccount cAccount, int operatorID, ApprovalStatus workStatus) {
		for (ContractorOperator cOperator : cAccount.getOperators()) {
            OperatorAccount operator = cOperator.getOperatorAccount();
			if (cOperator.getOperatorAccount().getId() == operatorID) {
                if (cOperator.getWorkStatus() != workStatus) {
				    if (operator.isOperator()) {
                        cOperator.setWorkStatus(workStatus);
                        cOperator.cascadeWorkStatusToParent();
                    } else {
                        cOperator.setForcedWorkStatus(workStatus);
                   }

                   if (operator.isCorporate()) {
                        for (OperatorAccount childAccount: operator.getChildOperators()) {
                             approveContractor(cAccount, childAccount.getId(), workStatus);
                        }
                   }
                    cOperator.setAuditColumns(permissions);
                    contractorOperatorDAO.save(cOperator);
                    break;
                 }
			}
		}
	}

    public String getWorkStatusDesc(ApprovalStatus workStatus) {
		return getText(workStatus.getI18nKey());
	}

	public boolean isOperatorCanChangeWorkStatus(ApprovalStatus workStatus) {
		return workStatus != ApprovalStatus.C && workStatus != ApprovalStatus.D;
	}

	protected void addExcelColumns() {
		super.addExcelColumns();

		if (permissions.isOperator()) {
			excelSheet.addColumn(new ExcelColumn("creationDate", "Date Added", ExcelCellType.Date), 400);
			excelSheet.addColumn(new ExcelColumn("workStatus", "Work Status", ExcelCellType.Enum));
		}
	}
}
