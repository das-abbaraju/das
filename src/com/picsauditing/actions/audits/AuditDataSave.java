package com.picsauditing.actions.audits;

import java.util.Date;

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
			
			
			if( oldCopy.getDataID() != 0 )  //check if this is a currently existing record or not
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
							if( auditData.getIsCorrectBoolean() )
							{
								oldCopy.setDateVerified(new Date());
							}
							else
							{
								oldCopy.setDateVerified(null);
							}
							
							oldCopy.setIsCorrectBoolean(auditData.getIsCorrectBoolean());
						}
					}
				}
			}
			else
			{
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
		String[] ids = (String[]) ActionContext.getContext().getParameters().get("auditData.dataID");
		
		if( ids != null && ids.length > 0 )
		{
			int id = new Integer(ids[0]).intValue();
			
			
			oldCopy = dao.find(id);
		}
	}
	
	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
