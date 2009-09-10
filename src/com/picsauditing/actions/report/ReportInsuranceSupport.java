package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportInsuranceSupport extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;
	protected OperatorAccountDAO operatorAccountDAO = null;
	protected AmBestDAO amBestDAO = null;

	/**
	 * Map of Purpose, AuditID, then List of Answers
	 */
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	public ReportInsuranceSupport(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
		// sql = new SelectContractorAudit();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
		this.amBestDAO = amBestDAO;
	}

	@Override
	protected void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addWhere("cao.visible = 1");
		if (!permissions.hasPermission(OpPerms.AllContractors)) {
			sql.addField("cao.status as caoStatus");
			sql.addField("cao.notes as caoNotes");
			sql.addField("cao.id as caoId");
			sql.addField("cao.flag as caoRecommendedFlag");
			sql.addField("cao.certificateID");
			sql.addField("valid");

			sql.addJoin("JOIN accounts caoaccount on caoaccount.id = cao.opID");
			sql.addField("caoaccount.name as caoName");

			if (permissions.getVisibleCAOs().size() > 0)
				sql.addWhere("cao.opid IN (" + Strings.implode(permissions.getVisibleCAOs(), ",") + ")");
			else {
				addActionError("Your account doesn't have access to any policies. Your account may not be set up correctly.");
				sql.addWhere("a.id = 0");
			}
		}

		sql.addWhere("ca.auditStatus != 'Expired'");
		
		if(getFilter().getAmBestRating() > 0 || getFilter().getAmBestClass() > 0) {
			sql.addJoin("JOIN pqfdata am ON am.auditid = ca.id");
			sql.addJoin("JOIN pqfquestions pq ON pq.id = am.questionid");
			sql.addJoin("JOIN ambest ambest ON ambest.naic = am.comment and ambest.companyName = am.answer");
			sql.addWhere("pq.questionType = 'AMBest'");
			if(getFilter().getAmBestRating() > 0)
				sql.addWhere("ambest.ratingcode = " + getFilter().getAmBestRating());
			if(getFilter().getAmBestClass() > 0)
				sql.addWhere("ambest.financialCode =" + getFilter().getAmBestClass());
		}
		
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowHasClosedDate(true);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditStatus(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowAuditFor(false);
		getFilter().setShowFlagStatus(false);

		getFilter().setShowCreatedDate(true);
		getFilter().setShowExpiredDate(true);
		getFilter().setShowPolicyType(true);
		getFilter().setShowCaoStatus(true);
		// getFilter().setShowConAuditor(false);

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
					if (answer.getQuestion().getSubCategory().getSubCategory().equals("Policy Limits"))
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
