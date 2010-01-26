package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;


@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	private AuditTypeDAO auditTypeDAO;
	private CertificateDAO certificateDAO;
	private ContractorAuditOperatorDAO caoDao;
	private int selectedAudit;
	private int selectedOperator;
	private String auditFor;
	private List<AuditType> auditTypeList;
	private AuditTypeClass auditClass = AuditTypeClass.Policy;

	private AuditBuilder auditBuilder;

	private Map<ContractorAudit, List<ContractorAuditOperator>> requested = new HashMap<ContractorAudit, List<ContractorAuditOperator>>();
	private Map<ContractorAudit, List<ContractorAuditOperator>> current = new HashMap<ContractorAudit, List<ContractorAuditOperator>>();
	private Set<ContractorAudit> expiredAudits = new HashSet<ContractorAudit>();
	private Set<ContractorAudit> others = new HashSet<ContractorAudit>();

	public ConInsureGuard(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditTypeDAO auditTypeDAO,
			AuditBuilder auditBuilder, CertificateDAO certificateDAO, ContractorAuditOperatorDAO caoDao) {
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

		List<ContractorAuditOperator> caoList = getCaoList();

		for (ContractorAuditOperator cao : caoList) {
			if (cao.getAudit().getAuditStatus().isExpired()) {
				expiredAudits.add(cao.getAudit());
			} else if (cao.getStatus().isPending() || cao.getStatus().isSubmitted() || cao.getStatus().isVerified()
					|| cao.getStatus().isRejected()) {
				if (requested.get(cao.getAudit()) == null)
					requested.put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());

				requested.get(cao.getAudit()).add(cao);
			} else if (cao.getStatus().isApproved() || cao.getStatus().isNotApplicable()) {
				if (current.get(cao.getAudit()) == null)
					current.put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());

				current.get(cao.getAudit()).add(cao);
			}
		}

		for (ContractorAudit ca : contractor.getAudits()) {
			if (ca.getAuditType().getClassType().isPolicy())
				if (!requested.keySet().contains(ca) && !current.keySet().contains(ca) && !expiredAudits.contains(ca))
					others.add(ca);
		}

		if (button != null && button.equals("Add")) {
			if (selectedAudit > 0) {
				boolean alreadyExists = false;
				// if (permissions.isOperator() || permissions.isCorporate())
				// selectedOperator = permissions.getAccountId();

				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (!conAudit.getAuditStatus().isExpired() && conAudit.getAuditType().getId() == selectedAudit
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
					conAudit.changeStatus(AuditStatus.Pending, getUser());
					if (selectedOperator != 0) {
						conAudit.setRequestingOpAccount(new OperatorAccount());
						conAudit.getRequestingOpAccount().setId(selectedOperator);
					}
					conAudit.setPercentComplete(0);
					conAudit.setPercentVerified(0);
					conAudit.setManuallyAdded(true);
					conAudit = auditDao.save(conAudit);

					addNote(conAudit.getContractorAccount(), "Added " + auditType.getAuditName() + " manually",
							NoteCategory.Insurance);

					contractor.getAudits().add(conAudit);
					auditBuilder.setUser(getUser());
					auditBuilder.buildAudits(contractor);

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

	public Set<AuditType> getRequiredAuditTypeNames() {
		Set<AuditType> result = new HashSet<AuditType>();
		for (ContractorOperator co : contractor.getOperators()) {
			for (AuditOperator ao : co.getOperatorAccount().getVisibleAudits()) {
				if (ao.getAuditType().getClassType().isPolicy()) {
					if (permissions.isOperatorCorporate()
							&& permissions.getAccountId() == co.getOperatorAccount().getId()) {
						if (ao.isCanSee() && ao.isCanEdit() && !result.contains(ao.getAuditType())) {
							result.add(ao.getAuditType());
						}
					} else {
						result.add(ao.getAuditType());

					}
				}
			}
		}
		return result;
	}

	public List<Certificate> getCertificates() {
		return certificateDAO.findByConId(contractor.getId(), permissions, false);
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getRequested() {
		return requested;
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getCurrent() {
		return current;
	}

	public Set<ContractorAudit> getExpiredAudits() {
		return expiredAudits;
	}

	public Set<ContractorAudit> getOthers() {
		return others;
	}
}
