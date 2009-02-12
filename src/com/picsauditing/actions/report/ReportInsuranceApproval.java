package com.picsauditing.actions.report;

import java.text.DecimalFormat;
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
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.AnswerMap;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAudits {

	protected AuditDataDAO auditDataDao = null;
	protected AuditQuestionDAO auditQuestionDao = null;
	protected OperatorAccountDAO operatorAccountDAO = null;

	/**
	 * Map of Purpose, AuditID, then List of Answers
	 */
	protected Map<String, Map<Integer, List<AuditData>>> questionData = null;

	/**
	 * Map of AuditType.Name, uniquecode : used to get the question (formal), 
	 * which we'll use later to get the answer (actual)
	 */
	protected Map<String, Map<String, AuditQuestion>> questionsKeyedByUniqueKey = null;

	public ReportInsuranceApproval(AuditDataDAO auditDataDao,
			AuditQuestionDAO auditQuestionDao, OperatorAccountDAO operatorAccountDAO) {
		// sql = new SelectContractorAudit();
		this.auditDataDao = auditDataDao;
		this.auditQuestionDao = auditQuestionDao;
		this.operatorAccountDAO = operatorAccountDAO;
		this.report.setLimit(25);
		orderByDefault = "a.name";
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();
		sql.addField("a_op.requiredAuditStatus");
		sql.addField("ca.expiresDate");
		sql.addField("ao.name as operatorName");
		sql.addField("cao.status as caoStatus");
		sql.addField("cao.notes as caoNotes");
		sql.addField("cao.id as caoId");
		sql.addField("cao.recommendedStatus as caoRecommendedStatus");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
		sql.addJoin("JOIN audit_operator a_op on a_op.auditTypeID = atype.id AND a_op.opID = cao.opID");

		sql.addJoin("JOIN accounts ao on ao.id = cao.opID");

		if(permissions.isOperator()) {
			boolean requiresActivePolicy = false;
			OperatorAccount operator = operatorAccountDAO.find(permissions.getAccountId());
			if(operator != null) {
				for(AuditOperator auditOperator : operator.getAudits()) {
					if(auditOperator.getAuditType().getClassType().equals(AuditTypeClass.Policy) 
							&& auditOperator.getRequiredAuditStatus().isActive()) {
						requiresActivePolicy = true;
						break;
					}	
				}
			}
			if(requiresActivePolicy)
				sql.addWhere("ca.auditStatus IN ('Resubmitted','Active')");
			else
				sql.addWhere("ca.auditStatus IN ('Submitted','Active','Resubmitted')");
		}
		
		sql.addWhere("a.active = 'Y'");

		if (getUser().getAccount().isOperator()) {
			sql.addWhere("cao.opid = " + getUser().getAccount().getIdString());
		}

		
		
		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowHasClosedDate(true);
		getFilter().setShowExpiredDate(false);
		getFilter().setShowPercentComplete(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowPercentComplete(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setShowPolicyType(true);
		getFilter().setShowCaoStatus(true);
		getFilter().setShowAuditStatus(true);
		getFilter().setShowRecommendedStatus(true);
	}

	/**
	 * returns the proper answer(s) of the questionData, which holds supplemental
	 * question data that is not returned in the main query 
	 * 
	 * @param auditId
	 * @param purpose - kind of like a column name. Known keys for this are: policyFile, aiWaiverSub, aiName, aiMatches, aiOther or limits
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

	/**
	 * Query all "child" answers associated with policies
	 * (called only once)
	 */
	private void loadSecondaryData() {
		try {

			questionData = new HashMap<String, Map<Integer, List<AuditData>>>();

			List<Integer> theseAudits = new Vector<Integer>();
			Map<Integer, String> theseAuditTypes = new HashMap<Integer, String>();
			for (DynaBean bean : data) {
				theseAudits.add(((Long) bean.get("auditID")).intValue());
				theseAuditTypes.put(((Long) bean.get("auditID")).intValue(),
						(String) bean.get("auditName"));
			}

			
			Map<Integer, List<AuditData>> limits = null;
			{
				/*****  Load our Limits *****/
				List<AuditData> answers = auditDataDao.findPolicyLimits(theseAudits);

				// if we got data, populate the "limits" section of our map
				if (answers != null && answers.size() > 0) {
					
					limits = new HashMap<Integer, List<AuditData>>();
					
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
					
					questionData.put("limits", limits);
				}
			}

			// These are the 3 questions listed as part of the Additional Insured subCategory
			List<AuditQuestion> questions = auditQuestionDao
					.findQuestionsByUniqueCodes(Arrays.asList("aiName",
							"policyFile", "aiWaiverSub", "aiMatches", "aiOther"));

			{
				// Fill out the double map (AuditTypeName->UniqueCode->AuditQuestion)
				// We need this later on so we can find the answer to these questions
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

			
			// Now we have all of our "formal data", let's go get the "actual data"
			Map<Integer, AnswerMap> answersForAllAudits = auditDataDao
					.findAnswersQuestionList(theseAudits, questions);

			for (Integer thisAuditId : answersForAllAudits.keySet()) {
				// We're looking at all the answers to additionalInsured questions for a given auditID
				// First use the anchor column "aiName" to see if it applies to this operator
				// Then add the other fields if necessary
				Map<String, AuditQuestion> byUniqueCode = questionsKeyedByUniqueKey
						.get(theseAuditTypes.get(thisAuditId));

				AnswerMap answersForThisAudit = answersForAllAudits
						.get(thisAuditId);

				AuditQuestion aiNameQuestion = byUniqueCode.get("aiName");

				List<AuditData> aiNameAnswers = null;
				
				if( aiNameQuestion != null && answersForThisAudit != null ) {
					aiNameAnswers = answersForThisAudit.getAnswerList(aiNameQuestion
							.getId());	
				}
				
				if (aiNameAnswers != null) {

					for( AuditData aiNameAnswer : aiNameAnswers ) {
						
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
	
						// find get all the operator's legal names
						if (permissions.isOperator()) {
							OperatorAccount thisOp = (OperatorAccount) getUser()
									.getAccount();
	
							List<AccountName> names = thisOp.getNames();
	
							if (names != null) {
								for (AccountName accountName : names) {
									if (accountName.getName().equalsIgnoreCase(
											aiNameAnswer.getAnswer())) {
										answers.add(aiNameAnswer);
										break;
									}
								}
							}
						}
						else if ( permissions.seesAllContractors() ) {
							answers.add(aiNameAnswer);
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

						List<AuditData> aiAnswers = questionData.get("aiName")
								.get(thisAuditId);

						if (aiAnswers != null && aiAnswers.size() > 0) {

							for (AuditData aiAnswer : aiAnswers) {
								AuditData tmpData = answersForThisAudit.get(
										question.getId(), aiAnswer.getId());
								if( tmpData != null ) {
									if( uniqueKey.equals("aiWaiverSub")) {
										if( answers.size() == 0 ) {
											answers.add(tmpData);
										}
									}
									else {
										answers.add(tmpData);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getFormattedDollarAmount( String answer )  {
		String response = "$0";
		
		try {
			String temp = answer.replaceAll(",", "");
			DecimalFormat decimalFormat = new DecimalFormat("$#,##0");
			
			Long input = new Long( temp );
			
			response = decimalFormat.format(input);
		}
		catch( Exception e ) {
			System.out.println("unable to format as money: " + answer);
		}
		return response;
	}
	
	public String getAiNameOrSupercededName( AuditData aiName ) {
		String response = "";
		
		List<AuditData> matchAnswers = getDataForAudit( aiName.getAudit().getId(), "aiMatches");
		List<AuditData> otherAnswers = getDataForAudit( aiName.getAudit().getId(), "aiOther");
		
		if( aiName != null ) {
			
			response = aiName.getAnswer();
			
			if( matchAnswers != null ) {
				for( AuditData matchAnswer : matchAnswers ) {
					if( matchAnswer != null && "No".equals(matchAnswer.getAnswer()) && matchAnswer.getParentAnswer().getId() == aiName.getId()) {
						if( otherAnswers != null ) {
							for( AuditData otherAnswer : otherAnswers ) {
								if( otherAnswer != null && otherAnswer.getAnswer() != null && otherAnswer.getParentAnswer().getId() == aiName.getId() ) {
									return otherAnswer.getAnswer();
								}
							}
						}
					}
				}
			}
		}
		
		return response;
		
	}
}
