package com.picsauditing.actions.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class ReportInsuranceApprovalSave extends PicsActionSupport {

	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null;
	protected NoteDAO noteDao = null;
	protected ContractorAccountDAO contractorAccountDAO; 

	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;

	protected List<String> newStatuses = null;

	public ReportInsuranceApprovalSave(ContractorAuditOperatorDAO conAuditOperatorDAO, NoteDAO noteDao, ContractorAccountDAO contractorAccountDAO) {
		this.conAuditOperatorDAO = conAuditOperatorDAO;
		this.noteDao = noteDao;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public String execute() throws Exception {

		if (!forceLogin())
			return LOGIN;

		CaoStatus newStatus = null;

		if (newStatuses != null && newStatuses.size() > 0) {
			try {
				newStatus = CaoStatus.valueOf( newStatuses.get(0) );	
			}
			catch( Exception emptyStringsDontTurnIntoEnumsVeryWell ) {}
		}
		if( caoids != null && caoids.size() > 0 ) {
			for (Integer i : caoids) {
	
				boolean dirty = false;
				boolean statusChanged = false;
	
				ContractorAuditOperator existing = conAuditOperatorDAO.find(i);
				ContractorAuditOperator newVersion = caos.get(i);
	
				if (! (existing.getNotes() == null && newVersion.getNotes() == null ) ) {
						existing.setNotes(newVersion.getNotes());
						dirty = true;
				}
	
				if (newStatus != null && !newStatus.equals(existing.getStatus())) {
					existing.setStatus(newStatus);
					existing.setAuditColumns(getUser());
					existing.setStatusChangedBy(getUser());
					existing.setStatusChangedDate(new Date());
					dirty = true;
					statusChanged = true;
				}

				if (dirty) {
					conAuditOperatorDAO.save(existing);
				}

				if (statusChanged) {
					ContractorAccount contractor = existing.getAudit().getContractorAccount();
					contractor.setNeedsRecalculation(true);
					contractorAccountDAO.save(contractor);

					ContractorAuditOperatorDAO.saveNoteAndEmail(existing, permissions);
				}
			}
		}
		
		return SUCCESS;
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
