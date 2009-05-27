package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportInsuranceSupport extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;
	protected OperatorAccountDAO operatorAccountDAO = null;

	/**
	 * Map of Purpose, AuditID, then List of Answers
	 */
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	public ReportInsuranceSupport(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO) {
		// sql = new SelectContractorAudit();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
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

			String subSelect = "SELECT inheritInsuranceCriteria FROM Operators WHERE id = " + permissions.getAccountId();
			sql.addWhere("cao.opid = (" + subSelect + ")");
		}

		sql.addWhere("ca.auditStatus != 'Expired'");

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
}
