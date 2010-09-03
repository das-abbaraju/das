package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilderController;
import com.picsauditing.PICS.ContractorFlagCriteriaList;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.InvoiceItemDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private AuditBuilderController auditBuilder;
	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO dataDAO;
	private FlagDataDAO flagDataDAO;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private InvoiceItemDAO invoiceItemDAO;
	private UserDAO userDAO;
	private NaicsDAO naicsDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	private ContractorOperator co;
	private int opID;

	private ContractorWatch watch;

	private List<ContractorAudit> docuGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> auditGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> insureGUARD = new ArrayList<ContractorAudit>();
	private List<AuditData> servicesPerformed = null;

	private ContractorFlagCriteriaList problems;

	private ContractorFlagCriteriaList criteriaList;

	private Map<FlagColor, Integer> flagCounts;

	private OshaDisplay oshaDisplay;

	public ContractorDashboard(AuditBuilderController auditBuilder, ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDAO, AuditDataDAO dataDAO, FlagDataDAO flagDataDAO,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO, InvoiceItemDAO invoiceItemDAO,
			UserDAO userDAO, NaicsDAO naicsDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.dataDAO = dataDAO;
		this.flagDataDAO = flagDataDAO;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.userDAO = userDAO;
		this.naicsDAO = naicsDAO;
		this.subHeading = "Account Summary";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();

		if (button != null && button.contains("Watch")) {
			tryPermissions(OpPerms.ContractorWatch, OpType.Edit);
			getWatch();

			if ("Start Watch".equals(button) && watch == null) {
				User user = userDAO.find(permissions.getUserId());
				watch = new ContractorWatch();
				watch.setContractor(contractor);
				watch.setUser(user);
				watch.setAuditColumns(permissions);

				userDAO.save(watch);
			}

			if ("Stop Watch".equals(button) && watch != null)
				userDAO.remove(watch);
		}

		if ("AddTag".equals(button)) {
			if (tagId > 0) {
				ContractorTag cTag = new ContractorTag();
				cTag.setContractor(contractor);
				cTag.setTag(new OperatorTag());
				cTag.getTag().setId(tagId);
				cTag.setAuditColumns(permissions);
				contractor.getOperatorTags().add(cTag);
				accountDao.save(contractor);
			}
		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
		}

		if ("Upgrade to Full Membership".equals(button)) {
			// See also ReportBiddingContractors Upgrade
			contractor.setAcceptsBids(false);
			contractor.setRenew(true);
			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf()) {
					for (ContractorAuditOperator cao : cAudit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							cao.changeStatus(AuditStatus.Pending, permissions);
							auditDao.save(cao);
						}
					}
				}
			}
			// Setting the payment Expires date to today
			for (Invoice invoice : contractor.getInvoices()) {
				for (InvoiceItem invoiceItem : invoice.getItems()) {
					if (invoiceItem.getInvoiceFee().getId() == InvoiceFee.BIDONLY) {
						invoiceItem.setPaymentExpires(new Date());
						invoiceItemDAO.save(invoiceItem);
					}
				}
			}

			if (permissions.isOperator()) {
				for (ContractorOperator cOperator : contractor.getNonCorporateOperators()) {
					if (cOperator.getOperatorAccount().getId() == permissions.getAccountId()) {
						cOperator.setWorkStatus("Y");
						cOperator.setAuditColumns(permissions);
						contractorOperatorDAO.save(cOperator);
						break;
					}
				}
			}

			contractor.incrementRecalculation();
			contractor.setAuditColumns(permissions);
			accountDao.save(contractor);

			addNote(contractor, "Upgraded the Bid Only Account to a full membership.", NoteCategory.General);

			// Sending a Email to the contractor for upgrade
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(73); // Trial Contractor Account Approval
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
			emailBuilder.addToken("permissions", permissions);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(60);
			emailQueue.setFromAddress("billing@picsauditing.com");
			if(permissions.isOperator())
				emailQueue.setViewableById(permissions.getTopAccountID());
			else
				emailQueue.setViewableById(Account.EVERYONE);
			EmailSender.send(emailQueue);

			if (permissions.isContractor()) {
				ServletActionContext.getResponse().sendRedirect(
						"BillingDetail.action?id=" + contractor.getId() + "&button=Create");
				return BLANK;
			}
		}

		if (permissions.isOperatorCorporate()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}

		if (contractor.getNonCorporateOperators().size() > 0) {
			auditBuilder.buildAudits(this.contractor, getUser());
		}

		if (opID == 0 && permissions.isOperatorCorporate())
			opID = permissions.getAccountId();

		co = contractorOperatorDAO.find(id, opID);

		if (contractor.getNonCorporateOperators().size() == 1) {
			co = contractor.getNonCorporateOperators().get(0);
			opID = co.getOperatorAccount().getId();
		}

		for (ContractorAudit audit : auditDao.findNonExpiredByContractor(id)) {
			if (permissions.canSeeAudit(audit.getAuditType())) {
				if (audit.getAuditType().getClassType().isPolicy())
					insureGUARD.add(audit);
				else if (audit.getAuditType().getClassType().isPqf() || audit.getAuditType().isAnnualAddendum())
					docuGUARD.add(audit);
				else
					auditGUARD.add(audit);
			}
		}

		return SUCCESS;
	}

	public ContractorOperator getCo() {
		return co;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public List<ContractorAudit> getDocuGUARD() {
		return docuGUARD;
	}

	public List<ContractorAudit> getAuditGUARD() {
		return auditGUARD;
	}

	public List<ContractorAudit> getInsureGUARD() {
		return insureGUARD;
	}

	public List<AuditData> getServicesPerformed() {
		if (servicesPerformed == null) {
			servicesPerformed = new ArrayList<AuditData>();
			for (AuditData auditData : dataDAO.findServicesPerformed(id)) {
				if (auditData.getAnswer().startsWith("C"))
					servicesPerformed.add(auditData);
			}

			for (AuditData auditData : dataDAO.findServicesPerformed(id)) {
				if (!auditData.getAnswer().startsWith("C") && auditData.getAnswer().endsWith("S"))
					servicesPerformed.add(auditData);
			}
		}
		return servicesPerformed;
	}

	public Map<Integer, List<ContractorOperator>> getActiveOperatorsMap() {

		Map<Integer, List<ContractorOperator>> result = new TreeMap<Integer, List<ContractorOperator>>();
		List<ContractorOperator> ops = getActiveOperators();

		result.put(0, ops.subList(0, ops.size() / 2));
		result.put(1, ops.subList(ops.size() / 2, ops.size()));

		return result;
	}

	public ContractorFlagCriteriaList getProblems() {
		if (problems == null)
			problems = new ContractorFlagCriteriaList(flagDataDAO.findProblems(id, opID));

		return problems;
	}

	public ContractorFlagCriteriaList getCriteriaList() {
		if (criteriaList == null)
			criteriaList = new ContractorFlagCriteriaList(flagDataDAO.findByContractorAndOperator(id, opID));
		return criteriaList;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		return operatorTagDAO.findByOperator(permissions.getAccountId(), true);
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}

	public boolean isCanUpgrade() {
		if (permissions.isContractor())
			return true;
		if (permissions.seesAllContractors())
			return true;
		if (permissions.isOperator() && permissions.hasPermission(OpPerms.ViewTrialAccounts, OpType.Edit))
			return true;

		return false;
	}

	public boolean isShowLogo() {
		File f = new File(getFtpDir() + "/logos/" + contractor.getLogoFile());

		return f.exists();
	}

	public Map<FlagColor, Integer> getFlagCounts() {
		if (flagCounts == null) {
			flagCounts = new LinkedHashMap<FlagColor, Integer>();
			flagCounts.put(FlagColor.Red, 0);
			flagCounts.put(FlagColor.Amber, 0);
			flagCounts.put(FlagColor.Green, 0);

			for (ContractorOperator contractorOperator : getActiveOperators()) {
				flagCounts
						.put(contractorOperator.getFlagColor(), flagCounts.get(contractorOperator.getFlagColor()) + 1);
			}

			Iterator<Map.Entry<FlagColor, Integer>> iter = flagCounts.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<FlagColor, Integer> entry = iter.next();
				if (entry.getValue() <= 0)
					iter.remove();
			}
		}
		return flagCounts;
	}

	public OshaDisplay getOshaDisplay() {
		if (oshaDisplay == null) {
			oshaDisplay = new OshaDisplay(getActiveOperators());
		}

		return oshaDisplay;
	}

	private ContractorWatch getWatch() {
		if (watch == null) {
			List<ContractorWatch> watched = userDAO.findContractorWatch(permissions.getUserId());
			for (ContractorWatch w : watched) {
				if (w.getContractor().equals(contractor)) {
					watch = w;
					break;
				}
			}
		}

		return watch;
	}

	public boolean isWatched() {
		return getWatch() != null;
	}

	public class OshaDisplay {

		private Set<String> auditForSet = new LinkedHashSet<String>();
		private Set<String> rateTypeSet = new LinkedHashSet<String>();

		private Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();

		public OshaDisplay(List<ContractorOperator> contractorOperators) {

			OshaOrganizer organizer = contractor.getOshaOrganizer();

			for (MultiYearScope scope : new MultiYearScope[] { MultiYearScope.ThreeYearsAgo,
					MultiYearScope.TwoYearsAgo, MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage }) {
				OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
				String auditFor = findAuditFor(organizer, scope);
				if (auditFor != null) {

					auditForSet.add(auditFor);

					for (OshaRateType rate : new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute,
							OshaRateType.Fatalities }) {
						put(rate.getDescription(), auditFor, format(organizer.getRate(OshaType.OSHA, scope, rate)));
					}

					put("Hours Worked", auditFor, format(audit.getManHours()));
				}
			}

			String ind = "Industry";
			auditForSet.add(ind);
			
			if (data.get(OshaRateType.TrirAbsolute.getDescription()) != null)
				put(OshaRateType.TrirAbsolute.getDescription(), ind, format(naicsDAO.getIndustryAverage(false, contractor.getNaics())));
			if (data.get(OshaRateType.LwcrAbsolute.getDescription()) != null)
				put(OshaRateType.LwcrAbsolute.getDescription(), ind, format(naicsDAO.getIndustryAverage(true, contractor.getNaics())));

			Set<OperatorAccount> inheritedOperators = new LinkedHashSet<OperatorAccount>();
			for (ContractorOperator co : contractorOperators) {
				inheritedOperators.add(co.getOperatorAccount().getInheritFlagCriteria());
			}

			for (OperatorAccount o : inheritedOperators) {
				for (FlagCriteriaOperator fco : o.getFlagCriteriaInherited()) {
					if (OshaRateType.TrirAbsolute.equals(fco.getCriteria().getOshaRateType())
							|| OshaRateType.LwcrAbsolute.equals(fco.getCriteria().getOshaRateType())) {
						MultiYearScope scope = fco.getCriteria().getMultiYearScope();
						String auditFor = findAuditFor(organizer, scope);
						if (auditFor != null) {
							String suffix = getOshaSuffix(fco.getCriteria().getOshaRateType());
							String operatorDisplay = getOperatorDisplay(o, suffix);

							if (getData(operatorDisplay, auditFor) != null)
								put(operatorDisplay, auditFor, getData(operatorDisplay, auditFor) + ", "
										+ getFlagDescription(fco));
							else
								put(operatorDisplay, auditFor, getFlagDescription(fco));
						}
					}
				}
			}

			for (Map.Entry<String, AuditData> entry : contractor.getEmrs().entrySet()) {
				if (entry.getValue() != null) {
					put("EMR", entry.getKey(), entry.getValue().getAnswer());
				}
			}

			if (data.get("EMR") != null) {
				for (OperatorAccount o : inheritedOperators) {
					for (FlagCriteriaOperator fco : o.getFlagCriteriaInherited()) {
						if (fco.getCriteria().getQuestion() != null
								&& fco.getCriteria().getQuestion().getId() == AuditQuestion.EMR) {
							String operatorDisplay = getOperatorDisplay(o, " EMR");
							String auditFor = fco.getCriteria().getMultiYearScope().getAuditFor();

							if (getData(operatorDisplay, auditFor) != null)
								put(operatorDisplay, auditFor, getData(operatorDisplay, auditFor) + ", "
										+ getFlagDescription(fco));
							else
								put(operatorDisplay, auditFor, getFlagDescription(fco));
						}
					}
				}
			}

			buildRateTypeSet(inheritedOperators);
		}

		private String getFlagDescription(FlagCriteriaOperator fco) {
			return "<nobr class=\"" + fco.getFlag() + "\">" + fco.getShortDescription() + "</nobr>";
		}

		private void put(String k1, String k2, String v) {
			if (data.get(k1) == null)
				data.put(k1, new HashMap<String, String>());

			data.get(k1).put(k2, v);
		}

		public String getData(String k1, String k2) {
			try {
				return data.get(k1).get(k2);
			} catch (Exception e) {
				return null;
			}
		}

		private void buildRateTypeSet(Collection<OperatorAccount> operators) {
			for (OshaRateType ort : Arrays.asList(OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute,
					OshaRateType.Fatalities)) {
				if (data.get(ort.getDescription()) != null) {
					rateTypeSet.add(ort.getDescription());
					for (OperatorAccount operatorAccount : operators) {
						String disp = getOperatorDisplay(operatorAccount, getOshaSuffix(ort));
						if (data.get(disp) != null)
							rateTypeSet.add(disp);
					}
				}
			}
			if (data.get("EMR") != null) {
				rateTypeSet.add("EMR");
				for (OperatorAccount operatorAccount : operators) {
					String disp = getOperatorDisplay(operatorAccount, " EMR");
					if (data.get(disp) != null)
						rateTypeSet.add(disp);
				}
			}
			if (data.get("Hours Worked") != null)
				rateTypeSet.add("Hours Worked");
		}

		private String getOshaSuffix(OshaRateType rateType) {
			if (OshaRateType.TrirAbsolute.equals(rateType))
				return " TRIR";
			else if (OshaRateType.LwcrAbsolute.equals(rateType))
				return " LWCR";
			else if (OshaRateType.Fatalities.equals(rateType))
				return " Fatalities";

			return "";
		}

		private String getOperatorDisplay(OperatorAccount operator, String suffix) {
			return "P:" + operator.getName() + suffix;
		}

		private String findAuditFor(OshaOrganizer organizer, MultiYearScope scope) {
			OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
			String auditFor = "";
			if (audit == null) {
				auditFor = null;
			} else if (audit.getConAudit() == null) {
				if (scope.equals(MultiYearScope.ThreeYearAverage))
					auditFor = "Average";
			} else
				auditFor = audit.getConAudit().getAuditFor();
			return auditFor;
		}

		public Set<String> getAuditForSet() {
			return auditForSet;
		}

		public Set<String> getRateTypeSet() {
			return rateTypeSet;
		}

		public boolean isHasData() {
			return rateTypeSet.size() > 0 && auditForSet.size() > 0;
		}
	}
}
