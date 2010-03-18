package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.ContractorFlagCriteriaList;
import com.picsauditing.PICS.OshaOrganizer;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.FlagDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;

@SuppressWarnings("serial")
public class ContractorDashboard extends ContractorActionSupport {

	private AuditBuilder auditBuilder;
	private ContractorOperatorDAO contractorOperatorDAO;
	private AuditDataDAO dataDAO;
	private FlagDataDAO flagDataDAO;

	private ContractorOperator co;
	private int opID;

	private List<ContractorAudit> docuGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> auditGUARD = new ArrayList<ContractorAudit>();
	private List<ContractorAudit> insureGUARD = new ArrayList<ContractorAudit>();

	List<FlagData> problems;

	private ContractorFlagCriteriaList criteriaList;

	private Map<String, Map<String, String>> oshaAudits;

	public ContractorDashboard(AuditBuilder auditBuilder, ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorOperatorDAO contractorOperatorDAO, AuditDataDAO dataDAO, FlagDataDAO flagDataDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.dataDAO = dataDAO;
		this.flagDataDAO = flagDataDAO;
		this.subHeading = "Contractor Dashboard";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN_AJAX;

		findContractor();

		if (contractor.getNonCorporateOperators().size() > 0) {
			auditBuilder.setUser(getUser());
			auditBuilder.buildAudits(this.contractor);
		}

		if (opID == 0 && permissions.isOperator())
			opID = permissions.getAccountId();

		co = contractorOperatorDAO.find(id, opID);

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

		criteriaList = new ContractorFlagCriteriaList(flagDataDAO.findByContractorAndOperator(id, opID));

		oshaAudits = new LinkedHashMap<String, Map<String, String>>();

		OshaOrganizer organizer = contractor.getOshaOrganizer();

		for (MultiYearScope scope : new MultiYearScope[] { MultiYearScope.ThreeYearsAgo, MultiYearScope.TwoYearsAgo,
				MultiYearScope.LastYearOnly, MultiYearScope.ThreeYearWeightedAverage }) {
			OshaAudit audit = organizer.getOshaAudit(OshaType.OSHA, scope);
			String auditFor = audit.getConAudit() == null ? "AVG" : audit.getConAudit().getAuditFor();

			for (OshaRateType rateType : new OshaRateType[] { OshaRateType.TrirNaics, OshaRateType.LwcrNaics }) {
				if (oshaAudits.get(auditFor) == null)
					oshaAudits.put(auditFor, new LinkedHashMap<String, String>());

				oshaAudits.get(auditFor).put(rateType.toString(),
						organizer.getRate(OshaType.OSHA, scope, rateType).toString());
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

	public List<FlagData> getProblems() {
		if (problems == null)
			problems = flagDataDAO.findProblems(id, opID);

		return problems;
	}

	public ContractorFlagCriteriaList getCriteriaList() {
		return criteriaList;
	}

	public Map<String, Map<String, String>> getOshaAudits() {
		return oshaAudits;
	}
}
