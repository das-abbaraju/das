package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.picsauditing.util.DoubleMap;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	private CertificateDAO certificateDAO;
	
	// Using CAOs
	private List<Certificate> certificates;
	
	// Update
	private Set<String> policyOrder = new HashSet<String>();
	private DoubleMap<String, ContractorAudit, Set<ContractorAuditOperator>> caos = new DoubleMap<String, ContractorAudit, Set<ContractorAuditOperator>>();
	private Set<ContractorAudit> caoAudits = new HashSet<ContractorAudit>();
	private Map<ContractorAudit, Set<Certificate>> policyCert = new HashMap<ContractorAudit, Set<Certificate>>();
	private Map<Certificate, Set<ContractorAudit>> certPolicy = new HashMap<Certificate, Set<ContractorAudit>>();

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
		
		Map<ContractorAudit, Set<AuditData>> certData = certificateDAO.findConCertsAuditData(id);
		
		for (ContractorAudit key : certData.keySet()) {
			Iterator<AuditData> iterator = certData.get(key).iterator();
			
			while (iterator.hasNext()) {
				AuditData d = iterator.next();
				if (!d.getAudit().isVisibleTo(permissions))
					iterator.remove();
				else {
					for (ContractorAuditOperator cao : d.getAudit().getOperators()) {
						if (!cao.isVisible())
							break;
						
						if (cao.getStatus().isPending() || cao.getStatus().isSubmittedResubmitted()) {
							if (caos.get("Pending", d.getAudit()) == null) {
								caos.put("Pending", d.getAudit(), new HashSet<ContractorAuditOperator>());
								policyOrder.add("Pending");
							}
							
							caos.get("Pending", d.getAudit()).add(cao);
						} else if (cao.getStatus().isApproved() || cao.getStatus().isComplete()) {
							if (caos.get("Current", d.getAudit()) == null) {
								caos.put("Current", d.getAudit(), new HashSet<ContractorAuditOperator>());
								policyOrder.add("Current");
							}
							
							caos.get("Current", d.getAudit()).add(cao);
						} else if (cao.getStatus().isExpired()) {
							if (caos.get("Expired", d.getAudit()) == null) {
								caos.put("Expired", d.getAudit(), new HashSet<ContractorAuditOperator>());
								policyOrder.add("Expired");
							}
							
							caos.get("Expired", d.getAudit()).add(cao);
						} else {
							if (caos.get("Other", d.getAudit()) == null) {
								caos.put("Other", d.getAudit(), new HashSet<ContractorAuditOperator>());
								policyOrder.add("Other");
							}
							
							caos.get("Other", d.getAudit()).add(cao);
						}
						
						caoAudits.add(d.getAudit());
					}
					
					Certificate cert = getCertByID(d.getAnswer());
					if (cert != null) {
						if (policyCert.get(d.getAudit()) == null)
							policyCert.put(d.getAudit(), new HashSet<Certificate>());
						
						policyCert.get(d.getAudit()).add(cert);
						
						if (certPolicy.get(cert) == null)
							certPolicy.put(cert, new HashSet<ContractorAudit>());
						
						certPolicy.get(cert).add(d.getAudit());
					}
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
	
	public Set<String> getPolicyOrder() {
		return policyOrder;
	}
	
	public DoubleMap<String, ContractorAudit, Set<ContractorAuditOperator>> getCaos() {
		return caos;
	}
	
	public Set<ContractorAudit> getCaoAudits() {
		return caoAudits;
	}
	
	public Map<ContractorAudit, Set<Certificate>> getPolicyCert() {
		return policyCert;
	}
	
	public Map<Certificate, Set<ContractorAudit>> getCertPolicy() {
		return certPolicy;
	}
}
