package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ConInsureGuard extends ContractorActionSupport {
	@Autowired
	private AuditDataDAO auditDataDAO;
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;

	String[] certTypes = { "Current", "Expired", "Uploaded" };

	// private Map<String, Map<Certificate, String>> certificatesMap;
	private Table<String, Certificate, String> certificatesMap;

	private Map<ContractorAudit, List<ContractorAuditOperator>> currentPoliciesMap;
	private Map<ContractorAudit, List<ContractorAuditOperator>> expiredPoliciesMap;

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
	};

	private List<WcbInfo> wcbFiles;

	public ConInsureGuard() {
		this.noteCategory = NoteCategory.Insurance;
		subHeading = "InsureGUARD&trade;";
	}

	public String execute() throws Exception {
		findContractor();

		// Initializing Maps
		certificatesMap = TreeBasedTable.create();

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

		// Populating policy Maps
		for (ContractorAudit audit : contractorAuditDAO
				.findByContractor(contractor.getId())) {
			if (audit.getAuditType().getClassType()
					.equals(AuditTypeClass.Policy)
					&& auditApplies(audit)) {
				if (audit.isExpired())
					expiredPoliciesMap.put(audit, null);
				else {
					List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();
					for (ContractorAuditOperator cao : audit
							.getOperatorsVisible()) {
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

				if (audit.getAuditType().isWCB() && !audit.isExpired()) {
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
							wcbFiles.add(wcb);
						}
					}
				}
			}
		}

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
		
		for (Certificate certificate : getCertificates()) {
			List<AuditData> auditData = auditDataDAO.findByCertificateID(
					contractor.getId(), certificate.getId());

			if (auditData.size() == 0) {
				certificatesMap.put("Uploaded", certificate, "");
			} else {
				if (certificate.isExpired()) {
					certificatesMap.put("Expired", certificate, auditData
							.get(0).getQuestion().getCategory().getName()
							.toString());
				} else {
					certificatesMap.put("Current", certificate, auditData
							.get(0).getQuestion().getCategory().getName()
							.toString());
				}
			}
		}
		return SUCCESS;
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
		return DateBean.format(d, "yy");
	}

	public Table<String, Certificate, String> getCertificatesMap() {
		return certificatesMap;
	}

	public void setCertificatesMap(
			Table<String, Certificate, String> certificatesMap) {
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
}
