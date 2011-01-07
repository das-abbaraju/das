package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class ContractorDocuments extends ContractorActionSupport {
	protected AuditTypeDAO auditTypeDAO;
	protected ContractorAuditOperatorDAO caoDAO;
	protected AuditTypeRuleCache auditTypeRuleCache;

	protected Map<AuditType, List<ContractorAudit>> auditMap;
	protected Map<String, List<AuditType>> auditTypes;
	protected Map<String, String> imScores = new HashMap<String, String>();

	protected Integer selectedAudit;
	protected Integer selectedOperator;
	protected String auditFor;
	protected List<AuditType> auditTypeList;
	protected AuditTypeClass auditClass;

	protected Set<AuditType> manuallyAddAudits = null;

	private final String ANNUAL_UPDATE = "AU";

	public ContractorDocuments(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO,
			ContractorAuditOperatorDAO caoDAO, AuditTypeRuleCache auditTypeRuleCache) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
		this.caoDAO = caoDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;

		subHeading = "Document Index";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		setup();

		return SUCCESS;
	}

	public boolean isManuallyAddAudit() {
		if (getManuallyAddAudits().size() > 0) {
			if (permissions.hasPermission(OpPerms.ManageAudits, OpType.Edit))
				return true;
			if (permissions.isOperatorCorporate()) {
				return true;
			}
		}
		return false;
	}

	public Map<AuditType, List<ContractorAudit>> getAuditMap() {
		return auditMap;
	}

	public Map<String, List<AuditType>> getAuditTypes() {
		return auditTypes;
	}

	public Map<String, String> getImScores() {
		return imScores;
	}

	public void setImScores(Map<String, String> imScores) {
		this.imScores = imScores;
	}

	public Integer getSelectedAudit() {
		return selectedAudit;
	}

	public void setSelectedAudit(Integer selectedAudit) {
		this.selectedAudit = selectedAudit;
	}

	public Integer getSelectedOperator() {
		return selectedOperator;
	}

	public void setSelectedOperator(Integer selectedOperator) {
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

	public Set<AuditType> getManuallyAddAudits() {
		if (manuallyAddAudits == null) {
			manuallyAddAudits = new HashSet<AuditType>();
			List<AuditTypeRule> applicableAuditRules = auditTypeRuleCache.getApplicableAuditRules(contractor);
			for (AuditTypeRule auditTypeRule : applicableAuditRules) {
				if (auditTypeRule.isInclude() && auditTypeRule.getAuditType() != null) {
					if (!auditTypeRule.getAuditType().isAnnualAddendum()
							&& (auditTypeRule.getAuditType().isHasMultiple() || auditTypeRule.isManuallyAdded())) {
						if (permissions.isAdmin())
							manuallyAddAudits.add(auditTypeRule.getAuditType());
						else if (permissions.isOperator()) {
							if (auditTypeRule.getOperatorAccount() != null
									&& permissions.getCorporateParent().contains(
											auditTypeRule.getOperatorAccount().getId())) {
								manuallyAddAudits.add(auditTypeRule.getAuditType());
							}
						} else if (permissions.isCorporate()) {
							if (auditTypeRule.getOperatorAccount() != null
									&& auditTypeRule.getOperatorAccount().getId() == permissions.getAccountId()) {
								manuallyAddAudits.add(auditTypeRule.getAuditType());
							}
						}
					}
				}
			}
		}

		return manuallyAddAudits;
	}

	private void setup() {
		Map<String, List<ContractorAudit>> allIMAudits = new HashMap<String, List<ContractorAudit>>();
		auditMap = new TreeMap<AuditType, List<ContractorAudit>>();
		auditTypes = new HashMap<String, List<AuditType>>();

		for (ContractorAudit audit : getAudits()) {
			// Policies are still on their own page
			if (audit.isVisibleTo(permissions) && !audit.isExpired()) {
				if (auditMap.get(audit.getAuditType()) == null)
					auditMap.put(audit.getAuditType(), new ArrayList<ContractorAudit>());

				auditMap.get(audit.getAuditType()).add(audit);

				String auditTypeClass = audit.getAuditType().getClassType().toString();
				// Put annual updates in their own category?
				if (audit.getAuditType().getId() == AuditType.ANNUALADDENDUM)
					auditTypeClass = ANNUAL_UPDATE;

				if (auditTypes.get(auditTypeClass) == null)
					auditTypes.put(auditTypeClass, new ArrayList<AuditType>());

				if (!auditTypes.get(auditTypeClass).contains(audit.getAuditType()))
					auditTypes.get(auditTypeClass).add(audit.getAuditType());

				// IM Audits
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

		for (AuditType type : auditMap.keySet()) {
			if (type.getId() == AuditType.ANNUALADDENDUM) {
				Collections.sort(auditMap.get(type), new Comparator<ContractorAudit>() {
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						String s1 = o1.getAuditType().getAuditName() + o1.getAuditFor();
						String s2 = o2.getAuditType().getAuditName() + o2.getAuditFor();

						return (s2.compareTo(s1));
					}
				});

				break;
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