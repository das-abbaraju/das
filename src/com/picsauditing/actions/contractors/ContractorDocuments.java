package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAudit;

@SuppressWarnings("serial")
public class ContractorDocuments extends ContractorActionSupport {
	@Autowired
	protected AuditTypeDAO auditTypeDAO;
	@Autowired
	protected ContractorAuditOperatorDAO caoDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDao;

	protected Map<AuditType, List<ContractorAudit>> auditMap;
	protected Map<DocumentTab, List<AuditType>> auditTypes;
	protected Map<String, String> imScores = new TreeMap<String, String>();
	protected List<ContractorAudit> expiredAudits;

	protected Integer selectedAudit;
	protected Integer selectedOperator;
	protected String auditFor;
	protected AuditTypeClass auditClass;

	@Override
	public String execute() throws Exception {
		findContractor();

		Map<String, List<ContractorAudit>> allIMAudits = new TreeMap<String, List<ContractorAudit>>();
		auditMap = new TreeMap<AuditType, List<ContractorAudit>>();
		auditTypes = new TreeMap<DocumentTab, List<AuditType>>();
		expiredAudits = new ArrayList<ContractorAudit>();

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
					List<ContractorAudit> imAudits = allIMAudits.get(getText(audit.getAuditType().getI18nKey("name")));

					if (imAudits == null) {
						imAudits = new Vector<ContractorAudit>();
						allIMAudits.put(getText(audit.getAuditType().getI18nKey("name")), imAudits);
					}

					imAudits.add(audit);
				}
			}
		}

		for (AuditType type : auditMap.keySet()) {
			if (type.getId() == AuditType.ANNUALADDENDUM)
				Collections.sort(auditMap.get(type), new AuditByDate());
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
		
		loadExpiredAudits();

		return SUCCESS;
	}

	private void loadExpiredAudits() {
		List<ContractorAudit> temp = contractorAuditDao.findExpiredByContractor(contractor.getId());

		for (ContractorAudit contractorAudit : temp) {
			if (contractorAudit.isVisibleTo(permissions)) {
				expiredAudits.add(contractorAudit);
			}
		}
		Collections.sort(expiredAudits, new AuditByDate());
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

	public List<ContractorAudit> getExpiredAudits() {
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

		public int compareTo(DocumentTab o) {
			return this.getOrder() - o.getOrder();
		}

		@Override
		public String toString() {
			return this.getName();
		}
	}

	public static String getSafeName(String name) {
		if (name == null)
			return null;
		return name.toLowerCase().replaceAll(" ", "_").replaceAll("&(.*?);", "");
	}

	private class AuditByDate implements Comparator<ContractorAudit> {
		public int compare(ContractorAudit o1, ContractorAudit o2) {
			if (o1.getAuditType().isAnnualAddendum() && o2.getAuditType().isAnnualAddendum())
				return o2.getAuditFor().compareTo(o1.getAuditFor());
			else if (o1.isExpired() && o2.isExpired())
				return o2.getExpiresDate().compareTo(o1.getExpiresDate());
			else
				return o1.getCreationDate().compareTo(o2.getCreationDate());
		}
	}
}