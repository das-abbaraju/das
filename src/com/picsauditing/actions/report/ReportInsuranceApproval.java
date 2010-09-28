package com.picsauditing.actions.report;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAuditOperator {
	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null;
	protected NoteDAO noteDao = null;
	protected ContractorAccountDAO contractorAccountDAO;

	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;

	protected List<String> newStatuses = null;
	protected Set<String> updatedContractors = new HashSet<String>();
	
	public ReportInsuranceApproval(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, ContractorAuditOperatorDAO conAuditOperatorDAO, NoteDAO noteDao, ContractorAccountDAO contractorAccountDAO, AmBestDAO amBestDAO) {
		super(auditDataDao, auditQuestionDao, operatorAccountDAO, amBestDAO);
		this.conAuditOperatorDAO = conAuditOperatorDAO;
		this.noteDao = noteDao;
		this.contractorAccountDAO = contractorAccountDAO;
		this.report.setLimit(25);
		orderByDefault = "cao.status DESC, cao.updateDate ASC";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowStatus(false);
		getFilter().setShowRecommendedFlag(true);
		getFilter().setShowAMBest(true);

		sql.addWhere("a.status IN ('Active','Demo')");

		sql.addField("cao.status as caoStatus");
		sql.addField("caow.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("caoaccount.name as caoOperatorName");
		sql.addField("cao.flag as caoRecommendedFlag");
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		// TODO move this to CaoSave
		if ("save".equalsIgnoreCase(button)) {
			// TODO Move this over to the new CaoSave class
			AuditStatus newStatus = null;
			if (newStatuses != null && newStatuses.size() > 0) {
				try {
					newStatus = AuditStatus.valueOf( newStatuses.get(0) );	
				}
				catch( Exception emptyStringsDontTurnIntoEnumsVeryWell ) {}
			}
			if( caoids != null && caoids.size() > 0 ) {
				for (Integer i : caoids) {
		
					boolean dirty = false;
					boolean statusChanged = false;
		
					ContractorAuditOperator existing = conAuditOperatorDAO.find(i);
					ContractorAuditOperator newVersion = caos.get(i);
		
					if (newStatus != null && !newStatus.equals(existing.getStatus())) {
						if(newStatus.isIncomplete()) {
							dirty = false;
							statusChanged = false;
							addActionError("Add notes before rejecting " + existing.getAudit().getAuditType().getAuditName() + " for "+ existing.getAudit().getContractorAccount().getName());
							conAuditOperatorDAO.refresh(existing);
						}
						else {
							existing.setStatus(newStatus);
							existing.setAuditColumns(permissions);
							existing.setStatusChangedDate(new Date());
							dirty = true;
							statusChanged = true;
						}
					}

					if (dirty) {
						conAuditOperatorDAO.save(existing);
					}

					if (statusChanged) {
						ContractorAccount contractor = existing.getAudit().getContractorAccount();
						contractor.incrementRecalculation();
						contractorAccountDAO.save(contractor);
						updatedContractors.add(contractor.getName());
						ContractorAuditOperatorDAO.saveNoteAndEmail(existing, permissions);
					}
				}
			}
			if(updatedContractors.size() > 0)
				addActionMessage("Email is sent to " + Strings.implode(updatedContractors, ",") + " notifying them about the policy status change");
			return BLANK;
		}
		
		return super.execute();
	}

	public Map<Integer, ContractorAuditOperator> getCaos() {
		return caos;
	}

	public void setCaos(Map<Integer, ContractorAuditOperator> caos) {
		this.caos = caos;
	}

	public List<String> getNewStatuses() {
		return newStatuses;
	}

	public void setNewStatuses(List<String> newStatuses) {
		this.newStatuses = newStatuses;
	}

	public List<Integer> getCaoids() {
		return caoids;
	}

	public void setCaoids(List<Integer> caoids) {
		this.caoids = caoids;
	}
}
