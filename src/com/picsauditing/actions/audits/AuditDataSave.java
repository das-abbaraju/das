package com.picsauditing.actions.audits;

import java.util.Date;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;

public class AuditDataSave extends PicsActionSupport implements Preparable 
{
	AuditData auditData = null;
	AuditDataDAO dao = null;
	
	public AuditDataSave( AuditDataDAO dao )
	{
		this.dao = dao;
	}
	
	public String execute() throws Exception {

		try
		{
			if (!forceLogin())
				return LOGIN;
			
			
			if( auditData.getDataID() != 0 )
			{
				AuditData oldCopy = dao.find(auditData.getDataID());		
			
					if( auditData.getIsCorrect() != oldCopy.getIsCorrect() )
					{
						if( auditData.getIsCorrectBoolean() )
						{
							auditData.setDateVerified(new Date());
						}
						else
						{
							auditData.setDateVerified(null);
						}
					}
	
					if( (auditData.getAnswer() == null ^ oldCopy.getAnswer() == null ) 
							|| (auditData.getAnswer() != null && oldCopy.getAnswer() != null && ! auditData.getAnswer().equals( oldCopy.getAnswer() ) ) 
					)
					{
						auditData.setWasChanged(YesNo.Yes);
					}
			}
			else
			{
				auditData.setAuditor(new User());
				auditData.getAuditor().setId(permissions.getUserId());
			}
	
			dao.save(auditData);
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
			
			auditData = dao.find(id);
		}
	}
	
	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
