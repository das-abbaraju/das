package com.picsauditing.actions.report;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportContractorApproval extends ReportAccount {
	protected List<Integer> conids = null;
	protected String operatorNotes = "";
	protected String workStatus = "";

	protected ContractorAccountDAO contractorAccountDAO;
	protected NoteDAO noteDAO;
	protected OperatorAccountDAO operatorAccountDAO;
	protected ContractorOperatorDAO contractorOperatorDAO;

	public ReportContractorApproval(ContractorAccountDAO contractorAccountDAO,
			NoteDAO noteDAO, OperatorAccountDAO operatorAccountDAO,
			ContractorOperatorDAO contractorOperatorDAO) {
		this.contractorAccountDAO = contractorAccountDAO;
		this.noteDAO = noteDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		String where = "1";

		if (permissions.isCorporate()) {
			if (filterOn(getFilter().getWorkStatus()))
				where = "gc.workStatus = '" + getFilter().getWorkStatus() + "'";

			sql
					.addWhere("a.id IN (SELECT gc.subID FROM generalcontractors gc "
							+ "JOIN facilities f ON f.opID = gc.genID AND f.corporateID = "
							+ permissions.getAccountId()
							+ ""
							+ " AND "
							+ where
							+ ")");
		}
		if (permissions.isOperator()) {
			sql
					.addJoin("JOIN generalcontractors gc ON gc.subID = a.id AND gc.genID="
							+ permissions.getAccountId());
			sql.addField("gc.creationDate as dateAdded");
			sql.addField("gc.workStatus");
		}

		sql.addWhere("a.acceptsBids = 0");
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addOrderBy("a.creationDate");

		filteredDefault = true;
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowFlagStatus(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowWorkStatus(true);
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (button != null) {
			if ("Save".equals(button)) {
				permissions.hasPermission(OpPerms.ContractorApproval,
						OpType.Edit);
				if (conids != null && conids.size() > 0) {
					for (Integer i : conids) {
						ContractorAccount cAccount = contractorAccountDAO
								.find(i);
						if (permissions.isOperator()) {
							approveContractor(cAccount, permissions
									.getAccountId(), getWorkStatus());
						}

						if (permissions.isCorporate()) {
							OperatorAccount corporate = operatorAccountDAO
									.find(permissions.getAccountId());
							for (Facility facility : corporate
									.getOperatorFacilities()) {
								approveContractor(cAccount, facility
										.getOperator().getId(), getWorkStatus());
							}
						}

						cAccount.incrementRecalculation();
						cAccount.setAuditColumns(permissions);
						contractorAccountDAO.save(cAccount);

						String summary = "Changed workStatus to "
								+ getWorkStatusDesc(getWorkStatus()) + " for "
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
			}
			operatorNotes = "";
			return BLANK;
		}
		return super.execute();
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

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public void approveContractor(ContractorAccount cAccount, int operatorID,
			String workStatus) {
		for (ContractorOperator cOperator : cAccount.getNonCorporateOperators()) {
			if (cOperator.getOperatorAccount().getId() == operatorID) {
				cOperator.setWorkStatus(workStatus);
				cOperator.setAuditColumns(permissions);
				contractorOperatorDAO.save(cOperator);
				break;
			}
		}
	}

	public String getWorkStatusDesc(String workStatus) {
		if (workStatus.equals("P"))
			return "Pending";
		if (workStatus.equals("Y"))
			return "Yes";
		else
			return "No";
	}
}
