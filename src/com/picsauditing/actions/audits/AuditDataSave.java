package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.YesNo;

public class AuditDataSave extends PicsActionSupport {
	AuditData auditData = null;
	AuditDataDAO dao = null;
	private AuditCategoryDataDAO catDataDAO = null;
	
	public AuditDataSave(AuditDataDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {

		try {
			if (!forceLogin())
				return LOGIN;

			AuditData newCopy = null;
			
			try
			{
				newCopy = dao.findAnswerToQuestion(auditData.getAudit()
						.getId(), auditData.getQuestion().getQuestionID());
			}
			catch( NoResultException notReallyAProblem ) {}
				
				
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
		
		List<AuditCatData> catData = catDataDAO.findAllAuditCatData(auditID, catID);
		for(AuditCatData data : catData) {
			if("Yes".equals(data.isAppliesB())) {
				return;
			}
		
		int requiredAnsweredCount = 0;
		int answeredCount = 0;
		int requiredCount = 0;
		int yesNACount = 0;
		
		// Get a map of all answers in this audit
		Map<Integer, AuditData> answers = this.dao.findAnswers(auditID);
		
		// Get a list of questions/answers for this category
		for(AuditSubCategory subCategory : category.getSubCategories()) {
			for(AuditQuestion question : subCategory.getQuestions()) {
				String tempIsRequired = question.getIsRequired();
				boolean isRequired = "Yes".equals(tempIsRequired);
				
				if ("Depends".equals(question.getDependsOnAnswer())){
					// SEE question.getDependsOnQuestion()
					int dependsOnQID = question.getDependsOnQuestion().getQuestionID();
					String dependsOnAnswer = question.getDependsOnAnswer();
					if (dependsOnAnswer.equals(answers.get(dependsOnQID)))
						isRequired = true;
				}//if
				String answerToQuestion = question.getAnswer().getAnswer();
				if ("Yes".equals(answerToQuestion) || "NA".equals(answerToQuestion))
					yesNACount++;
				if (isRequired){
					requiredCount++;
					if (!"".equals(answerToQuestion) && !com.picsauditing.PICS.DateBean.NULL_DATE_DB.equals(answerToQuestion))
						requiredAnsweredCount++;
				}//if
				if (!"".equals(answerToQuestion))
					answeredCount++;
				
				
			}
		}
		int percentCompleted = requiredAnsweredCount/requiredCount;
		data.setPercentCompleted(percentCompleted*100);
		//String tempPercentCompleted = getShowPercent();
		//String tempPercentVerified = getShowPercent(yesNACount,requiredCount);
		
		catDataDAO.save(data);
		}
	}	

	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
