package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Objects;
import com.picsauditing.PICS.AccountLevelAdjuster;
import com.picsauditing.PICS.ContractorFlagCriteriaList;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.access.GeneralContractorNotApprovedException;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.FlagCriteriaContractorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.ContractorWatch;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagDataOverride;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.oshadisplay.OshaDisplay;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.NoteFactory;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {
	private static Logger logger = LoggerFactory
			.getLogger(ContractorDashboard.class);

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
	private NaicsDAO naicsDao;
	@Autowired
	private FlagCriteriaContractorDAO flagCriteriaContractorDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private AccountLevelAdjuster accountLevelAdjuster;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private PermissionBuilder permissionBuilder;

	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;
	private boolean runTagConCronAjax = false;
	private Boolean approveGeneralContractorRelationship;

	private ContractorOperator co;
	private int opID;

	private List<ContractorAudit> docuGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> auditGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> insureGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> employeeGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorOperator> generalContractorsNeedingApproval;
	private List<AuditData> servicesPerformed = null;
	private Map<Integer, FlagCriteriaContractor> fccMap = null;
	private Map<ContractorAuditOperator, AuditStatus> prevStats = null;

	private ContractorFlagCriteriaList problems;
	private ContractorFlagCriteriaList criteriaList;
	private Map<FlagColor, Integer> flagCounts;
	private OshaOrganizer oshaOrganizer;
	private OshaDisplay oshaDisplay;
	private Map<OshaType, Boolean> displayOsha;

	private Date earliestIndividualFlagOverride = null;
	private int individualFlagOverrideCount = 0;
	private ContractorOperator corporateFlagOverride = null;

	// Show extremely limited contractor view for operators
	private boolean showBasicsOnly = false;

	@Override
	public String execute() throws Exception {
		if (!forceLogin()) {
			return LOGIN_AJAX;
		}

		this.subHeading = getText("ContractorView.title");

		try {
			findContractor();
		} catch (NoRightsException noRights) {
			if (permissions.isOperatorCorporate()
					&& contractor.isShowInDirectory()) {
				showBasicsOnly = true;
			} else {
				throw noRights;
			}
		} catch (Exception e) {
			throw e;
		}

		if (permissions.isGeneralContractor()
				&& !contractor.isAutoApproveRelationships()) {
			OperatorAccount gc = new OperatorAccount();
			gc.setId(permissions.getAccountId());

			ContractorOperator contractorOperator = contractor
					.getContractorOperatorForOperator(gc);
			if (contractorOperator != null
					&& contractorOperator.getOperatorAccount().getId() == permissions
							.getAccountId()
					&& !contractorOperator.getWorkStatus().isYes()) {
				throw new GeneralContractorNotApprovedException(
						getTextParameterized(
								"ContractorView.ContractorHasNotApprovedGC",
								contractor.getName()));
			}
		}

		setOpIDToFirstClientSiteIfGCFree();

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
				contractorAccountDao.save(contractor);
				noteDAO.save(NoteFactory.generateNoteForTaggingContractor(cTag,
						permissions));
				auditTypeRuleCache.initialize(auditRuleDAO);
				for (AuditTypeRule atr : auditTypeRuleCache
						.getRules(contractor)) {
					if (Objects.equal(cTag.getTag(), atr.getTag())) {
						runTagConCronAjax = true;
						break;
					}
				}
			}
		}

		if ("RemoveTag".equals(button)) {
			noteDAO.save(NoteFactory.generateNoteForRemovingTagFromContractor(
					tagId, permissions));
			contractorTagDAO.remove(tagId);
			contractor.incrementRecalculation(10);
			contractorAccountDao.save(contractor);
		}

		if ("Upgrade to Full Membership".equals(button)) {

			accountLevelAdjuster.upgradeToFullAccount(contractor, permissions);
			addNote(contractor,
					"Upgraded the Bid Only Account to a full membership.",
					NoteCategory.General);

			if (permissions.isOperator()) {
				for (ContractorOperator cOperator : contractor
						.getNonCorporateOperators()) {
					if (cOperator.getOperatorAccount().getId() == permissions
							.getAccountId()) {
						cOperator.setWorkStatus(ApprovalStatus.Y);
						cOperator.cascadeWorkStatusToParent();
						cOperator.setAuditColumns(permissions);
						contractorOperatorDAO.save(cOperator);
						break;
					}
				}
			}

			contractor.incrementRecalculation();
			contractor.setAuditColumns(permissions);
			contractorAccountDao.save(contractor);

			// Sending a Email to the contractor for upgrade
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(73); // Trial Contractor Account Approval
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
			emailBuilder.addToken("permissions", permissions);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setFromAddress(EmailAddressUtils
					.getBillingEmail(contractor.getCurrency()));
			if (permissions.isOperator())
				emailQueue.setViewableById(permissions.getTopAccountID());
			else
				emailQueue.setViewableById(Account.EVERYONE);
			emailSender.send(emailQueue);

			if (permissions.isContractor()) {
				ServletActionContext.getResponse().sendRedirect(
						"BillingDetail.action?id=" + contractor.getId()
								+ "&button=Create");
				return BLANK;
			}
		}

		if ("Synchronize Contractor".equals(button)) {
			String redirectUrl = "ContractorView.action?id=" + id;
			return setUrlForRedirect("ContractorCronAjax.action?conID=" + id
					+ "&steps=All&redirectUrl=" + redirectUrl);
		}

		if (permissions.isOperatorCorporate()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}

		if (opID == 0 && permissions.isOperatorCorporate()) {
			opID = permissions.getAccountId();
		}

		co = contractorOperatorDAO.find(id, opID);

		if (contractor.getNonCorporateOperators().size() == 1) {
			co = contractor.getNonCorporateOperators().get(0);
			opID = co.getOperatorAccount().getId();
		}

		findCorporateOverride();

		calculateEarliestIndividualFlagSummaries();

		for (ContractorAudit audit : auditDao.findNonExpiredByContractor(id)) {
			if (permissions.canSeeAudit(audit.getAuditType())
					&& !audit.hasOnlyInvisibleCaos()) {
				if (audit.getAuditType().getClassType().isPolicy())
					insureGUARD.add(audit);
				else if (audit.getAuditType().getClassType().isPqf()
						|| audit.getAuditType().isAnnualAddendum())
					docuGUARD.add(audit);
				else if (audit.getAuditType().getClassType().isImEmployee())
					employeeGUARD.add(audit);
				else
					auditGUARD.add(audit);
			}
		}

		oshaOrganizer = contractor.getOshaOrganizer();

		oshaDisplay = new OshaDisplay(oshaOrganizer, contractor.getLocale(),
				getActiveOperators(), contractor, naicsDao);
		determineOshaTypesToDisplay();

		return SUCCESS;
	}

	private void determineOshaTypesToDisplay() {
		displayOsha = new HashMap<OshaType, Boolean>();
		displayOsha.put(OshaType.OSHA, false);
		displayOsha.put(OshaType.COHS, false);
		displayOsha.put(OshaType.UK_HSE, false);
		displayOsha.put(OshaType.EMR, false);
		displayOsha.put(OshaType.MEXICO, false);

		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().isAnnualAddendum()
					&& audit.hasCaoStatus(AuditStatus.Complete)) {
				if (audit.isDataExpectedAnswer(AuditQuestion.OSHA_KEPT_ID,
						"Yes"))
					displayOsha.put(OshaType.OSHA, true);
				if (audit.isDataExpectedAnswer(AuditQuestion.COHS_KEPT_ID,
						"Yes"))
					displayOsha.put(OshaType.COHS, true);
				if (audit.isDataExpectedAnswer(AuditQuestion.UK_HSE_KEPT_ID,
						"Yes"))
					displayOsha.put(OshaType.UK_HSE, true);
				if (audit
						.isDataExpectedAnswer(AuditQuestion.EMR_KEPT_ID, "Yes"))
					displayOsha.put(OshaType.EMR, true);
				if (audit.isDataExpectedAnswer(AuditQuestion.MEXICO_KEPT_ID,
				"Yes"))
					displayOsha.put(OshaType.MEXICO, true);
			}
		}
	}

	public boolean isAnyOshasToDisplay() {
		return displayOsha.containsValue(true);
	}

	public Map<OshaType, Boolean> getDisplayOsha() {
		return displayOsha;
	}

	public void setDisplayOsha(Map<OshaType, Boolean> displayOsha) {
		this.displayOsha = displayOsha;
	}

	@RequiredPermission(value = OpPerms.ContractorWatch, type = OpType.Edit)
	public String startWatch() {
		ContractorWatch contractorWatch = getExistingContractorWatch();

		if (contractorWatch == null) {
			contractorWatch = new ContractorWatch();
			contractorWatch.setContractor(contractor);
			contractorWatch.setUser(userDAO.find(permissions.getUserId()));
			contractorWatch.setAuditColumns(permissions);

			userDAO.save(contractorWatch);
			addActionMessage("ContractorView.SuccessfullyAddedWatch");
		} else {
			addActionError(getText("ContractorDashboard.AlreadyWatchingContractor"));
		}

		return BLANK;
	}

	@RequiredPermission(value = OpPerms.ContractorWatch, type = OpType.Edit)
	public String stopWatch() {
		ContractorWatch existingWatch = getExistingContractorWatch();

		if (existingWatch != null) {
			userDAO.remove(existingWatch);
			addActionMessage("ContractorView.SuccessfullyRemovedWatch");
		} else {
			addActionError("ContractorView.RemovingUnwatchedContractor");
		}

		return BLANK;
	}

	public String updateGeneralContractor() {
		co = contractorOperatorDAO.find(contractor.getId(), opID);

		checkIfGeneralContractorRecordIsValid();

		if (approveGeneralContractorRelationship == null) {
			addActionError(getText("ContractorView.SelectGeneralContractorStatus"));
		}

		if (!hasActionErrors()) {
			addNote(contractor, permissions.getName()
					+ (approveGeneralContractorRelationship ? " approved "
							: " rejected ") + "the work status for "
					+ co.getOperatorAccount().getName());
			co.setWorkStatus(approveGeneralContractorRelationship ? ApprovalStatus.Y
					: ApprovalStatus.D);
			co.setAuditColumns(permissions);
			contractorOperatorDAO.save(co);
		}

		return "pendingGcOperators";
	}

	public String preview() throws Exception {
		return "preview";
	}

	public String printFlagMatrix() throws Exception {
		findContractor();
		return "printFlagMatrix";
	}

	public ContractorOperator getCo() {
		return co;
	}

	public int getOpID() {
		return opID;
	}

	@SuppressWarnings("unchecked")
	public String getContractorAndOperatorNames() throws Exception {
		int id = Integer.parseInt(getRequest().getParameter("id"));
		int opID = Integer.parseInt(getRequest().getParameter("opId"));
		json = new JSONObject();

		if (id > 0 && opID > 0) {
			findContractor();

			co = contractorOperatorDAO.find(id, opID);
			json.put("result", "success");
			json.put("contractorName", co.getContractorAccount().getName());
			json.put("operatorName", co.getOperatorAccount().getName());
		}

		return JSON;
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

	public List<ContractorAudit> getEmployeeGUARD() {
		return employeeGUARD;
	}

	public void setEmployeeGUARD(List<ContractorAudit> employeeGUARD) {
		this.employeeGUARD = employeeGUARD;
	}

	public boolean isShowBasicsOnly() {
		return showBasicsOnly;
	}

	public List<AuditData> getServicesPerformed() {
		if (servicesPerformed == null) {
			servicesPerformed = new ArrayList<AuditData>();
			for (AuditData auditData : dataDAO.findServicesPerformed(id)) {
				if (auditData.getAnswer().startsWith("C"))
					servicesPerformed.add(auditData);
			}

			for (AuditData auditData : dataDAO.findServicesPerformed(id)) {
				if (!auditData.getAnswer().startsWith("C")
						&& auditData.getAnswer().endsWith("S"))
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
			problems = new ContractorFlagCriteriaList(flagDataDAO.findProblems(
					id, opID));

		return problems;
	}

	public String getCriteriaLabel(int fcID, int operatorId) {
		if (fccMap == null) {
			fccMap = new HashMap<Integer, FlagCriteriaContractor>();

			List<FlagCriteriaContractor> flagCriteriaConList = flagCriteriaContractorDAO
					.findByContractor(id);

			for (FlagCriteriaContractor fcc : flagCriteriaConList) {
				fccMap.put(fcc.getCriteria().getId(), fcc);
			}
		}

		FlagCriteriaContractor fcc = fccMap.get(fcID);
		String result = "";

		if (fcc != null) {
			if (!Strings.isEmpty(fcc.getAnswer2())) {
				String answer = fcc.getAnswer2().split("<br/>")[0];

				if (answer != null
						&& fcc.getCriteria().getMultiYearScope() != null) {
					if (fcc.getCriteria().getMultiYearScope() != null) {
						if (answer.contains("for")) {
							result = answer;
						} else if (answer.contains("Year")) {
							result = " for " + answer;
						} else if (fcc.getCriteria().getMultiYearScope() == MultiYearScope.ThreeYearAverage) {
							result = " for Years: " + answer;
						} else {
							result = " for Year: " + answer;
						}
					}
				}
			}
			if (fcc.getCriteria().getId() == FlagCriteria.ANNUAL_UPDATE_ID) {
				result += getIncompleteAnnualUpdates(fcc.getContractor(),
						operatorId);
			}
		}

		return result;
	}

	private String getIncompleteAnnualUpdates(ContractorAccount con,
			int operatorId) {
		String years = "";
		
		ArrayList<String> forYears = new ArrayList<String>();
		for (ContractorAudit audit : con.getCurrentAnnualUpdates()) {
			for (ContractorAuditOperator cao : audit.getOperators()) {
				if (!cao.getStatus().equals(AuditStatus.Complete)) {
					for (ContractorAuditOperatorPermission caop : cao
							.getCaoPermissions()) {
						if (caop.getOperator().getId() == operatorId) {
							forYears.add(audit.getAuditFor());
							break;
						}
					}
				}
			}
		}

		Collections.sort(forYears);
		for (String yr : forYears) {
			if (years.length() > 0)
				years += ", ";
			years += yr;
		}
		return " " + years;
	}

	public ContractorFlagCriteriaList getCriteriaList() {
		if (criteriaList == null) {
			List<ContractorOperator> activeOperators = getActiveOperators();
			int[] operatorIds = new int[activeOperators.size()];
			int index = 0;
			for (ContractorOperator co : activeOperators) {
				operatorIds[index++] = co.getOperatorAccount().getId();
			}
			criteriaList = new ContractorFlagCriteriaList(
					flagDataDAO.findByContractorAndOperator(id, operatorIds));
		}
		return criteriaList;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		return operatorTagDAO.findByOperator(permissions.getAccountId(), true);
	}

	public List<ContractorOperator> getGeneralContractorClientSites() {
		// Intersection between contractor's operators and gc's operators
		if (permissions.isGeneralContractor()) {
			Set<Integer> clientSites = permissions.getLinkedClients();
			List<ContractorOperator> contractorOperators = new ArrayList<ContractorOperator>(
					contractor.getOperators());

			Iterator<ContractorOperator> contractorOperatorIterator = contractorOperators
					.iterator();
			while (contractorOperatorIterator.hasNext()) {
				ContractorOperator contractorOperator = contractorOperatorIterator
						.next();
				int operatorID = contractorOperator.getOperatorAccount()
						.getId();

				if (!clientSites.contains(operatorID)
						|| (permissions.isGeneralContractorFree() && operatorID == permissions
								.getAccountId())) {
					contractorOperatorIterator.remove();
				}
			}

			Collections.sort(contractorOperators,
					new Comparator<ContractorOperator>() {
						@Override
						public int compare(ContractorOperator o1,
								ContractorOperator o2) {
							return o1
									.getOperatorAccount()
									.getName()
									.compareTo(
											o2.getOperatorAccount().getName());
						}
					});

			return contractorOperators;
		} else {
			return Collections.emptyList();
		}
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

	public Boolean getApproveGeneralContractorRelationship() {
		return approveGeneralContractorRelationship;
	}

	public void setApproveGeneralContractorRelationship(
			Boolean approveGeneralContractorRelationship) {
		this.approveGeneralContractorRelationship = approveGeneralContractorRelationship;
	}

	public boolean isCanUpgrade() {
		if (permissions.isContractor())
			return true;

		if (permissions.seesAllContractors())
			return true;

		if (permissions.isOperator()
				&& permissions.hasPermission(OpPerms.ViewTrialAccounts,
						OpType.Edit))
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
				flagCounts.put(contractorOperator.getFlagColor(),
						flagCounts.get(contractorOperator.getFlagColor()) + 1);
			}

			Iterator<Map.Entry<FlagColor, Integer>> iter = flagCounts
					.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<FlagColor, Integer> entry = iter.next();
				if (entry.getValue() <= 0)
					iter.remove();
			}
		}
		return flagCounts;
	}

	public Date getEarliestIndividualFlagOverride() {
		return earliestIndividualFlagOverride;
	}

	public void setEarliestIndividualFlagOverride(
			Date earliestIndividualFlagOverride) {
		this.earliestIndividualFlagOverride = earliestIndividualFlagOverride;
	}

	public int getIndividualFlagOverrideCount() {
		return individualFlagOverrideCount;
	}

	public void setIndividualFlagOverrideCount(int individualFlagOverrideCount) {
		this.individualFlagOverrideCount = individualFlagOverrideCount;
	}

	public ContractorOperator getCorporateFlagOverride() {
		return corporateFlagOverride;
	}

	public void setCorporateFlagOverride(
			ContractorOperator corporateFlagOverride) {
		this.corporateFlagOverride = corporateFlagOverride;
	}

	public boolean isWatched() {
		return getExistingContractorWatch() != null;
	}

	public OshaOrganizer getOshaOrganizer() {
		return oshaOrganizer;
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

		if (permissions.isContractor())
			return true;

		return false;
	}

	public Map<ContractorAuditOperator, AuditStatus> getCaoStats(Integer opID) {
		if (prevStats == null) {
			prevStats = new TreeMap<ContractorAuditOperator, AuditStatus>(
					new Comparator<ContractorAuditOperator>() {
						public int compare(ContractorAuditOperator o1,
								ContractorAuditOperator o2) {
							return o2.getStatusChangedDate().compareTo(
									o1.getStatusChangedDate());
						}
					});
			for (ContractorAudit ca : this.getActiveAudits()) {
				for (ContractorAuditOperator cao : ca.getOperators()) {
					if (cao.hasCaop(opID)) {
						if (prevStats.get(cao) == null)
							prevStats.put(cao, null);

						// finding previous status from workflow if it exists
						if (cao.getCaoWorkflow().size() > 0) {
							List<ContractorAuditOperatorWorkflow> caow = cao
									.getCaoWorkflow();
							Collections
									.sort(caow,
											new Comparator<ContractorAuditOperatorWorkflow>() {
												public int compare(
														ContractorAuditOperatorWorkflow o1,
														ContractorAuditOperatorWorkflow o2) {
													return o1
															.getCreationDate()
															.compareTo(
																	o2.getCreationDate());
												}
											});
							prevStats
									.put(cao,
											cao.getCaoWorkflow()
													.get(cao.getCaoWorkflow()
															.size() - 1)
													.getPreviousStatus());
						}

					}
				}
			}
		}
		return prevStats;
	}

	public List<ContractorTag> getTagsViewableByUser() {
		if (permissions.isOperator()) {
			List<ContractorTag> tags = new ArrayList<ContractorTag>(
					contractor.getOperatorTags());
			Iterator<ContractorTag> iterator = tags.iterator();
			while (iterator.hasNext()) {
				int id = iterator.next().getTag().getOperator().getId();
				if (permissions.getAccountId() != id
						&& !permissions.getCorporateParent().contains(id)) {
					iterator.remove();
				}
			}

			return tags;
		}

		return contractor.getOperatorTags();
	}

	public String getCommaSeparatedContractorTypes() {
		if (contractor != null) {
			List<String> commaSeparatedContractorTypes = new ArrayList<String>();
			List<ContractorType> sortedContractorTypes = new ArrayList<ContractorType>(
					contractor.getAccountTypes());

			Collections.sort(sortedContractorTypes);

			for (ContractorType type : sortedContractorTypes) {
				commaSeparatedContractorTypes.add(getText(type.getI18nKey()));
			}

			return Strings.implode(commaSeparatedContractorTypes, ", ");
		}

		return null;
	}

	public boolean isHasPendingGeneralContractors() {
		for (ContractorOperator contractorOperator : contractor.getOperators()) {
			if (contractorOperator.getOperatorAccount().isGeneralContractor()
					&& contractorOperator.isWorkStatusContractor())
				return true;
		}

		return false;
	}

	public List<ContractorOperator> getGeneralContractorsNeedingApproval() {
		if (generalContractorsNeedingApproval == null) {
			generalContractorsNeedingApproval = new ArrayList<ContractorOperator>();

			for (OperatorAccount operator : contractor
					.getGeneralContractorOperatorAccounts()) {
				ContractorOperator contractorOperator = contractor
						.getContractorOperatorForOperator(operator);
				if (ApprovalStatus.C.equals(contractorOperator.getWorkStatus())) {
					generalContractorsNeedingApproval.add(contractorOperator);
				}
			}
		}

		return generalContractorsNeedingApproval;
	}

	public OshaDisplay getOshaDisplay() {
		return oshaDisplay;
	}

	public boolean isViewableByGC() {
		if (permissions.isGeneralContractorFree()) {
			if (getActiveOperators().size() == 1
					&& getActiveOperators().get(0).getOperatorAccount().getId() == permissions
							.getAccountId()) {
				return false;
			}
		}

		return true;
	}

	public String getGeneralContractorsListing() {
		String generalContractorListing = "";
		boolean start = true;

		for (OperatorAccount generalContractor : contractor
				.getGeneralContractorOperatorAccounts()) {
			if (!start) {
				generalContractorListing += ", ";
			}

			generalContractorListing += generalContractor.getName();
			start = false;
		}

		return generalContractorListing;
	}

	public String getPercentComplete(FlagCriteria criteria, int operatorId) {
		String percentComplete = "";
		if (criteria.getAuditType() != null) {
			for (ContractorAudit audit : contractor.getAudits()) {
				if (audit.getAuditType().equals(criteria.getAuditType())) {
					if (percentComplete.length() > 0)
						percentComplete += ", ";
					percentComplete += getPercentCompleteForOperator(audit,
							operatorId);
				}
			}
		}

		if (percentComplete.length() > 0)
			percentComplete = this.getTextParameterized(
					"ContractorView.Complete", percentComplete);
		return percentComplete;
	}

	public List<User> getUsersWithPermission(OpPerms operatorPermission) {
		List<User> visibleUsersWithPermissions = new ArrayList<User>();

		if (co != null) {
			for (User user : co.getOperatorAccount().getUsers()) {
				try {
					Permissions permissions = permissionBuilder.login(user);
					if (permissions.hasPermission(operatorPermission)) {
						visibleUsersWithPermissions.add(user);
					}
				} catch (Exception e) {
					logger.error("Cannot login user", e);
				}
			}
		}

		return visibleUsersWithPermissions;
	}

	public User getPicsRepresentativeForOperator() {
		if (co != null) {
			for (AccountUser accountUser : co.getOperatorAccount()
					.getAccountUsers()) {
				if (accountUser.isCurrent()) {
					return accountUser.getUser();
				}
			}
		}

		return null;
	}

	private void setOpIDToFirstClientSiteIfGCFree() {
		if (permissions.isGeneralContractorFree()
				&& permissions.getLinkedClients().size() > 0) {
			for (OperatorAccount client : contractor.getOperatorAccounts()) {
				if (permissions.getLinkedClients().contains(client.getId())) {
					opID = client.getId();
					break;
				}
			}
		}
	}

	private void findCorporateOverride() {
		corporateFlagOverride = null;
		if (co == null) {
			return;
		}

		if (co.isForcedFlag()) {
			return;
		}

		List<ContractorOperator> contractorOperators = contractorOperatorDAO
				.findByContractor(contractor.getId(), permissions);
		for (ContractorOperator contractorOperator : contractorOperators) {
			if (contractorOperator.equals(co))
				continue;
			if (contractorOperator.isForcedFlag()) {
				for (Facility facility : co.getOperatorAccount()
						.getCorporateFacilities()) {
					if (facility.getCorporate() == contractorOperator
							.getOperatorAccount()) {
						corporateFlagOverride = contractorOperator;
						break;
					}
				}
			}
		}
	}

	private void calculateEarliestIndividualFlagSummaries() {
		if (co == null)
			return;

		earliestIndividualFlagOverride = null;

		Date now = new Date();
		for (FlagDataOverride fdo : co.getOverrides()) {
			if (fdo.getForceEnd() != null && fdo.getForceEnd().after(now)) {
				individualFlagOverrideCount++;
				if (earliestIndividualFlagOverride == null
						|| fdo.getForceEnd().before(
								earliestIndividualFlagOverride)) {
					earliestIndividualFlagOverride = fdo.getForceEnd();
				}
			}
		}
	}

	private String getPercentCompleteForOperator(ContractorAudit audit,
			int operatorId) {
		int lowestPercentComplete = 101;
		String answer = "";
		for (ContractorAuditOperator cao : audit.getOperators()) {
			if (cao.isVisible() && cao.getPercentComplete() <= 100) {
				if (lowestPercentComplete > cao.getPercentComplete())
					lowestPercentComplete = cao.getPercentComplete();
				for (ContractorAuditOperatorPermission caop : cao
						.getCaoPermissions()) {
					if (caop.getOperator().getId() == operatorId
							|| caop.getOperator().getOperatorHeirarchy()
									.contains(operatorId)) {
						if (audit.getAuditType().isAnnualAddendum()) {
							return audit.getAuditFor() + ": "
									+ String.valueOf(cao.getPercentComplete())
									+ "%";
						}
						return String.valueOf(cao.getPercentComplete()) + "%";
					}
				}
			}
		}

		String prefix = "";
		if (audit.getAuditType().isAnnualAddendum()) {
			prefix = audit.getAuditFor() + ": ";
		}

		return (lowestPercentComplete <= 100) ? prefix + lowestPercentComplete
				+ "%" : "";
	}

	private ContractorWatch getExistingContractorWatch() {
		User user = userDAO.find(permissions.getUserId());

		for (ContractorWatch watchedContractor : user.getWatchedContractors()) {
			if (watchedContractor.getContractor().getId() == contractor.getId())
				return watchedContractor;
		}

		return null;
	}

	private void checkIfGeneralContractorRecordIsValid() {
		if (co == null
				|| (co.getOperatorAccount() != null && !co.getOperatorAccount()
						.isGeneralContractor())) {
			addActionError(getText("ContractorView.SelectGeneralContractor"));
		}
	}
}
