package com.picsauditing.actions.audits;

import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.NoResultException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.pqf.SubCategoryBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

public class AuditDataSave extends PicsActionSupport {
	AuditData auditData = null;
	AuditDataDAO dao = null;

	public AuditDataSave(AuditDataDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {

		try {
			if (!forceLogin())
				return LOGIN;

			AuditData newCopy = dao.findAnswerToQuestion(auditData.getAudit()
					.getId(), auditData.getQuestion().getQuestionID());
			if (newCopy == null) // insert mode
			{
				dao.save(auditData);
			} else // update mode
			{
				if (auditData.getAnswer() != null) // if answer is being set,
													// then we are not currently
													// verifying
				{
					if (auditData.getAnswer() == null
							|| !newCopy.getAnswer().equals(
									auditData.getAnswer())) {
						newCopy.setDateVerified(null);
						newCopy.setIsCorrect(null);
						newCopy.setVerifiedAnswer(null);
						newCopy.setAnswer(auditData.getAnswer());

						if (newCopy.getAudit().getAuditStatus().equals(
								AuditStatus.Submitted)) // double check this
						{
							newCopy.setWasChanged(YesNo.Yes);
						}
					}
				} else // we were handed the verification parms instead of the
						// edit parms
				{
					if (auditData.getVerifiedAnswer() != null) {
						newCopy
								.setVerifiedAnswer(auditData
										.getVerifiedAnswer());
					}

					if (ActionContext.getContext().getParameters().get(
							"auditData.isCorrect") != null) {
						if (auditData.getIsCorrect() != newCopy.getIsCorrect()) {
							if (auditData.isVerified()) {
								newCopy.setDateVerified(new Date());
							} else {
								newCopy.setDateVerified(null);
							}

							newCopy.setVerified(auditData.isVerified());
						}
					}
				}

				if (auditData.getComment() != null) {
					newCopy.setComment(auditData.getComment());
				}

				dao.save(newCopy);
			}

			//hook to calculation
			// read/update the ContractorAudit and AuditCatData
			
			
			setMessage("Saved");
		} catch (Exception e) {
			e.printStackTrace();
			setMessage("An Error has Occurred");
		}

		return SUCCESS;
	}
	
	private void updatePercentageCompleted() throws Exception {
		int auditID = auditData.getAudit().getId();
		AuditCategory category = auditData.getQuestion().getSubCategory().getCategory();
		int catID = category.getId();
		
		AuditCategoryDataDAO catDataDAO = new AuditCategoryDataDAO();
		AuditCatData catData = dao.find(auditID, catID);
		/**
		 * MOVED THIS INTO AuditCategoryDataDAO.find()
		String selectQuery = "SELECT * FROM pqfCatData "+
			"WHERE catID="+catID+" AND auditID="+auditID;
		ResultSet rs = SQLStatement.executeQuery(selectQuery);
		if (!rs.next() || !"Yes".equals(rs.getString("applies"))){
			rs.close();
			return;
		}
		rs.close();
		*/
		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int yesNACount = 0;
		
		// Get a map of all answers in this audit
		Map<Integer, AuditData> answers = this.dao.findAnswers(auditID);
		
		// Get a list of questions/answers for this category
		for(AuditSubCategory subCategory : category.getSubCategories()) {
			for(AuditQuestion question : subCategory.getQuestions()) {
				String answer = this.getString(rs, "answer");
				
				String tempIsRequired = rs.getString("isRequired");
				boolean isRequired = "Yes".equals(tempIsRequired);
				if ("Depends".equals(tempIsRequired)){
					// SEE question.getDependsOnQuestion()
					int dependsOnQID = rs.getInt("dependsOnQID");
					String dependsOnAnswer = rs.getString("dependsOnAnswer");
					if (dependsOnAnswer.equals(tempQAMap.get(dependsOnQID)))
						isRequired = true;
				}//if
				if ("Yes".equals(answer) || "NA".equals(answer))
					yesNACount++;
				if (isRequired){
					requiredCount++;
					if (!"".equals(answer) && !com.picsauditing.PICS.DateBean.NULL_DATE_DB.equals(answer))
						requiredAnsweredCount++;
				}//if
				if (!"".equals(answer))
					answeredCount++;
			}
		}
		
		catData.setPercentCompleted(100*requiredAnsweredCount,requiredCount);
		String tempPercentCompleted = getShowPercent();
		String tempPercentVerified = getShowPercent(yesNACount,requiredCount);
		
//		String updateQuery = "REPLACE INTO pqfcatdata " +
//				"(catID, auditID, applies, requiredCompleted, numAnswered, " +
//					"numRequired, percentCompleted, percentVerified) " +
//				"VALUES " +
//				"("+catID+", "+auditID+", 'Yes', "+requiredAnsweredCount+", "+answeredCount+", " +
//					""+requiredCount+", "+tempPercentCompleted+", "+tempPercentVerified+")";
		catDataDAO.save(catData);
	}


	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
