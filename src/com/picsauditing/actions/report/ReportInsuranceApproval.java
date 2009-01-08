package com.picsauditing.actions.report;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.beanutils.DynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;

	// BusinessPurpose (limit, 'aiWaiver', etc), auditId
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	// AuditType, uniquecode
	protected Map<String, Map<String, AuditQuestion>> questionsKeyedByUniqueKey = null;

	public ReportInsuranceApproval(AuditDataDAO auditDataDao,
			AuditQuestionDAO auditQuestionDao) {
		// sql = new SelectContractorAudit();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		showOnlyAudits = false;
		super.buildQuery();
		sql.addField("ca.expiresDate");
		sql.addField("ao.name as operatorName");
		sql.addField("cao.status as caoStatus");
		sql.addField("cao.notes as caoNotes");
		sql
				.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.auditID");

		sql.addJoin("JOIN accounts ao on ao.id = cao.opID");
		sql.addWhere("ca.auditStatus IN ('Submitted','Active')");
		// sql.addWhere("cao.status = 'Pending'");
		sql.addWhere("atype.classType = 'Policy'");
		sql.addWhere("a.active = 'Y'");

		if (getUser().getAccount().isOperator()) {
			sql.addWhere("cao.opid = " + getUser().getAccount().getIdString());
		}

		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowExpiredDate(false);
		getFilter().setShowPercentComplete(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowCaoStatus(true);
		getFilter().setShowAuditStatus(false);
	}

	/**
	 * returns the proper submap of the questionData, which holds supplemental
	 * question data that is not returned in the main query known keys for this
	 * data are : aiFile, aiWaiverSub, aiName, and limits
	 * 
	 * @param auditId
	 * @return List<AuditData>
	 */
	public List<AuditData> getDataForAudit(int auditId, String purpose) {

		if (questionsKeyedByUniqueKey == null) {
			loadSecondaryData();
		}

		if (questionData.get(purpose) != null) {
			return questionData.get(purpose).get(auditId);
		} else {
			return null;
		}
	}

	protected void loadSecondaryData() {
		try {

			questionData = new HashMap<String, Map<Integer, List<AuditData>>>();

			List<Integer> theseAudits = new Vector<Integer>();
			Map<Integer, String> theseAuditTypes = new HashMap<Integer, String>();
			for (DynaBean bean : data) {
				theseAudits.add(((Long) bean.get("auditID")).intValue());
				theseAuditTypes.put(((Long) bean.get("auditID")).intValue(),
						(String) bean.get("auditName"));
			}

			Map<Integer, List<AuditData>> limits = questionData.get("limits");

			if (limits == null) {
				loadLimits(theseAudits, limits);
			}

			List<AuditQuestion> questions = auditQuestionDao
					.findQuestionsByUniqueCodes(Arrays.asList("aiName",
							"aiFile", "aiWaiverSub"));

			// figure out which questions we care about, based on their unique
			// codes
			loadQuestionsByUniqueCode(questions);

			List<Integer> questionIDs = new Vector<Integer>();
			for (AuditQuestion q : questions) {
				questionIDs.add(q.getId());
			}

			Map<Integer, AnswerMap> answersForAllAudits = auditDataDao
					.findAnswers(theseAudits, questionIDs);

			for (Integer thisAuditId : answersForAllAudits.keySet()) {

				Map<String, AuditQuestion> byUniqueCode = questionsKeyedByUniqueKey
						.get(theseAuditTypes.get(thisAuditId));

				AnswerMap answersForThisAudit = answersForAllAudits
						.get(thisAuditId);

				AuditQuestion aiNameQuestion = byUniqueCode.get("aiName");

				AuditData aiNameAnswer = answersForThisAudit.get(aiNameQuestion.getId());
				if (aiNameAnswer != null) {

					Map<Integer, List<AuditData>> byAuditId = questionData
							.get("aiName");

					if (byAuditId == null) {
						byAuditId = new HashMap<Integer, List<AuditData>>();
						questionData.put("aiName", byAuditId);
					}

					List<AuditData> answers = byAuditId.get(thisAuditId);

					if (answers == null) {
						answers = new Vector<AuditData>();
						byAuditId.put(thisAuditId, answers);
					}

					if (aiNameAnswer != null) {
						
						//find get all the operator's legal names
						if( getUser().getAccount().isOperator() ) {
							OperatorAccount thisOp = (OperatorAccount) getUser().getAccount();
							
							List<AccountName> names = thisOp.getNames();
							
							if( names != null ) {
								for( AccountName accountName : names ) {
									if( accountName.getName().equalsIgnoreCase( aiNameAnswer.getAnswer())) {
										answers.add(aiNameAnswer);	
									}
								}
							}
							
						}
						
						
					}
				}
			}

			for (Integer thisAuditId : answersForAllAudits.keySet()) {

				Map<String, AuditQuestion> byUniqueCode = questionsKeyedByUniqueKey
						.get(theseAuditTypes.get(thisAuditId));

				for (String uniqueKey : byUniqueCode.keySet()) {
					AuditQuestion question = byUniqueCode.get(uniqueKey);

					if (!uniqueKey.equals("aiName")) {

						AnswerMap answersForThisAudit = answersForAllAudits
								.get(thisAuditId);


						
						Map<Integer, List<AuditData>> byAuditId = questionData
								.get(uniqueKey);

						if (byAuditId == null) {
							byAuditId = new HashMap<Integer, List<AuditData>>();
							questionData.put(uniqueKey, byAuditId);
						}

						List<AuditData> answers = byAuditId.get(thisAuditId);

						if (answers == null) {
							answers = new Vector<AuditData>();
							byAuditId.put(thisAuditId, answers);
						}

						List<AuditData> aiAnswers = questionData.get("aiName").get(thisAuditId);
						
						if (aiAnswers != null && aiAnswers.size() > 0) {
							
							for( AuditData aiAnswer : aiAnswers ) {
								AuditData tmpData = answersForThisAudit.get(
										question.getId(), aiAnswer.getId());
								answers.add(tmpData);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void loadQuestionsByUniqueCode(List<AuditQuestion> questions) {
		questionsKeyedByUniqueKey = new HashMap<String, Map<String, AuditQuestion>>();

		for (AuditQuestion question : questions) {
			String auditTypeName = question.getSubCategory().getCategory()
					.getAuditType().getAuditName();

			Map<String, AuditQuestion> forThisType = questionsKeyedByUniqueKey
					.get(auditTypeName);

			if (forThisType == null) {
				forThisType = new HashMap<String, AuditQuestion>();
				questionsKeyedByUniqueKey.put(auditTypeName, forThisType);
			}

			forThisType.put(question.getUniqueCode(), question);
		}
	}

	protected void loadLimits(List<Integer> theseAudits,
			Map<Integer, List<AuditData>> limits) {
		List<AuditData> answers = auditDataDao
				.findAnswersByAuditAndSubCategory(theseAudits, "Policy Limits");

		// if we got data, populate the "limits" section of our map
		if (answers != null && answers.size() > 0) {

			limits = new HashMap<Integer, List<AuditData>>();
			questionData.put("limits", limits);
		}

		// add the answers, keyed by auditid
		for (AuditData answer : answers) {
			int tempId = answer.getAudit().getId();

			List<AuditData> dataForThisAudit = limits.get(tempId);

			if (dataForThisAudit == null) {

				dataForThisAudit = new Vector<AuditData>();
				limits.put(tempId, dataForThisAudit);
			}

			dataForThisAudit.add(answer);
		}
	}
	
	
//	public void sendEmail(List<CertificateDO> list, Permissions permissions) throws Exception {
//		for (CertificateDO cdo : list) {
//
//			AccountBean aBean = new AccountBean();
//			String conID = cdo.getContractor_id();
//			aBean.setFromDB(conID);
//			String contactName = aBean.contact;
//			String contractor = aBean.name;
//
//			AccountBean oAccountBean = new AccountBean();
//			oAccountBean.setFromDB(cdo.getOperator_id());
//			String operator = oAccountBean.name;
//
//			String message = "Hello " + contactName + ",\n\n" + contractor + "'s " + cdo.getType()
//					+ " Insurance Certificate has been " + cdo.getStatus() + " by " + operator;
//			if (!Strings.isEmpty(cdo.getReason()))
//				message += " for the following reasons:\n\n" + cdo.getReason() + "\n\n";
//
//			if (cdo.getStatus().equals("Rejected")) {
//				UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
//				User user = userDAO.find(permissions.getUserId());
//				message += "Please correct these issues and re-upload your insurance certificate to your "
//						+ "PICS account.\n" + "If you have any specific questions about " + operator
//						+ "'s insurance requirements, " + "please contact " + permissions.getName() + " at "
//						+ user.getEmail() + ".";
//			} else {
//				message += "Please make sure that you keep up-to-date in PICS by uploading your "
//						+ "insurance certificate when you renew your policy.";
//			}
//			message += "\n\nHave a great day,\n" + "PICS Customer Service";
//
//			EmailSender.send(aBean.email, operator + " insurance certificate " + cdo.getStatus(), message);
//
//			String newNote = cdo.getType() + " insurance certificate " + cdo.getStatus() + " by " + operator
//					+ " for reason: " + cdo.getReason();
//			Note note = new Note(cdo.getOperator_id(), cdo.getContractor_id(), permissions.getUserIdString(),
//					permissions.getName(), newNote);
//			note.writeToDB();
//		}
//	}
}
