package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	// Update
	private Map<String, Map<ContractorAudit, Set<ContractorAuditOperator>>> caos;
	private Map<ContractorAudit, Set<Certificate>> policyCert;
	private Map<Certificate, Set<ContractorAudit>> certPolicy;

	public ConInsureGuard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDAO) {
		super(accountDao, auditDao);
		this.certificateDAO = certificateDAO;
		this.noteCategory = NoteCategory.Audits;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		Map<ContractorAudit, Set<AuditData>> certMap = certificateDAO.findConCertsAuditData(id);
		caos = new HashMap<String, Map<ContractorAudit, Set<ContractorAuditOperator>>>();
		policyCert = new HashMap<ContractorAudit, Set<Certificate>>();
		certPolicy = new HashMap<Certificate, Set<ContractorAudit>>();
		
		for (ContractorAudit key : certMap.keySet()) {
			for (AuditData d : certMap.get(key)) {
				for (ContractorAuditOperator cao : d.getAudit().getOperators()) {
					if (cao.isVisibleTo(permissions) && cao.isVisible()) {
						if (cao.getStatus().isExpired() || d.getAudit().getExpiresDate().before(new Date()))
							addCao("Expired", d.getAudit(), cao);
						else if (cao.getStatus().isPending() || cao.getStatus().isSubmittedResubmitted())
							addCao("Pending", d.getAudit(), cao);
						else if (cao.getStatus().isApproved() || cao.getStatus().isComplete())
							addCao("Current", d.getAudit(), cao);
						else
							addCao("Other", d.getAudit(), cao);
					}
				}
				
				Certificate c = getCertByID(d.getAnswer());
				if (c != null && d.getAudit().getExpiresDate().after(new Date())) {
					if (policyCert.get(d.getAudit()) == null)
						policyCert.put(d.getAudit(), new HashSet<Certificate>());
					
					policyCert.get(d.getAudit()).add(c);
					
					if (certPolicy.get(c) == null)
						certPolicy.put(c, new HashSet<ContractorAudit>());
					
					certPolicy.get(c).add(d.getAudit());
				}
			}
		}
		
		return SUCCESS;
	}

	public List<Certificate> getCertificates() {
		if (certificates == null)
			certificates = certificateDAO.findByConId(contractor.getId(), permissions, false);
		
		return certificates;
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
	
	public Map<String, Map<ContractorAudit, Set<ContractorAuditOperator>>> getCaos() {
		return caos;
	}
	
	public Map<ContractorAudit, Set<Certificate>> getPolicyCert() {
		return policyCert;
	}
	
	public Map<Certificate, Set<ContractorAudit>> getCertPolicy() {
		return certPolicy;
	}
	
	private void addCao(String status, ContractorAudit audit, ContractorAuditOperator cao) {
		if (caos.get(status) == null)
			caos.put(status, new HashMap<ContractorAudit, Set<ContractorAuditOperator>>());
		
		if (caos.get(status).get(audit) == null)
			caos.get(status).put(audit, new HashSet<ContractorAuditOperator>());
		
		caos.get(status).get(audit).add(cao);
	}
}
