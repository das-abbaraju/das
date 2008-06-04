package com.picsauditing.actions.audits;

import java.util.Date;

import javax.persistence.NoResultException;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

public class AuditDataSave extends PicsActionSupport implements Preparable 
{
	AuditData auditData = null;
	AuditDataDAO dao = null;
	protected AuditData oldCopy;
	
	public AuditDataSave( AuditDataDAO dao )
	{
		this.dao = dao;
	}
	
	public String execute() throws Exception {

		try
		{
			if (!forceLogin())
				return LOGIN;
			
			
			if( oldCopy != null )  //check if this is a currently existing record or not
			{
				if( auditData.getAnswer() != null )  //if answer is being set, then we are not currently verifying
				{
					if( oldCopy.getAnswer() == null || ! oldCopy.getAnswer().equals(auditData.getAnswer()))
					{
						oldCopy.setDateVerified(null);
						oldCopy.setIsCorrect(null);
						oldCopy.setVerifiedAnswer(null);
						oldCopy.setWasChanged(YesNo.Yes);
						oldCopy.setAnswer(auditData.getAnswer());
					}
				}
				else  //we were handed the verification parms instead of the edit parms
				{
					if( auditData.getVerifiedAnswer() != null )
					{
						oldCopy.setVerifiedAnswer(auditData.getVerifiedAnswer());
					}

					
					if( ActionContext.getContext().getParameters().get("auditData.isCorrectBoolean") != null
							|| ActionContext.getContext().getParameters().get("auditData.isCorrect") != null )
					{
						if( auditData.getIsCorrect() != oldCopy.getIsCorrect() )
						{
							if( auditData.isVerified() )
							{
								oldCopy.setDateVerified(new Date());
							}
							else
							{
								oldCopy.setDateVerified(null);
							}
							
							oldCopy.setVerified(auditData.isVerified());
						}
					}
				}
			}
			else
			{
				oldCopy = new AuditData();
				
				oldCopy.setAuditor(new User());
				oldCopy.getAuditor().setId(permissions.getUserId());
				
				oldCopy.setAudit(new ContractorAudit());
				oldCopy.getAudit().setId(auditData.getAudit().getId());
				
				oldCopy.setQuestion(new AuditQuestion() );
				oldCopy.getQuestion().setQuestionID(auditData.getQuestion().getQuestionID());
				
				oldCopy.setAnswer(auditData.getAnswer());
				
				oldCopy.setDateVerified(null);
				oldCopy.setIsCorrect(null);
				oldCopy.setVerifiedAnswer(null);
				oldCopy.setWasChanged(YesNo.No);
			}

			
			if( auditData.getComment() != null )
			{
				oldCopy.setComment(auditData.getComment() );
			}
			
			dao.save(oldCopy);
			setMessage("Saved");
		}
		catch( Exception e )
		{
			e.printStackTrace();
			setMessage("An Error has Occurred");
		}
		
		return SUCCESS;
	}
	
	
	@Override
	public void prepare() throws Exception {
		String[] auditIds = (String[]) ActionContext.getContext().getParameters().get("auditData.audit.id");
		
		if( auditIds != null && auditIds.length > 0 )
		{
			int auditId = new Integer(auditIds[0]).intValue();
			
			String[] questionIds = (String[]) ActionContext.getContext().getParameters().get("auditData.question.questionID");
			
			if( questionIds != null && questionIds.length > 0 )
			{
				int questionId = new Integer(questionIds[0]).intValue();

				try
				{
					oldCopy = dao.findAnswerToQuestion(auditId, questionId);
				}
				catch( NoResultException weWillHandleThisInTheMergeLogic ){}
			}
		}
	}
	
	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
