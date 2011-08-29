package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.picsauditing.PICS.ContractorFlagCriteriaList;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private AuditDataDAO dataDAO;
	@Autowired
	private FlagDataDAO flagDataDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;
	@Autowired
	private ContractorTagDAO contractorTagDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private NaicsDAO naicsDAO;
	@Autowired
	private FlagCriteriaContractorDAO flagCriteriaContractorDAO;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private AuditTypeRuleCache auditTypeRuleCache;
	@Autowired
	private EmailSenderSpring emailSender;

	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;
	private boolean runTagConCronAjax = false;

	private ContractorOperator co;
	private int opID;

	private ContractorWatch watch;

	private List<ContractorAudit> docuGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> auditGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> insureGUARD = new ArrayList<ContractorAudit>();
	private List<AuditData> servicesPerformed = null;
	private Map<Integer, FlagCriteriaContractor> fccMap = null;
	private Map<ContractorAuditOperator, AuditStatus> prevStats = null;

	private ContractorFlagCriteriaList problems;

	private ContractorFlagCriteriaList criteriaList;

	private Map<FlagColor, Integer> flagCounts;

	private OshaDisplay oshaDisplay;

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		this.subHeading = getText("ContractorView.title");
		findContractor();

		if (permissions.isOperatorCorporate()
				&& (contractor.getStatus().isDeactivated() || contractor.getStatus().isDeleted()))
			throw new NoRightsException("PICS Administrator");

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
			// TODO Move this into a new class
			// and then redirect back to Dashboard if necessary
			if (tagId > 0) {
				ContractorTag cTag = new ContractorTag();
				cTag.setContractor(contractor);
				cTag.setTag(new OperatorTag());
				cTag.getTag().setId(tagId);
				cTag.setAuditColumns(permissions);
				contractor.getOperatorTags().add(cTag);
				contractor.incrementRecalculation(10);
				accountDao.save(contractor);
				for (AuditTypeRule atr : auditTypeRuleCache.getRules(contractor)) {
					if (Objects.equal(cTag.getTag(), atr.getTag())) {
						runTagConCronAjax = true;
						break;
					}
				}
			}
		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
			contractor.incrementRecalculation(10);
			accountDao.save(contractor);
		}

		if ("Upgrade to Full Membership".equals(button)) {

			// TODO Move this into a new class (maybe Payment Options or Facilities or Billing Details)
			// and then redirect back to Dashboard if necessary
			// See also ReportBiddingContractors/RequestNewContractor Upgrade
			contractor.setAccountLevel(AccountLevel.Full);
			contractor.setRenew(true);

			auditBuilder.buildAudits(contractor);

			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf()) {
					for (ContractorAuditOperator cao : cAudit.getOperators()) {
						if (cao.getStatus().after(AuditStatus.Pending)) {
							cao.changeStatus(AuditStatus.Pending, permissions);
							auditDao.save(cao);
						}
					}

					auditBuilder.recalculateCategories(cAudit);
					auditPercentCalculator.recalcAllAuditCatDatas(cAudit);
					auditPercentCalculator.percentCalculateComplete(cAudit);
					auditDao.save(cAudit);
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
			if (permissions.isOperator())
				emailQueue.setViewableById(permissions.getTopAccountID());
			else
				emailQueue.setViewableById(Account.EVERYONE);
			emailSender.send(emailQueue);

			if (permissions.isContractor()) {
				ServletActionContext.getResponse().sendRedirect(
						"BillingDetail.action?id=" + contractor.getId() + "&button=Create");
				return BLANK;
			}
		}

		if ("Synchronize Contractor".equals(button)) {
			String redirectUrl = "ContractorView.action?id=" + id;
			return redirect("ContractorCronAjax.action?conID=" + id + "&steps=All&redirectUrl=" + redirectUrl);
		}

		if (permissions.isOperatorCorporate()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
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

	public String getCriteriaLabel(int fcID) {
		if (fccMap == null) {
			fccMap = new HashMap<Integer, FlagCriteriaContractor>();
			List<FlagCriteriaContractor> flagCriteriaConList = flagCriteriaContractorDAO.findByContractor(id);
			for (FlagCriteriaContractor fcc : flagCriteriaConList)
				fccMap.put(fcc.getCriteria().getId(), fcc);
		}
		FlagCriteriaContractor fcc = fccMap.get(fcID);
		String result = "";
		if (fcc != null) {
			if (!Strings.isEmpty(fcc.getAnswer2())) {
				String answer = fcc.getAnswer2().split("<br/>")[0];
				if (answer != null && answer.contains("Year"))
					result = " for " + answer;
			}
		}
		return result;
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
						Float value = organizer.getRate(OshaType.OSHA, scope, rate);
						if (rate.equals(OshaRateType.Fatalities)) {
							put(rate.getDescription(), auditFor, Integer.toString((value.intValue())));
						} else {
							put(rate.getDescription(), auditFor, format(value));
						}
					}

					put("Hours Worked", auditFor, format(audit.getManHours()));
				}
			}

			String ind = "Industry";
			if (auditForSet.size() != 0) {
				auditForSet.add(ind);
			}

			if (data.get(OshaRateType.TrirAbsolute.getDescription()) != null)
				put(OshaRateType.TrirAbsolute.getDescription(), ind, format(naicsDAO.getIndustryAverage(false,
						contractor.getNaics())));
			if (data.get(OshaRateType.LwcrAbsolute.getDescription()) != null)
				put(OshaRateType.LwcrAbsolute.getDescription(), ind, format(naicsDAO.getIndustryAverage(true,
						contractor.getNaics())));

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
					auditForSet.add(entry.getKey());
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

	public boolean isRunTagConCronAjax() {
		return runTagConCronAjax;
	}

	public void setRunTagConCronAjax(boolean runAjax) {
		this.runTagConCronAjax = runAjax;
	}

	public boolean isHasOperatorTags() {
		if (permissions.hasPermission(OpPerms.ContractorTags))
			return true;
		if (permissions.hasGroup(959))
			return true;
		return false;
	}

	public Map<ContractorAuditOperator, AuditStatus> getCaoStats(Integer opID) {
		if (prevStats == null) {
			prevStats = new TreeMap<ContractorAuditOperator, AuditStatus>(new Comparator<ContractorAuditOperator>() {
				public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
					return o2.getStatusChangedDate().compareTo(o1.getStatusChangedDate());
				}
			});
			for (ContractorAudit ca : this.getActiveAudits()) {
				for (ContractorAuditOperator cao : ca.getOperators()) {
					if (cao.hasCaop(opID)) {
						if (prevStats.get(cao) == null)
							prevStats.put(cao, null);

						// finding previous status from workflow if it exists
						if (cao.getCaoWorkflow().size() > 0) {
							List<ContractorAuditOperatorWorkflow> caow = cao.getCaoWorkflow();
							Collections.sort(caow, new Comparator<ContractorAuditOperatorWorkflow>() {
								@Override
								public int compare(ContractorAuditOperatorWorkflow o1,
										ContractorAuditOperatorWorkflow o2) {
									return o1.getCreationDate().compareTo(o2.getCreationDate());
								}
							});
							prevStats.put(cao, cao.getCaoWorkflow().get(cao.getCaoWorkflow().size() - 1)
									.getPreviousStatus());
						}

					}
				}
			}
		}
		return prevStats;
	}

}
