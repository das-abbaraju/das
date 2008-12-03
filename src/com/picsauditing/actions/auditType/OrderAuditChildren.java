package com.picsauditing.actions.auditType;

import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditSubCategoryDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class OrderAuditChildren extends PicsActionSupport {
	protected Map<Integer, Integer> list = new HashMap<Integer, Integer>();
	protected int id;
	protected String type;
	
	protected AuditTypeDAO auditTypeDAO;
	protected AuditCategoryDAO auditCategoryDAO;
	protected AuditSubCategoryDAO auditSubCategoryDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	
	public OrderAuditChildren(AuditTypeDAO auditTypeDAO, AuditCategoryDAO auditCategoryDAO, AuditSubCategoryDAO auditSubCategoryDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditSubCategoryDAO = auditSubCategoryDAO;
	}	
	
	public String execute() {
		if (type==null)
			return SUCCESS;
		
		String[] listString = (String[]) ActionContext.getContext().getParameters().get("list[]");
		for(int i=0; i < listString.length; i++) {

			try {
				int id = Integer.parseInt(listString[i]);
				list.put(id, i+1);
			} catch(Exception e){}
		}

		// Change the Order numbers of the AuditCategories
		if (type.equals("AuditType")) {
			AuditType auditType = auditTypeDAO.find(id);
			for (AuditCategory category : auditType.getCategories()) {
				category.setNumber(list.get(category.getId()));
			}
			auditTypeDAO.save(auditType);
		}		
		
		// Change the Order numbers of the AuditSubCategories
		if (type.equals("AuditCategory")) {
			AuditCategory auditCategory = auditCategoryDAO.find(id);
			for (AuditSubCategory subCategory : auditCategory.getSubCategories()) {
				subCategory.setNumber(list.get(subCategory.getId()));
			}
			auditCategoryDAO.save(auditCategory);
		}
		
		// Change the Order numbers of the AuditQuestions
		if (type.equals("AuditSubCategory")) {
			AuditSubCategory auditSubCategory = auditSubCategoryDAO.find(id);
			for (AuditQuestion question : auditSubCategory.getQuestions()) {
				question.setNumber((short)list.get(question.getId()).intValue());
			}
			auditSubCategoryDAO.save(auditSubCategory);
		}		

		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
