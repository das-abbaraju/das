package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	@Autowired
	private CertificateDAO certificateDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private AuditCategoryRuleCache categoryRuleCache;

	/**
	 * A list of Insurance Certificates that are currently being used by a CAO
	 */
	private List<Certificate> active;
	// Audit data
	private Map<String, Map<ContractorAudit, List<ContractorAuditOperator>>> status;
	private Map<ContractorAuditOperator, Certificate> caoCert;
	private Map<Certificate, List<ContractorAuditOperator>> certCaos;

	private static final String[] STATUSES = new String[] { "Pending", "Current", "Expired", "Other" };

	public ConInsureGuard() {
		this.noteCategory = NoteCategory.Insurance;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		findContractor();

		List<AuditData> certificates = certificateDAO.findConCertsAuditData(id);

		status = new HashMap<String, Map<ContractorAudit, List<ContractorAuditOperator>>>();
		for (String igStatus : STATUSES)
			status.put(igStatus, new HashMap<ContractorAudit, List<ContractorAuditOperator>>());

		caoCert = new HashMap<ContractorAuditOperator, Certificate>();
		certCaos = new HashMap<Certificate, List<ContractorAuditOperator>>();

		Set<AuditCategoryRule> rules = new HashSet<AuditCategoryRule>();
		{
			List<AuditType> policies = auditTypeDAO.findWhere("t.classType = 'Policy'");
			for (AuditType auditType : policies) {
				rules.addAll(categoryRuleCache.getRules(contractor, auditType));
			}
		}

		for (AuditData certificate : certificates) {
			ContractorAuditOperator cao = findCao(rules, certificate);
			Certificate cert = getCertByID(certificate.getAnswer());

			if (cert != null && cao != null) {
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

	public List<Certificate> getActive() {
		if (active == null) {
			active = new Grepper<Certificate>() {
				public boolean check(Certificate t) {
					return !t.isExpired();
				};
			}.grep(getCertificates());
		}

		return active;
	}

	public Certificate getCertByID(String certID) {
		int id = 0;

		try {
			id = Integer.parseInt(certID);

			for (Certificate c : getCertificates()) {
				if (c.getId() == id)
					return c;
			}
		} catch (NumberFormatException e) {
			System.out.println("Could not find certificate: " + certID);
		}

		return null;
	}

	public String getStatusName(AuditStatus status) {
		if (status.isIncomplete())
			return "Rejected";
		else if (status.isNotApplicable())
			return status.getButton();
		else
			return status.name();
	}

	private ContractorAuditOperator findCao(Collection<AuditCategoryRule> rules, AuditData d) {
		for (AuditCategoryRule rule : rules) {
			if (rule.isInclude() && rule.isApplies(d.getQuestion().getCategory())) {
				for (ContractorAuditOperator cao : d.getAudit().getOperators()) {
					if (permissions.isOperatorCorporate()) {
						if (permissions.getVisibleAccounts().contains(cao.getOperator().getId()))
							if (cao.getOperator().equals(rule.getOperatorAccount()))
								return cao;
					} else if (cao.getOperator().equals(rule.getOperatorAccount()))
						return cao;
				}
			}
		}

		return null;
	}

	private void addStatus(ContractorAuditOperator cao) {
		if (cao.getStatus().isPendingSubmittedResubmitted())
			addStatus(cao, STATUSES[0]);
		else if (cao.getStatus().isApproved())
			addStatus(cao, STATUSES[1]);
		else if (cao.getStatus().isExpired())
			addStatus(cao, STATUSES[2]);
		else
			addStatus(cao, STATUSES[3]);
	}

	private void addStatus(ContractorAuditOperator cao, String s) {
		if (status.get(s).get(cao.getAudit()) == null)
			status.get(s).put(cao.getAudit(), new ArrayList<ContractorAuditOperator>());

		if (!status.get(s).get(cao.getAudit()).contains(cao))
			status.get(s).get(cao.getAudit()).add(cao);
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

	public static List<String> getStatuses() {
		return Collections.unmodifiableList(Arrays.asList(STATUSES));
	}
}
