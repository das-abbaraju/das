package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.jpa.entities.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.util.PicsDateFormat;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {

	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;

	private Map<Certificate, Set<String>> uploadedCertificatesClientSites;
	private Map<Certificate, Set<String>> currentCertificatesClientSites;
	private Map<Certificate, Set<String>> expiredCertificatesClientSites;

	private Map<ContractorAudit, List<ContractorAuditOperator>> currentPoliciesMap;
	private Map<ContractorAudit, List<ContractorAuditOperator>> expiredPoliciesMap;

	private List<WcbInfo> wcbFiles;
	private List<WcbInfo> expiredWcbFiles;

	public ConInsureGuard() {
		this.noteCategory = NoteCategory.Insurance;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		findContractor();

		initializeMaps();

		populatePolicyMaps();

		Comparator<WcbInfo> comparator = new Comparator<WcbInfo>() {
			public int compare(WcbInfo o1, WcbInfo o2) {
				if (o1.getYear() != o2.getYear())
					return o1.getYear() - o2.getYear();
				if (o1.getAuditName().compareTo(o2.getAuditName()) != 0)
					return o1.getAuditName().compareTo(o2.getAuditName());
				return o1.questionId - o2.questionId;
			}
		};

		Collections.sort(wcbFiles, comparator);
		Collections.sort(expiredWcbFiles, comparator);

		populateCertificates();

		return SUCCESS;
	}

	private void initializeMaps() {
		uploadedCertificatesClientSites = new HashMap<Certificate, Set<String>>();
		currentCertificatesClientSites = new HashMap<Certificate, Set<String>>();
		expiredCertificatesClientSites = new HashMap<Certificate, Set<String>>();

		currentPoliciesMap = new TreeMap<ContractorAudit, List<ContractorAuditOperator>>(
				new Comparator<ContractorAudit>() {
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						return (o1.getAuditType().getName().compareTo(o2
								.getAuditType().getName()));
					}
				});

		expiredPoliciesMap = new TreeMap<ContractorAudit, List<ContractorAuditOperator>>(
				new Comparator<ContractorAudit>() {
					public int compare(ContractorAudit o1, ContractorAudit o2) {
						return (o1.getAuditType().getName().compareTo(o2
								.getAuditType().getName()));
					}
				});

		wcbFiles = new ArrayList<WcbInfo>();
		expiredWcbFiles = new ArrayList<WcbInfo>();
	}

	private void populatePolicyMaps() {
		List<ContractorAudit> audits = contractorAuditDAO.findByContractor(contractor.getId());

		for (ContractorAudit audit : audits) {
			if ((audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
			|| audit.getAuditType().getId() == AuditType.IHG_INSURANCE_QUESTIONAIRE)
					&& auditApplies(audit)) {
				if (audit.isExpired()) {
					expiredPoliciesMap.put(audit, null);
				} else {
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

				if (audit.getAuditType().isWCB()) {
					for (AuditData data : audit.getData()) {
						if (data.getQuestion().getQuestionType().equals("File")
								&& !Strings.isEmpty(data.getAnswer())) {
							WcbInfo wcb = new WcbInfo();
							wcb.setAuditId(audit.getId());
							wcb.setQuestionId(data.getQuestion().getId());
							wcb.setYear(Integer.parseInt(audit.getAuditFor()));
							wcb.setExpires(audit.getExpiresDate());
							wcb.setTitle(getText("ConInsureGUARD.WCB." + data.getQuestion().getId()));

							wcb.setAuditName(getTextNullSafeParameterized(
									"ConInsureGUARD.WcbName", audit
											.getAuditType().getName()
											.toString(), audit.getAuditFor()));

							for (ContractorAuditOperator cao : audit
									.getOperatorsVisible()) {
								if (permissions.canSeeAudit(audit.getAuditType())) {
									if (permissions.isOperatorCorporate()) {
										if (cao.isVisibleTo(permissions)) {
											wcb.getOperators().add(cao.getOperator());
										}
									} else {
										wcb.getOperators().add(cao.getOperator());
									}
								}
							}

							if (!audit.isExpired()) {
								wcbFiles.add(wcb);
							} else {
								expiredWcbFiles.add(wcb);
							}
						}
					}
				}
			}
		}
	}

	private void populateCertificates() {
		for (Certificate certificate : getCertificates()) {
			List<AuditData> certificateAudits = auditDataDAO.findByCertificateID(
					contractor.getId(), certificate.getId());

			if (CollectionUtils.isEmpty(certificateAudits)) {
				uploadedCertificatesClientSites.put(certificate, null);
				continue;
			}

			if (certificate.isExpired()) {
				associateClientSitesWithCertificate(certificateAudits, expiredCertificatesClientSites, certificate);
			} else {
				associateClientSitesWithCertificate(certificateAudits, currentCertificatesClientSites, certificate);
			}
		}
	}

	private void associateClientSitesWithCertificate(List<AuditData> certificateAudits,
			Map<Certificate, Set<String>> certificates, Certificate certificate) {

		Set<String> clientSites = certificates.get(certificate);
		if (CollectionUtils.isEmpty(clientSites)) {
			clientSites = new HashSet<String>();
		}

		for (AuditData audit : certificateAudits) {
			String clientSite = audit.getQuestion().getCategory().getName().toString();
			clientSites.add(clientSite);
		}

		certificates.put(certificate, clientSites);
	}

	@Override
	public List<Certificate> getCertificates() {
		if (certificates == null) {
			super.getCertificates();

			Collections.sort(certificates, new Comparator<Certificate>() {
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

		if (permissions.isContractor()
				&& audit.getContractorAccount().getId() == permissions
						.getAccountId())
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
		return DateBean.format(d, PicsDateFormat.TwoDigitYear);
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getCurrentPoliciesMap() {
		return currentPoliciesMap;
	}

	public void setCurrentPoliciesMap(
			Map<ContractorAudit, List<ContractorAuditOperator>> currentPoliciesMap) {
		this.currentPoliciesMap = currentPoliciesMap;
	}

	public Map<ContractorAudit, List<ContractorAuditOperator>> getExpiredPoliciesMap() {
		return expiredPoliciesMap;
	}

	public void setExpiredPoliciesMap(
			Map<ContractorAudit, List<ContractorAuditOperator>> expiredPoliciesMap) {
		this.expiredPoliciesMap = expiredPoliciesMap;
	}

	public List<WcbInfo> getWcbFiles() {
		return wcbFiles;
	}

	public void setWcbFiles(List<WcbInfo> wcbFiles) {
		this.wcbFiles = wcbFiles;
	}

	public List<WcbInfo> getExpiredWcbFiles() {
		return expiredWcbFiles;
	}

	public void setExpiredWcbFiles(List<WcbInfo> expiredWcbFiles) {
		this.expiredWcbFiles = expiredWcbFiles;
	}

	public Map<Certificate, Set<String>> getUploadedCertificatesClientSites() {
		return uploadedCertificatesClientSites;
	}

	public Map<Certificate, Set<String>> getCurrentCertificatesClientSites() {
		return currentCertificatesClientSites;
	}

	public Map<Certificate, Set<String>> getExpiredCertificatesClientSites() {
		return expiredCertificatesClientSites;
	}

	private class WcbInfo {
		String title;
		String auditName;
		int year;
		Date expires;
		int auditId;
		int questionId;
		List<OperatorAccount> operators = new ArrayList<OperatorAccount>();

		public String getAuditName() {
			return auditName;
		}

		public void setAuditName(String auditName) {
			this.auditName = auditName;
		}

		public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		public Date getExpires() {
			return expires;
		}

		public void setExpires(Date expires) {
			this.expires = expires;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getAuditId() {
			return auditId;
		}

		public void setAuditId(int auditId) {
			this.auditId = auditId;
		}

		public int getQuestionId() {
			return questionId;
		}

		public void setQuestionId(int dataId) {
			this.questionId = dataId;
		}

		public List<OperatorAccount> getOperators() {
			return operators;
		}

		public void setOperators(List<OperatorAccount> operators) {
			this.operators = operators;
		}
	}; // end WcbInfo inner class

}
