package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.YesNo;

@SuppressWarnings("serial")
public class ConAuditList extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private AuditDataDAO auditDataDAO;
	private int selectedAudit;
	private int selectedOperator;
	private String auditFor;
	private Map<String, String> imScores = new HashMap<String, String>();
	private List<AuditType> auditTypeList;
	private AuditTypeClass auditClass = AuditTypeClass.Audit;

	private AuditBuilder auditBuilder;

	public List<ContractorAudit> upComingAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> currentAudits = new ArrayList<ContractorAudit>();
	public List<ContractorAudit> expiredAudits = new ArrayList<ContractorAudit>();
	public List<AuditData> certificatesFiles = new ArrayList<AuditData>();

	public ConAuditList(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO,
			AuditDataDAO auditDataDAO, AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
		this.auditDataDAO = auditDataDAO;
		this.auditBuilder = auditBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		Map<String, List<ContractorAudit>> allIMAudits = new HashMap<String, List<ContractorAudit>>();

		for (ContractorAudit contractorAudit : getAudits()) {
			// Only show Insurance policies or all of them
			if (contractorAudit.getAuditType().getClassType().equals(auditClass) 
					|| (auditClass.equals(AuditTypeClass.Audit) && contractorAudit.getAuditType().getClassType().isPqf())) {
				if (auditClass.isPolicy()) {
					if (contractorAudit.getAuditStatus().isExpired()) {
						expiredAudits.add(contractorAudit);
					} else {
						for (ContractorAuditOperator conAuditOp : getCaosByAccount(contractorAudit)) {
							if (conAuditOp.getStatus().isPending() || conAuditOp.getStatus().isSubmitted()
									|| conAuditOp.getStatus().isVerified()) {
								if (!upComingAudits.contains(contractorAudit))
									upComingAudits.add(contractorAudit);
								else
									break;
							} else if (conAuditOp.getStatus().isApproved() || conAuditOp.getStatus().isRejected()) {
								if (!currentAudits.contains(contractorAudit))
									currentAudits.add(contractorAudit);
								else
									break;
							}
						}
					}
				} else if (!contractorAudit.getAuditType().isAnnualAddendum()) {

					if (contractorAudit.getAuditStatus().isPendingSubmitted())
						upComingAudits.add(contractorAudit);
					else if (contractorAudit.getAuditStatus().isActiveResubmittedExempt())
						currentAudits.add(contractorAudit);
					else if (contractorAudit.getAuditStatus().equals(AuditStatus.Expired))
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
			boolean alreadyExists = false;
			if (permissions.isOperator() || permissions.isCorporate())
				selectedOperator = permissions.getAccountId();

			if (auditClass != AuditTypeClass.IM) {
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (conAudit.getAuditType().getId() == selectedAudit && !conAudit.getAuditStatus().isExpired()) {
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
				conAudit.setPercentComplete(0);
				conAudit.setPercentVerified(0);
				conAudit.setManuallyAdded(true);
				conAudit = auditDao.save(conAudit);

				addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
						NoteCategory.Audits);

				if (auditClass.isPolicy()) {
					contractor.getAudits().add(conAudit);
					auditBuilder.setUser(getUser());
					auditBuilder.buildAudits(contractor);
				}
				return "saved";
			}
		}
		auditTypeList = auditTypeDAO.findAll(permissions, true, auditClass);

		return SUCCESS;
	}

	public List<ContractorAuditOperator> getCaosByAccount(ContractorAudit ca) {
		List<ContractorAuditOperator> result = new ArrayList<ContractorAuditOperator>();
		for (ContractorAuditOperator cao : ca.getCurrentOperators()) {
			boolean add = false;
			if (permissions.isOperator() && cao.getOperator().getId() == permissions.getAccountId())
				add = true;
			else if (permissions.isCorporate()) {
				if (permissions.getOperatorChildren().contains(cao.getOperator().getId()))
					add = true;
			}
			else if(!permissions.isOperator() && !permissions.isCorporate())
				add = true;

			if (add)
				result.add(cao);
		}

		return result;
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
		if (permissions.isContractor()) {
			if (auditClass.equals(AuditTypeClass.Policy) || auditClass.equals(AuditTypeClass.IM))
				return true;
			return false;
		}

		if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit)
				|| permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Edit))
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

	public List<AuditData> getCertificatesFiles() {
		certificatesFiles = auditDataDAO.findAnswersByContractorAndUniqueCode(contractor.getId(), "policyFile");
		if (certificatesFiles == null)
			return new ArrayList<AuditData>();
		if (permissions.isContractor() || permissions.seesAllContractors() || permissions.isAuditor())
			return certificatesFiles;
		List<AuditData> operatorList = new ArrayList<AuditData>();
		OperatorAccount thisOp = (OperatorAccount) getUser().getAccount();

		for (AuditData answer : certificatesFiles) {
			if (answer.getParentAnswer() != null) {
				if (thisOp.isHasLegalName(answer.getParentAnswer().getAnswer())) {
					operatorList.add(answer);
				}
			} else
				operatorList.add(answer);
		}
		return operatorList;
	}

	public void setCertificatesFiles(List<AuditData> certificatesFiles) {
		this.certificatesFiles = certificatesFiles;
	}

	public List<ContractorOperator> getOperatorsWithInsurance() {
		List<ContractorOperator> result = new ArrayList<ContractorOperator>();

		for (ContractorOperator o : getOperators()) {
			if (o.getOperatorAccount().getInheritInsurance().getCanSeeInsurance().equals(YesNo.Yes)) {
				for (AuditOperator ao : o.getOperatorAccount().getAudits()) {
					if (ao.isCanSee() && ao.getMinRiskLevel() > 0
							&& ao.getMinRiskLevel() <= contractor.getRiskLevel().ordinal()) {
						result.add(o);
						break;
					}
				}
			}
		}

		return result;
	}

	public List<AuditType> getRequiredAuditTypeNames() {
		List<AuditType> result = new ArrayList<AuditType>();

		for (ContractorOperator co : contractor.getOperators()) {
			for (AuditOperator ao : co.getOperatorAccount().getAudits()) {
				if (auditTypeList.contains(ao.getAuditType()) && ao.isCanSee() && !result.contains(ao.getAuditType())) {
					result.add(ao.getAuditType());
				}
			}
		}

		return result;
	}
}
