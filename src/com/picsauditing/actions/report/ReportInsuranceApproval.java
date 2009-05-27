package com.picsauditing.actions.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportInsuranceSupport {
	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null;
	protected NoteDAO noteDao = null;
	protected ContractorAccountDAO contractorAccountDAO; 

	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;

	protected List<String> newStatuses = null;
	protected Set<String> updatedContractors = new HashSet<String>();
	
	public ReportInsuranceApproval(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, ContractorAuditOperatorDAO conAuditOperatorDAO, NoteDAO noteDao, ContractorAccountDAO contractorAccountDAO) {
		// sql = new SelectContractorAudit();
		super(auditDataDao, auditQuestionDao, operatorAccountDAO);
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

		getFilter().setShowVisible(false);
		getFilter().setShowRecommendedFlag(true);

		sql.addWhere("a.active = 'Y'");

		sql.addField("cao.status as caoStatus");
		sql.addField("cao.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("cao.flag as caoRecommendedFlag");
		sql.addField("cao.reason");
	}
	
	@Override
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
					if(newStatus.equals(CaoStatus.Rejected) 
							&& Strings.isEmpty(newVersion.getNotes())) {
						dirty = false;
						statusChanged = false;
						addActionError("Add notes before rejecting " + existing.getAudit().getAuditType().getAuditName() + " for "+ existing.getAudit().getContractorAccount().getName());
						conAuditOperatorDAO.refresh(existing);
					}
					else {
						existing.setStatus(newStatus);
						existing.setAuditColumns(getUser());
						existing.setStatusChangedBy(getUser());
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
					contractor.setNeedsRecalculation(true);
					contractorAccountDAO.save(contractor);
					updatedContractors.add(contractor.getName());
					ContractorAuditOperatorDAO.saveNoteAndEmail(existing, permissions);
				}
			}
		}
		if(updatedContractors.size() > 0)
			addActionMessage("Email is sent to " + Strings.implode(updatedContractors, ",") + " notifying them about the policy status change");
		
		if(getFilter().getCaoStatus() == null) {
			CaoStatus[] caStatus = new CaoStatus[1];
			caStatus[0] = CaoStatus.Verified;
			getFilter().setCaoStatus(caStatus);
		}	
		
		return super.execute();
	}

	public boolean isRequiresActivePolicy() {
		if (permissions.seesAllContractors())
			return true;
		for (DynaBean bean : data) {
			String status = bean.get("requiredAuditStatus").toString();
			if (status.equals(AuditStatus.Active.toString()))
				return true;
		}
		return false;
	}

	public String getFormattedDollarAmount(String answer) {
		String response = "$0";

		try {
			String temp = answer.replaceAll(",", "");
			DecimalFormat decimalFormat = new DecimalFormat("$#,##0");

			Long input = new Long(temp);

			response = decimalFormat.format(input);
		} catch (Exception e) {
			// System.out.println("unable to format as money: " + answer);
		}
		return response;
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
