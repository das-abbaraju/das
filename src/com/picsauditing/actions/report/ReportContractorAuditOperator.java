package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.util.ReportFilterCAO;
import com.picsauditing.util.Strings;
import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportContractorAuditOperator extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected AmBestDAO amBestDAO = null;

	/**
	 * Map of Purpose, AuditID, then List of Answers
	 */
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	public ReportContractorAuditOperator(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		super();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
		this.amBestDAO = amBestDAO;
		orderByDefault = "cao.statusChangedDate DESC";
		filter = new ReportFilterCAO();
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit_operator cao ON cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		sql.addJoin("JOIN accounts caoAccount ON cao.opID = caoAccount.id");
		sql.addField("cao.id caoID");
		sql.addField("cao.status auditStatus");
		sql.addField("cao.statusChangedDate");
		sql.addField("caoAccount.name caoAccountName");
		sql.addWhere("a.status IN ('Active'" + (permissions.getAccountStatus().isDemo() ? ",'Demo'" : "")
				+ ")");

		if (permissions.isOperatorCorporate()) {
			String opIDs = permissions.getAccountIdString();
			if (permissions.isCorporate())
				opIDs = Strings.implode(permissions.getOperatorChildren());

			sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
					+ "))");
		}

		if (getFilter().isShowAuditStatus() && getFilter().getAuditStatus() == null)
			getFilter().setAuditStatus(AuditStatus.valuesWithoutPendingExpired());

		getFilter().setShowOperator(false);
		getFilter().setShowTrade(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowWaitingOn(true);
		getFilter().setShowIndustry(false);
		getFilter().setShowAddress(false);
	}

	@Override
	protected void addFilterToSQL() {
		super.addFilterToSQL();

		ReportFilterCAO f = getFilter();

		String auditStatusList = Strings.implodeForDB(f.getAuditStatus(), ",");
		if (filterOn(auditStatusList)) {
			sql.addWhere("cao.status IN (" + auditStatusList + ")");
		}

		if (filterOn(f.getPercentComplete1())) {
			report.addFilter(new SelectFilter("percentComplete1", "cao.percentComplete >= '?'", f.getPercentComplete1()));
		}

		if (filterOn(f.getPercentComplete2())) {
			report.addFilter(new SelectFilter("percentComplete2", "cao.percentComplete < '?'", f.getPercentComplete2()));
		}

		if (getFilter().getAmBestRating() > 0 || getFilter().getAmBestClass() > 0) {
			sql.addJoin("JOIN pqfdata am ON am.auditid = ca.id");
			sql.addJoin("JOIN audit_question aq ON aq.id = am.questionid");
			sql.addJoin("JOIN ambest ambest ON ambest.naic = am.comment and ambest.companyName = am.answer");
			sql.addWhere("aq.questionType = 'AMBest'");
			if (getFilter().getAmBestRating() > 0)
				sql.addWhere("ambest.ratingcode = " + getFilter().getAmBestRating());
			if (getFilter().getAmBestClass() > 0)
				sql.addWhere("ambest.financialCode =" + getFilter().getAmBestClass());
		}

		if (filterOn(f.getCaoOperator())) {
			sql.addWhere("cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN ("
					+ Strings.implode(f.getCaoOperator()) + "))");
		}
	}

	@Override
	public ReportFilterCAO getFilter() {
		return (ReportFilterCAO) filter;
	}

	/**
	 * returns the proper answer(s) of the questionData, which holds
	 * supplemental question data that is not returned in the main query
	 * 
	 * @param auditId
	 * @param purpose
	 *            - kind of like a column name. Known keys for this are:
	 *            policyFile, aiWaiverSub, aiName, aiMatches, aiOther or limits
	 * @return List<AuditData>
	 */
	public List<AuditData> getDataForAudit(int auditId, String purpose) {
		if (questionData == null) {
			List<Integer> theseAudits = new Vector<Integer>();
			for (DynaBean bean : data) {
				theseAudits.add(Integer.parseInt(bean.get("auditID").toString()));
			}

			/***** Load our Policy Data *****/
			List<AuditData> answers = auditDataDao.findPolicyData(theseAudits);

			// Map<UniqueCode, Map<AuditID, List<AuditData>>>
			questionData = new HashMap<String, Map<Integer, List<AuditData>>>();

			// if we got data, populate the "limits" section of our map
			if (answers != null && answers.size() > 0) {

				// add the answers, keyed by auditid
				for (AuditData answer : answers) {
					String uniqueCode = answer.getQuestion().getUniqueCode();
					if (answer.getQuestion().getCategory().getName().equals("Policy Limits"))
						uniqueCode = "Limits";

					if (answer.getQuestion().getQuestionType().equals("AMBest")) {
						uniqueCode = "AMBest";
					}

					if (!Strings.isEmpty(uniqueCode)) {
						int auditID = answer.getAudit().getId();
						if (questionData.get(uniqueCode) == null)
							questionData.put(uniqueCode, new HashMap<Integer, List<AuditData>>());
						if (questionData.get(uniqueCode).get(auditID) == null)
							questionData.get(uniqueCode).put(auditID, new ArrayList<AuditData>());
						questionData.get(uniqueCode).get(auditID).add(answer);
					}
				}
			}
		}

		try {
			return questionData.get(purpose).get(auditId);
		} catch (Exception e) {
			return new ArrayList<AuditData>();
		}
	}

	public String getAMBestRatings(String comment) {
		String value = "";
		if (!Strings.isEmpty(comment)) {
			AmBest amBest = amBestDAO.findByNaic(comment);
			if (amBest != null) {
				if (!Strings.isEmpty(amBest.getRatingAlpha())) {
					value = "<nobr> Ratings: " + amBest.getRatingAlpha() + "</nobr><br/>";
				}
				if (!Strings.isEmpty(amBest.getFinancialAlpha())) {
					value += "Class: " + amBest.getFinancialAlpha();
				}
			}
		}
		if (!Strings.isEmpty(value))
			return value;
		return "N/A";
	}

}
