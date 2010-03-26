package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.ContractorFlagCriteriaList;
import com.picsauditing.PICS.DoubleMap;
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
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private AuditBuilder auditBuilder;
	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO dataDAO;
	private FlagDataDAO flagDataDAO;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private InvoiceItemDAO invoiceItemDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	private ContractorOperator co;
	private int opID;

	private List<ContractorAudit> docuGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> auditGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> insureGUARD = new ArrayList<ContractorAudit>();

	private ContractorFlagCriteriaList problems;

	private ContractorFlagCriteriaList criteriaList;

	private Map<String, Map<String, String>> oshaAudits;

	private OshaDisplay oshaDisplay;

	public ContractorDashboard(AuditBuilder auditBuilder, ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDAO, AuditDataDAO dataDAO, FlagDataDAO flagDataDAO,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO, InvoiceItemDAO invoiceItemDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.dataDAO = dataDAO;
		this.flagDataDAO = flagDataDAO;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.invoiceItemDAO = invoiceItemDAO;
		this.subHeading = "Account Summary";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();

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
			contractor.setAcceptsBids(false);
			contractor.setRenew(true);
			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf() && !cAudit.getAuditStatus().isPending()) {
					cAudit.changeStatus(AuditStatus.Pending, getUser());
					auditDao.save(cAudit);
					break;
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

			contractor.setNeedsRecalculation(true);
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
			EmailSender.send(emailQueue);

			if (permissions.isContractor()) {
				ServletActionContext.getResponse().sendRedirect(
						"BillingDetail.action?id=" + contractor.getId() + "&button=Create");
				return BLANK;
			}
		}

		if (permissions.isOperator()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}

		if (contractor.getNonCorporateOperators().size() > 0) {
			auditBuilder.setUser(getUser());
			auditBuilder.buildAudits(this.contractor);
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

		// auditFor ==> Rate Type ==> value
		oshaAudits = new LinkedHashMap<String, Map<String, String>>();

		OshaOrganizer organizer = contractor.getOshaOrganizer();
		for (MultiYearScope scope : new MultiYearScope[] { MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
				MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage, MultiYearScope.ThreeYearWeightedAverage }) {
			OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
			if (audit != null) {
				String auditFor = "";
				if (audit.getConAudit() == null) {
					if (scope.equals(MultiYearScope.ThreeYearAverage))
						auditFor = "Average";
					else if (scope.equals(MultiYearScope.ThreeYearWeightedAverage))
						auditFor = "W Average";
				} else
					auditFor = audit.getConAudit().getAuditFor();

				oshaAudits.put(auditFor, new LinkedHashMap<String, String>());
				for (OshaRateType rateType : new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute,
						OshaRateType.Fatalities }) {

					oshaAudits.get(auditFor).put(rateType.toString(),
							organizer.getRate(OshaType.OSHA, scope, rateType).toString());
				}

				oshaAudits.get(auditFor).put("manhours", "" + audit.getManHours());
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
		return dataDAO.findServicesPerformed(id);
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

	public Map<String, Map<String, String>> getOshaAudits() {
		return oshaAudits;
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

	public OshaDisplay getOshaDisplay() {
		if (oshaDisplay == null) {
			if (co == null)
				oshaDisplay = new OshaDisplay(contractor.getOshaOrganizer(), getActiveOperators());
			else
				oshaDisplay = new OshaDisplay(contractor.getOshaOrganizer(), new ArrayList<ContractorOperator>() {
					{
						add(co);
					}
				});
		}

		return oshaDisplay;
	}

	public class OshaDisplay {

		Set<String> auditForSet = new LinkedHashSet<String>();
		Set<String> rateTypeSet = new LinkedHashSet<String>();

		private DoubleMap<String, String, String> data = new DoubleMap<String, String, String>();

		public OshaDisplay(OshaOrganizer organizer, List<ContractorOperator> operators) {

			rateTypeSet.add(OshaRateType.TrirAbsolute.getDescription());
			for (ContractorOperator contractorOperator : operators) {
				rateTypeSet.add(getOperatorDisplay(contractorOperator, " TRIR"));
			}
			rateTypeSet.add(OshaRateType.LwcrAbsolute.getDescription());
			for (ContractorOperator contractorOperator : operators) {
				rateTypeSet.add(getOperatorDisplay(contractorOperator, " LWCR"));
			}
			rateTypeSet.add(OshaRateType.Fatalities.getDescription());
			rateTypeSet.add("Hours Worked");

			for (MultiYearScope scope : new MultiYearScope[] { MultiYearScope.ThreeYearsAgo,
					MultiYearScope.TwoYearsAgo, MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearAverage,
					MultiYearScope.ThreeYearWeightedAverage }) {
				OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
				if (audit != null) {
					String auditFor = findAuditFor(organizer, scope);

					auditForSet.add(auditFor);

					for (OshaRateType rate : new OshaRateType[] { OshaRateType.TrirAbsolute, OshaRateType.LwcrAbsolute,
							OshaRateType.Fatalities }) {
						data
								.put(auditFor, rate.getDescription(), format(organizer.getRate(OshaType.OSHA, scope,
										rate)));
					}

					data.put(auditFor, "Hours Worked", format(audit.getManHours()));

				}
			}

			String ind = "Industry";
			auditForSet.add(ind);
			data.put(ind, OshaRateType.TrirAbsolute.getDescription(), format(contractor.getNaics().getTrir()));
			data.put(ind, OshaRateType.LwcrAbsolute.getDescription(), format(contractor.getNaics().getLwcr()));

			for (ContractorOperator contractorOperator : operators) {
				for (FlagCriteriaOperator fco : contractorOperator.getOperatorAccount().getFlagCriteriaInherited()) {
					if (OshaRateType.TrirAbsolute.equals(fco.getCriteria().getOshaRateType())
							|| OshaRateType.LwcrAbsolute.equals(fco.getCriteria().getOshaRateType())) {
						MultiYearScope scope = fco.getCriteria().getMultiYearScope();
						String auditFor = findAuditFor(organizer, scope);
						String suffix = OshaRateType.TrirAbsolute.equals(fco.getCriteria().getOshaRateType()) ? " TRIR"
								: " LWCR";
						data.put(auditFor, getOperatorDisplay(contractorOperator, suffix), fco.getShortDescription());
					}
				}
			}
		}

		private String getOperatorDisplay(ContractorOperator contractorOperator, String type) {
			return "&nbsp;&nbsp;" + contractorOperator.getOperatorAccount().getName() + type;
		}

		private String findAuditFor(OshaOrganizer organizer, MultiYearScope scope) {
			OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
			String auditFor = "";
			if (audit.getConAudit() == null) {
				if (scope.equals(MultiYearScope.ThreeYearAverage))
					auditFor = "Average";
				else if (scope.equals(MultiYearScope.ThreeYearWeightedAverage))
					auditFor = "W Average";
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

		public String getData(String k1, String k2) {
			return data.get(k1, k2);
		}
	}
}
