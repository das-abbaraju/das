package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class ContractorDocuments extends ContractorActionSupport {
	protected AuditTypeDAO auditTypeDAO;
	protected ContractorAuditOperatorDAO caoDAO;
	
	protected Map<AuditType, List<ContractorAudit>> auditMap;
	protected Map<AuditTypeClass, Set<AuditType>> auditTypes;
	protected Map<ContractorAudit, Map<AuditStatus, List<ContractorAuditOperator>>> counts;
	protected Map<String, String> imScores = new HashMap<String, String>();
	
	private int selectedAudit;
	private int selectedOperator;
	private String auditFor;
	private List<AuditType> auditTypeList;
	private AuditTypeClass auditClass;

	public ContractorDocuments(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditTypeDAO auditTypeDAO, ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
		this.caoDAO = caoDAO;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		setup();
		
		AuditType auditType = null;
		if (selectedAudit > 0) {
			auditType = auditTypeDAO.find(selectedAudit);
			auditClass = auditType.getClassType();
		}

		if (button != null) {
			if ("getAuditList".equals(button) && auditClass != null) {
				auditTypeList = auditTypeDAO.findAll(permissions, true, auditClass, false);
			}
			
			if (button.equals("Add") && auditType != null) {
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

					conAudit.setAuditColumns(permissions);
					conAudit.setAuditType(auditType);
					conAudit.setAuditFor(this.auditFor);
					conAudit.setContractorAccount(contractor);
					if (selectedOperator != 0) {
						conAudit.setRequestingOpAccount(new OperatorAccount());
						conAudit.getRequestingOpAccount().setId(selectedOperator);
					}
					conAudit.setManuallyAdded(true);
					conAudit = auditDao.save(conAudit);

					if (selectedOperator > 0) {
						ContractorAuditOperator cao = new ContractorAuditOperator();
						cao.setAuditColumns(permissions);
						cao.setAudit(conAudit);
						cao.setOperator(new OperatorAccount());
						cao.getOperator().setId(selectedOperator);
						cao.setStatus(AuditStatus.Pending);
						cao.setStatusChangedDate(new Date());
						cao.setPercentComplete(0);
						cao.setPercentVerified(0);
						caoDAO.save(cao);
					}

					addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
							NoteCategory.Audits, getViewableByAccount(conAudit.getAuditType().getAccount()));
				}
				
				return "saved";
			}
		}
		
		return SUCCESS;
	}

	public Map<AuditType, List<ContractorAudit>> getAuditMap() {
		return auditMap;
	}

	public Map<AuditTypeClass, Set<AuditType>> getAuditTypes() {
		return auditTypes;
	}

	public Map<ContractorAudit, Map<AuditStatus, List<ContractorAuditOperator>>> getCounts() {
		return counts;
	}
	
	public Map<String, String> getImScores() {
		return imScores;
	}

	public void setImScores(Map<String, String> imScores) {
		this.imScores = imScores;
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

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}
	
	public List<AuditType> getAuditTypeList() {
		return auditTypeList;
	}
	
	public void setAuditTypeList(List<AuditType> auditTypeList) {
		this.auditTypeList = auditTypeList;
	}
	
	public AuditTypeClass getAuditClass() {
		return auditClass;
	}
	
	public void setAuditClass(AuditTypeClass auditClass) {
		this.auditClass = auditClass;
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

	private void setup() {
		Map<String, List<ContractorAudit>> allIMAudits = new HashMap<String, List<ContractorAudit>>();
		auditMap = new TreeMap<AuditType, List<ContractorAudit>>();
		auditTypes = new TreeMap<AuditTypeClass, Set<AuditType>>();
		counts = new HashMap<ContractorAudit, Map<AuditStatus, List<ContractorAuditOperator>>>();

		for (ContractorAudit audit : getAudits()) {
			// Policies are still on their own page
			if (!audit.getAuditType().getClassType().equals(AuditTypeClass.Policy) && audit.isVisibleTo(permissions)) {
				if (auditMap.get(audit.getAuditType()) == null)
					auditMap.put(audit.getAuditType(), new ArrayList<ContractorAudit>());

				auditMap.get(audit.getAuditType()).add(audit);

				if (auditTypes.get(audit.getAuditType().getClassType()) == null)
					auditTypes.put(audit.getAuditType().getClassType(), new TreeSet<AuditType>());

				auditTypes.get(audit.getAuditType().getClassType()).add(audit.getAuditType());
			}

			Map<AuditStatus, List<ContractorAuditOperator>> statusCount = new HashMap<AuditStatus, List<ContractorAuditOperator>>();
			for (ContractorAuditOperator cao : audit.getOperators()) {
				if (statusCount.get(cao.getStatus()) == null)
					statusCount.put(cao.getStatus(), new ArrayList<ContractorAuditOperator>());
				
				statusCount.get(cao.getStatus()).add(cao);
			}
			
			counts.put(audit, statusCount);
			
			// IM Audits
			if (audit.getAuditType().getClassType().isPqf()) {
				if (!audit.getAuditType().isAnnualAddendum()) {
					if (audit.getAuditType().getClassType() == AuditTypeClass.IM) {
						List<ContractorAudit> imAudits = allIMAudits.get(audit.getAuditType().getAuditName());

						if (imAudits == null) {
							imAudits = new Vector<ContractorAudit>();
							allIMAudits.put(audit.getAuditType().getAuditName(), imAudits);
						}
						
						imAudits.add(audit);
					}
				}
			}
		}
		
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
}
