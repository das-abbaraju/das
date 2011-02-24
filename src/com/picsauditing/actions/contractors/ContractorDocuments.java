package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	protected Map<DocumentTab, List<AuditType>> auditTypes;
	protected Map<String, String> imScores = new TreeMap<String, String>();
	protected Map<DocumentTab, List<ContractorAudit>> expiredAudits;

	protected Integer selectedAudit;
	protected Integer selectedOperator;
	protected String auditFor;
	protected AuditTypeClass auditClass;

	protected Set<AuditType> manuallyAddAudits = null;

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

		Map<String, List<ContractorAudit>> allIMAudits = new TreeMap<String, List<ContractorAudit>>();
		auditMap = new TreeMap<AuditType, List<ContractorAudit>>();
		auditTypes = new TreeMap<DocumentTab, List<AuditType>>();
		expiredAudits = new TreeMap<DocumentTab, List<ContractorAudit>>();

		for (ContractorAudit audit : getAudits()) {
			DocumentTab tab = new DocumentTab(audit.getAuditType());
			if (!audit.isExpired() && audit.getCurrentOperators().size() > 0) {
				if (auditMap.get(audit.getAuditType()) == null)
					auditMap.put(audit.getAuditType(), new ArrayList<ContractorAudit>());

				auditMap.get(audit.getAuditType()).add(audit);

				if (auditTypes.get(tab) == null)
					auditTypes.put(tab, new ArrayList<AuditType>());

				if (!auditTypes.get(tab).contains(audit.getAuditType()))
					auditTypes.get(tab).add(audit.getAuditType());

				// IM Audits
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.IM)) {
					List<ContractorAudit> imAudits = allIMAudits.get(audit.getAuditType().getAuditName());

					if (imAudits == null) {
						imAudits = new Vector<ContractorAudit>();
						allIMAudits.put(audit.getAuditType().getAuditName(), imAudits);
					}

					imAudits.add(audit);
				}
			} else if ((audit.getAuditType().getClassType().isAudit() || audit.getAuditType().getClassType().isPolicy())
					&& !audit.getAuditType().isAnnualAddendum()) {
				if (expiredAudits.get(tab) == null)
					expiredAudits.put(tab, new ArrayList<ContractorAudit>());

				expiredAudits.get(tab).add(audit);
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
				count++;
			}

			int tempScore = -1;
			if (count != 0)
				tempScore = Math.round(score / (float) count);

			if (tempScore <= 0)
				imScores.put(auditName, "None");
			else if (tempScore < 50)
				imScores.put(auditName, "Red");
			else if (tempScore < 100)
				imScores.put(auditName, "Yellow");
			else
				imScores.put(auditName, "Green");
		}

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

	public Map<DocumentTab, List<AuditType>> getAuditTypes() {
		return auditTypes;
	}

	public Map<String, String> getImScores() {
		return imScores;
	}

	public Map<DocumentTab, List<ContractorAudit>> getExpiredAudits() {
		return expiredAudits;
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

	public class DocumentTab implements Comparable<DocumentTab> {
		private AuditType type;

		public DocumentTab(AuditType type) {
			this.type = type;
		}

		public int getOrder() {
			if (type.isAnnualAddendum())
				return 1;
			else if (type.getClassType().isAudit())
				return 4;
			
			return type.getClassType().ordinal();
		}

		public String getName() {
			if (type.isAnnualAddendum() || type.getClassType().isIm())
				return getText(type.getI18nKey("name"));
			
			if (type.getClassType().isPqf())
				return getText("AuditType.1.name");
			
			if (type.getClassType().isPolicy()) 
				return getText("global.InsureGUARD");
			
			return getText("global.AuditGUARD");
		}
		
		@Override
		public int compareTo(DocumentTab o) {
			return this.getOrder() - o.getOrder();
		}

		@Override
		public String toString() {
			return this.getName();
		}
	}
	
	public static String getSafeName(String name) {
		return name.toLowerCase().replaceAll(" ", "_").replaceAll("&(.*?);", "");
	}
}