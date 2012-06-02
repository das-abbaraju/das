package com.picsauditing.actions.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
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

		if (permissions.isCorporate()) {
			if (filterOn(getFilter().getWorkStatus()))
				where = "gc.workStatus = '" + getFilter().getWorkStatus() + "'";

			sql.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc "
					+ "JOIN facilities f ON f.opID = gc.genID AND f.corporateID = " + permissions.getAccountId() + ""
					+ " AND " + where + ")");
		}
		if (permissions.isOperator()) {
			sql.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID=" + permissions.getAccountId());
			sql.addField("gc.creationDate as dateAdded");
			sql.addField("gc.workStatus");
		}

		sql.addWhere("c.accountLevel != 'BidOnly'");
		sql.addWhere("a.status IN ('Active','Demo')");
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

	@RequiredPermission(value = OpPerms.ContractorApproval, type = OpType.Edit)
	public String save() {
		OperatorAccount corporate = null;
		if (permissions.isCorporate())
			corporate = operatorAccountDAO.find(permissions.getAccountId());

		if (conids != null && conids.size() > 0) {
			List<ContractorAccount> cAccounts = contractorAccountDAO.findWhere("a.id IN (" + Strings.implode(conids)
					+ ")");
			for (ContractorAccount cAccount : cAccounts) {
				if (permissions.isOperator()) {
					approveContractor(cAccount, permissions.getAccountId(), getWorkStatus());
				}

				if (permissions.isCorporate()) {
					for (Facility facility : corporate.getOperatorFacilities()) {
						approveContractor(cAccount, facility.getOperator().getId(), getWorkStatus());
					}
				}

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
			if (cOperator.getOperatorAccount().getId() == operatorID) {
				cOperator.setWorkStatus(workStatus);
				cOperator.setAuditColumns(permissions);
				contractorOperatorDAO.save(cOperator);
				break;
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
