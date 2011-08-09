package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	@Autowired
	private AuditDataDAO auditDataDAO;

	String[] certTypes = {"Current", "Expired", "Uploaded"};
	
	private Map<AuditStatus, Map<ContractorAudit, List<ContractorAuditOperator>>> policiesMap;
	private Map<String, Map<Certificate, List<ContractorAuditOperator>>> certificatesMap;
	
	public ConInsureGuard() {
		this.noteCategory = NoteCategory.Insurance;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		findContractor();
		
		policiesMap = new HashMap<AuditStatus, Map<ContractorAudit, List<ContractorAuditOperator>>>();
		certificatesMap = new HashMap<String, Map<Certificate, List<ContractorAuditOperator>>>();
		
		for (AuditStatus auditStatus : AuditStatus.values()) {
			policiesMap.put(auditStatus, new HashMap<ContractorAudit, List<ContractorAuditOperator>>());
		}
		
		for (String certType : certTypes) {
			certificatesMap.put(certType, new HashMap<Certificate, List<ContractorAuditOperator>>());
		}
		
		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
				for (ContractorAuditOperator cao : ca.getOperatorsVisible()) {
					if (permissions.canSeeAudit(ca.getAuditType())) {
						List<ContractorAuditOperator> list = new ArrayList<ContractorAuditOperator>();
						for (ContractorAuditOperator cao2 : ca.getOperatorsVisible()) {
							if (permissions.isOperatorCorporate()) {
								if (cao2.isVisibleTo(permissions)) 
									list.add(cao2);
							} else {
								list.add(cao2);
							}
						}
						policiesMap.get(cao.getStatus()).put(ca, list);
					}
				}
			}
		}
		
		for (Certificate certificate : getCertificates()) {
			List<AuditData> auditData = auditDataDAO.findByCertificateID(contractor.getId(), certificate.getId());

			if (auditData.size() == 0) {
				certificatesMap.get("Uploaded").put(certificate, Collections.<ContractorAuditOperator>emptyList());
			} else {
				if (certificate.isExpired())
					certificatesMap.get("Expired").put(certificate, auditData.get(0).getAudit().getOperatorsVisible());
				else
					certificatesMap.get("Current").put(certificate, auditData.get(0).getAudit().getOperatorsVisible());
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
	
	public String getAuditForYear(Date effectiveDate) {
		Date d = (effectiveDate == null) ? new Date(): effectiveDate;
		return DateBean.format(d, "yy");
	}
	
	public Map<AuditStatus, Map<ContractorAudit, List<ContractorAuditOperator>>> getPoliciesMap() {
		return policiesMap;
	}

	public void setPoliciesMap(Map<AuditStatus, Map<ContractorAudit, List<ContractorAuditOperator>>> policiesMap) {
		this.policiesMap = policiesMap;
	}

	public Map<String, Map<Certificate, List<ContractorAuditOperator>>> getCertificatesMap() {
		return certificatesMap;
	}

	public void setCertificatesMap(Map<String, Map<Certificate, List<ContractorAuditOperator>>> certificatesMap) {
		this.certificatesMap = certificatesMap;
	}

	public String[] getCertTypes() {
		return certTypes;
	}

	public void setCertTypes(String[] certTypes) {
		this.certTypes = certTypes;
	}
}
