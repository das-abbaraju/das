package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	private CertificateDAO certificateDAO;

	// Using CAOs
	private List<Certificate> certificates;
	// Audit data
	private Map<String, Map<ContractorAudit, List<ContractorAuditOperator>>> status;
	private Map<ContractorAuditOperator, Certificate> caoCert;
	private Map<Certificate, List<ContractorAuditOperator>> certCaos;

	public ConInsureGuard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
		this.noteCategory = NoteCategory.Audits;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		List<AuditData> data = certificateDAO.findConCertsAuditData(id);
		// Match certs to the category name
		status = new HashMap<String, Map<ContractorAudit, List<ContractorAuditOperator>>>();
		caoCert = new HashMap<ContractorAuditOperator, Certificate>();
		certCaos = new HashMap<Certificate, List<ContractorAuditOperator>>();

		status.put("Pending", new HashMap<ContractorAudit, List<ContractorAuditOperator>>());
		status.put("Current", new HashMap<ContractorAudit, List<ContractorAuditOperator>>());
		status.put("Expired", new HashMap<ContractorAudit, List<ContractorAuditOperator>>());
		status.put("Other", new HashMap<ContractorAudit, List<ContractorAuditOperator>>());

		for (AuditData d : data) {
			ContractorAuditOperator cao = findCao(d);
			Certificate cert = getCertByID(d.getAnswer());

			if (cert != null) {
				if (caoCert.get(cao) == null)
					caoCert.put(cao, cert);
				
				if (certCaos.get(cert) == null)
					certCaos.put(cert, new ArrayList<ContractorAuditOperator>());
				if (!certCaos.get(cert).contains(cao))
					certCaos.get(cert).add(cao);
				
				addStatus(cao);
			}
		}
		
		for (String n : status.keySet()) {
			for (ContractorAudit c : status.get(n).keySet()) {
				Collections.sort(status.get(n).get(c), new Comparator<ContractorAuditOperator>() {
					@Override
					public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
						return (o1.getOperator().getName().compareTo(o2.getOperator().getName()));
					}
				});
			}
		}
		
		for (Certificate c : certCaos.keySet()) {
			Collections.sort(certCaos.get(c), new Comparator<ContractorAuditOperator>() {
				@Override
				public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
					return (o1.getOperator().getName().compareTo(o2.getOperator().getName()));
				}
			});
		}

		return SUCCESS;
	}

	public List<Certificate> getCertificates() {
		if (certificates == null)
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, false);

		return certificates;
	}
	
	public Map<ContractorAuditOperator, Certificate> getCaoCert() {
		return caoCert;
	}
	
	public Map<Certificate, List<ContractorAuditOperator>> getCertCaos() {
		return certCaos;
	}
	
	public Map<String, Map<ContractorAudit, List<ContractorAuditOperator>>> getStatus() {
		return status;
	}

	public Certificate getCertByID(String certID) {
		int id = 0;

		try {
			id = Integer.parseInt(certID);

			for (Certificate c : getCertificates()) {
				if (c.getId() == id)
					return c;
			}
		} catch (Exception e) {
			System.out.println("Could not find certificate: " + certID);
		}

		return null;
	}

	private ContractorAuditOperator findCao(AuditData d) {
		for (ContractorAuditOperator cao : d.getAudit().getOperators()) {
			if (cao.getOperator().getName().equals(d.getQuestion().getCategory().getName()))
				return cao;
		}

		return null;
	}
	
	private void addStatus(ContractorAuditOperator cao) {
		if (cao.getStatus().isPendingSubmittedResubmitted()) {
			if (status.get("Pending").get(cao.getAudit()) == null)
				status.get("Pending").put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());
			
			if (!status.get("Pending").get(cao.getAudit()).contains(cao))
				status.get("Pending").get(cao.getAudit()).add(cao);
		} else if (cao.getStatus().isApproved()) {
			if (status.get("Current").get(cao.getAudit()) == null)
				status.get("Current").put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());
			
			if (!status.get("Current").get(cao.getAudit()).contains(cao))
				status.get("Current").get(cao.getAudit()).add(cao);
		} else if (cao.getStatus().isExpired()) {
			if (status.get("Expired").get(cao.getAudit()) == null)
				status.get("Expired").put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());
			
			if (!status.get("Expired").get(cao.getAudit()).contains(cao))
				status.get("Expired").get(cao.getAudit()).add(cao);
		} else {
			if (status.get("Other").get(cao.getAudit()) == null)
				status.get("Other").put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());
			
			if (!status.get("Other").get(cao.getAudit()).contains(cao))
				status.get("Other").get(cao.getAudit()).add(cao);
		}
	}
}
