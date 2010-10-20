package com.picsauditing.actions.auditType;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;

@SuppressWarnings("serial")
public class RecalculateRules extends PicsActionSupport {	

	private AuditDecisionTableDAO dao;
	
	public RecalculateRules(AuditDecisionTableDAO dao){
		this.dao = dao;
	}
	
	@SuppressWarnings("unchecked")
	public String execute(){
		if (!forceLogin())
			return LOGIN;
		
		if("category".equals(button) || "auditType".equals(button)){
			Class clazz;
			if("category".equals(button))
				clazz = AuditCategoryRule.class;
			else
				clazz = AuditTypeRule.class;
			int lastIndex = 0;
			int count = 0;
			List<AuditRule> acrList;
			acrList = (List<AuditRule>) dao.findWhere(clazz, "id > 0", 100);
			while(acrList.size()>0){
				for(AuditRule acr : acrList){
					acr.calculatePriority();
					dao.save(acr);
				}
				count+=acrList.size();
				lastIndex = acrList.get(acrList.size()-1).getId();
				acrList = (List<AuditRule>) dao.findWhere(clazz, "id > "+lastIndex, 100);
			}		
			addActionMessage("Calculated Priority for "+count+" rules");
		}
		
		return SUCCESS;
	}
}
