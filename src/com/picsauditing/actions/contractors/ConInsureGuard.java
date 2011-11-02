package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	@Autowired
	private AuditDataDAO auditDataDAO;

	String[] certTypes = { "Current", "Expired", "Uploaded" };

	private Map<String, Map<Certificate, String>> certificatesMap;

	private Map<ContractorAudit, List<ContractorAuditOperator>> currentPoliciesMap;
	private Map<ContractorAudit, List<ContractorAuditOperator>> expiredPoliciesMap;

	public ConInsureGuard() {
		this.noteCategory = NoteCategory.Insurance;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		findContractor();

		//Initializing Maps
		certificatesMap = new HashMap<String, Map<Certificate, String>>();

		currentPoliciesMap = new TreeMap<ContractorAudit, List<ContractorAuditOperator>>(
				new Comparator<ContractorAudit>() {
					@Override
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						return (o1.getAuditType().getName().compareTo(o2.getAuditType().getName()));
					}
				});
		
		expiredPoliciesMap = new TreeMap<ContractorAudit, List<ContractorAuditOperator>>(
				new Comparator<ContractorAudit>() {
					@Override
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						return (o1.getAuditType().getName().compareTo(o2.getAuditType().getName()));
					}
				});

		//Populating policy Maps
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy) && auditApplies(audit)) {
				if (audit.isExpired())
					expiredPoliciesMap.put(audit, null);
				else {
					List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();
					for (ContractorAuditOperator cao : audit.getOperatorsVisible()) {
						if (permissions.canSeeAudit(audit.getAuditType())) {
							if (permissions.isOperatorCorporate()) {
								if (cao.isVisibleTo(permissions)) {
									caos.add(cao);
								}
							} else {
								caos.add(cao);
							}
						}
					}
					currentPoliciesMap.put(audit, caos);
				}
			}
		}

		//populating certificate map
		for (String certType : certTypes) {
			certificatesMap.put(certType, new HashMap<Certificate, String>());
		}

		for (Certificate certificate : getCertificates()) {
			List<AuditData> auditData = auditDataDAO.findByCertificateID(contractor.getId(), certificate.getId());

			if (auditData.size() == 0) {
				certificatesMap.get("Uploaded").put(certificate, "");
			} else {
				if (certificate.isExpired())
					certificatesMap.get("Expired").put(certificate, auditData.get(0).getQuestion().getCategory().getName().toString());
				else
					certificatesMap.get("Current").put(certificate, auditData.get(0).getQuestion().getCategory().getName().toString());
			}
		}
		return SUCCESS;
	}

	@Override
	public List<Certificate> getCertificates() {
		if (certificates == null) {
			super.getCertificates();

			Collections.sort(certificates, new Comparator<Certificate>() {
				@Override
				public int compare(Certificate o1, Certificate o2) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
			});
		}

		return certificates;
	}
	
	private boolean auditApplies(ContractorAudit audit) {
		if (permissions.isAdmin())
			return true;

		if (permissions.isContractor() && audit.getContractorAccount().getId() == permissions.getAccountId())
			return true;
		
		if (permissions.isOperatorCorporate()) {
			for (ContractorAuditOperator cao : audit.getOperatorsVisible()) {
				if (cao.isVisibleTo(permissions)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public String getAuditForYear(Date effectiveDate) {
		Date d = (effectiveDate == null) ? new Date() : effectiveDate;
		return DateBean.format(d, "yy");
	}

	public Map<String, Map<Certificate, String>> getCertificatesMap() {
		return certificatesMap;
	}

	public void setCertificatesMap(Map<String, Map<Certificate, String>> certificatesMap) {
		this.certificatesMap = certificatesMap;
	}

	public String[] getCertTypes() {
		return certTypes;
	}

	public void setCertTypes(String[] certTypes) {
		this.certTypes = certTypes;
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getCurrentPoliciesMap() {
		return currentPoliciesMap;
	}

	public void setCurrentPoliciesMap(Map<ContractorAudit, List<ContractorAuditOperator>> currentPoliciesMap) {
		this.currentPoliciesMap = currentPoliciesMap;
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getExpiredPoliciesMap() {
		return expiredPoliciesMap;
	}

	public void setExpiredPoliciesMap(Map<ContractorAudit, List<ContractorAuditOperator>> expiredPoliciesMap) {
		this.expiredPoliciesMap = expiredPoliciesMap;
	}
}
