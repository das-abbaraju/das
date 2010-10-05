package com.picsauditing.actions.contractors;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;


@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private CertificateDAO certificateDAO;
	private ContractorAuditOperatorDAO caoDao;
	private AuditBuilderController auditBuilder;
	
	private int selectedAudit;
	private int selectedOperator;
	private String auditFor;
	private List<AuditType> auditTypeList;
	private List<Certificate> certificates;
	private AuditTypeClass auditClass = AuditTypeClass.Policy;
	
	// Using CAOs
	private Map<String, Map<AuditType, Set<AuditData>>> policies = new HashMap<String, Map<AuditType,Set<AuditData>>>();
	private Map<String, Set<AuditData>> certMap = new HashMap<String, Set<AuditData>>();
	private String[] policyOrder = new String[] { "Pending", "Current", "Expired", "Other" };

	public ConInsureGuard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO,
			AuditBuilderController auditBuilder, CertificateDAO certificateDAO, ContractorAuditOperatorDAO caoDao) {
		super(accountDao, auditDao);
		this.auditTypeDAO = auditTypeDAO;
		this.auditBuilder = auditBuilder;
		this.certificateDAO = certificateDAO;
		this.caoDao = caoDao;
		this.noteCategory = NoteCategory.Audits;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		Map<AuditType, Set<AuditData>> certData = certificateDAO.findConCertsAuditData(id);
		
		Map<AuditType, Set<AuditData>> pending = new HashMap<AuditType, Set<AuditData>>();
		Map<AuditType, Set<AuditData>> current = new HashMap<AuditType, Set<AuditData>>();
		Map<AuditType, Set<AuditData>> expired = new HashMap<AuditType, Set<AuditData>>();
		Map<AuditType, Set<AuditData>> other = new HashMap<AuditType, Set<AuditData>>();
		
		for (AuditType key : certData.keySet()) {
			Iterator<AuditData> iterator = certData.get(key).iterator();
			
			while (iterator.hasNext()) {
				AuditData d = iterator.next();
				if (!d.getAudit().isVisibleTo(permissions))
					iterator.remove();
				else {
					for (ContractorAuditOperator cao : d.getAudit().getOperators()) {
						if (cao.getStatus().equals(AuditStatus.Pending)
								|| cao.getStatus().equals(AuditStatus.Submitted)
								|| cao.getStatus().equals(AuditStatus.Resubmitted)) {
							if (pending.get(key) == null)
								pending.put(key, new HashSet<AuditData>());
							
							pending.get(key).add(d);
						} else if (cao.getStatus().equals(AuditStatus.Complete)
									|| cao.getStatus().equals(AuditStatus.Approved)) {
							if (current.get(key) == null)
								current.put(key, new HashSet<AuditData>());
							
							current.get(key).add(d);
						} else if (cao.getStatus().equals(AuditStatus.Expired)) {
							if (expired.get(key) == null)
								expired.put(key, new HashSet<AuditData>());
							
							expired.get(key).add(d);
						} else {
							if (other.get(key) == null)
								other.put(key, new HashSet<AuditData>());
							
							other.get(key).add(d);
						}
					}
					
					if (certMap.get(d.getAnswer()) == null)
						certMap.put(d.getAnswer(), new HashSet<AuditData>());
					
					certMap.get(d.getAnswer()).add(d);
				}
			}
		}
		
		if (pending.keySet().size() > 0)
			policies.put("Pending", pending);
		if (current.keySet().size() > 0)
			policies.put("Current", current);
		if (expired.keySet().size() > 0)
			policies.put("Expired", expired);
		if (other.keySet().size() > 0)
			policies.put("Other", other);
		
		if (button != null && button.equals("Add")) {
			if (selectedAudit > 0) {
				boolean alreadyExists = false;
				// if (permissions.isOperator() || permissions.isCorporate())
				// selectedOperator = permissions.getAccountId();

				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (!conAudit.isExpired() && conAudit.getAuditType().getId() == selectedAudit
							&& conAudit.getRequestingOpAccount() == null) {
						alreadyExists = true;
						break;
					}
				}

				if (alreadyExists) {
					addActionError("Policy already exists");
				} else {
					ContractorAudit conAudit = new ContractorAudit();
					AuditType auditType = auditTypeDAO.find(selectedAudit);

					conAudit.setAuditType(auditType);
					conAudit.setAuditFor(this.auditFor);
					conAudit.setContractorAccount(contractor);
					if (selectedOperator != 0) {
						conAudit.setRequestingOpAccount(new OperatorAccount());
						conAudit.getRequestingOpAccount().setId(selectedOperator);
					}
					conAudit.setManuallyAdded(true);
					conAudit = auditDao.save(conAudit);

					addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
							NoteCategory.Insurance, getViewableByAccount(conAudit.getAuditType().getAccount()));

					contractor.getAudits().add(conAudit);
					auditBuilder.buildAudits(contractor, getUser());

					if (permissions.isOperatorCorporate() && conAudit.getId() > 0) {
						boolean hasCao = false;
						for (ContractorAuditOperator cao : conAudit.getOperators()) {
							if (cao.isVisibleTo(permissions)) {
								cao.setVisible(true);
								caoDao.save(cao);
								hasCao = true;
							}
						}
						if (hasCao) {
							redirect("AuditCat.action?auditID=" + conAudit.getId());
							return BLANK;
						}
					}
					return "saved";
				}
			}
		}
		auditTypeList = auditTypeDAO.findAll(permissions, true, auditClass);

		return SUCCESS;
	}

	public List<ContractorAuditOperator> getCaoList() {
		List<ContractorAuditOperator> caoList = caoDao.findByContractorAccount(contractor.getId(), permissions);

		Collections.sort(caoList, new Comparator<ContractorAuditOperator>() {
			@Override
			public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
				return o1.getOperator().compareTo(o2.getOperator());
			}
		});

		return caoList;
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

	public boolean isManuallyAddAudit() {
		if (permissions.isContractor()) {
			return true;
		}

		if (permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Edit)) {
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
		} catch (Exception e) { }
		
		return null;
	}
	
	public Map<String, Set<AuditData>> getCertMap() {
		return certMap;
	}

	public Map<String, Map<AuditType, Set<AuditData>>> getPolicies() {
		return policies;
	}
	
	public String[] getPolicyOrder() {
		return policyOrder;
	}
}
