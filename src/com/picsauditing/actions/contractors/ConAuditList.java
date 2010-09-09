package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ConAuditList extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private int selectedAudit;
	private int selectedOperator;
	private String auditFor;
	private Map<String, String> imScores = new HashMap<String, String>();
	private List<AuditType> auditTypeList;
	private AuditTypeClass auditClass = AuditTypeClass.Audit;

	public List<ContractorAudit> upComingAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> currentAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> expiredAudits = new ArrayList<ContractorAudit>();
	public List<AuditData> certificatesFiles = new ArrayList<AuditData>();

	public ConAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
		this.noteCategory = NoteCategory.Audits;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		Map<String, List<ContractorAudit>> allIMAudits = new HashMap<String, List<ContractorAudit>>();

		for (ContractorAudit contractorAudit : getAudits()) {
			// Only show Insurance policies or all of them
			if (contractorAudit.getAuditType().getClassType().equals(auditClass)
					|| (auditClass.equals(AuditTypeClass.Audit) && contractorAudit.getAuditType().getClassType()
							.isPqf())) {
				if (!contractorAudit.getAuditType().isAnnualAddendum()) {

					if (contractorAudit.getAuditStatus().isPendingSubmitted()
							|| contractorAudit.getAuditStatus().isIncomplete())
						upComingAudits.add(contractorAudit);
					else if (contractorAudit.getAuditStatus().isActiveResubmittedExempt())
						currentAudits.add(contractorAudit);
					else if (contractorAudit.isExpired())
						expiredAudits.add(contractorAudit);
					else {
						// There shouldn't be any others
					}

					if (auditClass == AuditTypeClass.IM) {
						List<ContractorAudit> imAudits = allIMAudits.get(contractorAudit.getAuditType().getAuditName());

						if (imAudits == null) {
							imAudits = new Vector<ContractorAudit>();
							allIMAudits.put(contractorAudit.getAuditType().getAuditName(), imAudits);
						}
						imAudits.add(contractorAudit);
					}
				}
			}
		}

		if (auditClass == AuditTypeClass.IM) {
			for (String auditName : allIMAudits.keySet()) {
				int count = 0;
				float score = 0;
				for (ContractorAudit audit : allIMAudits.get(auditName)) {
					score += audit.getScore();
					count += 1;
				}

				int tempScore = -1;

				if (count != 0) {

					float average = score / (float) count;

					tempScore = Math.round(average);
				}

				Map<Integer, String> map = new HashMap<Integer, String>() {
					{
						put(-1, "None");
						put(0, "Red");
						put(1, "Yellow");
						put(2, "Green");
					}
				};

				imScores.put(auditName, map.get(tempScore));
			}
		}

		if (button != null && button.equals("Add")) {
			if (selectedAudit > 0) {
				boolean alreadyExists = false;
				if (permissions.isOperator() || permissions.isCorporate())
					selectedOperator = permissions.getAccountId();

				if (auditClass != AuditTypeClass.IM) {
					for (ContractorAudit conAudit : contractor.getAudits()) {
						if (conAudit.getAuditType().getId() == selectedAudit && !conAudit.isExpired()) {
							if ((selectedOperator == 0 && conAudit.getRequestingOpAccount() == null)
									|| (conAudit.getRequestingOpAccount() != null && conAudit.getRequestingOpAccount()
											.getId() == selectedOperator)) {
								alreadyExists = true;
								break;
							}
						}
					}
				}

				if (alreadyExists) {
					addActionError("Audit already exists");
				} else {
					ContractorAudit conAudit = new ContractorAudit();
					AuditType auditType = auditTypeDAO.find(selectedAudit);

					conAudit.setAuditType(auditType);
					conAudit.setAuditFor(this.auditFor);
					conAudit.setContractorAccount(contractor);
					conAudit.changeStatus(AuditStatus.Pending, getUser());
					if (selectedOperator != 0) {
						conAudit.setRequestingOpAccount(new OperatorAccount());
						conAudit.getRequestingOpAccount().setId(selectedOperator);
					}
					conAudit.setManuallyAdded(true);
					conAudit = auditDao.save(conAudit);

					addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
							NoteCategory.Audits,getViewableByAccount(conAudit.getAuditType().getAccount()));

					return "saved";
				}
			}
		}
		auditTypeList = auditTypeDAO.findAll(permissions, true, auditClass);

		return SUCCESS;
	}

	public List<AuditType> getAuditTypeName() {
		return auditTypeList;
	}

	public int getSelectedAudit() {
		return selectedAudit;
	}

	public void setSelectedAudit(int selectedAudit) {
		this.selectedAudit = selectedAudit;
	}

	public int getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(int selectedOperator) {
		this.selectedOperator = selectedOperator;
	}

	public List<ContractorAudit> getUpComingAudits() {
		return upComingAudits;
	}

	public List<ContractorAudit> getCurrentAudits() {
		return currentAudits;
	}

	public List<ContractorAudit> getExpiredAudits() {
		return expiredAudits;
	}

	public boolean isManuallyAddAudit() {
		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit))
			return true;
		if (permissions.isOperator() || permissions.isCorporate()) {
			if (auditTypeList.size() > 0)
				return true;
		}
		return false;
	}

	public AuditTypeClass getAuditClass() {
		return auditClass;
	}

	public void setAuditClass(AuditTypeClass auditClass) {
		this.auditClass = auditClass;
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public Map<String, String> getImScores() {
		return imScores;
	}

	public void setImScores(Map<String, String> imScores) {
		this.imScores = imScores;
	}
}
