package com.picsauditing.actions.auditType;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOptionType;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditQuestionOption;
import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class OrderAuditChildren extends PicsActionSupport {
	protected Map<Integer, Integer> list = new HashMap<Integer, Integer>();
	protected Integer[] item = {};
	protected int id;
	protected String type;

	protected AuditTypeDAO auditTypeDAO;
	protected AuditCategoryDAO auditCategoryDAO;
	protected AuditQuestionDAO auditQuestionDAO;

	public OrderAuditChildren(AuditTypeDAO auditTypeDAO, AuditCategoryDAO auditCategoryDAO, AuditQuestionDAO auditQuestionDAO) {
		this.auditTypeDAO = auditTypeDAO;
		this.auditCategoryDAO = auditCategoryDAO;
		this.auditQuestionDAO = auditQuestionDAO;
	}

	public String execute() {
		if (type == null)
			return SUCCESS;

		for (int i = 0; i < item.length; i++) {

			try {
				list.put(item[i], i + 1);
			} catch (Exception e) {
			}
		}

		// Change the Order numbers of the AuditCategories
		if (type.equals("AuditType")) {
			AuditType auditType = auditTypeDAO.find(id);
			for (AuditCategory category : auditType.getTopCategories()) {
				category.setNumber(list.get(category.getId()));
			}
			auditTypeDAO.save(auditType);
		}

		// Change the Order numbers of the AuditSubCategories
		if (type.equals("AuditCategory")) {
			AuditCategory auditCategory = auditCategoryDAO.find(id);
			for (AuditCategory subCategory : auditCategory.getSubCategories()) {
				subCategory.setNumber(list.get(subCategory.getId()));
			}
			auditCategoryDAO.save(auditCategory);
		}

		// Change the Order numbers of the AuditQuestions
		if (type.equals("AuditCategoryQuestions")) {
			AuditCategory auditSubCategory = auditCategoryDAO.find(id);
			for (AuditQuestion question : auditSubCategory.getQuestions()) {
				question.setNumber(list.get(question.getId()));
			}
			auditCategoryDAO.save(auditSubCategory);
		}
		
		// Change the Order numbers of the AuditQuestions
		if (type.equals("AuditQuestionOption")) {
			AuditOptionType auditOptionType = auditQuestionDAO.findOptionType(id);
			for (AuditQuestionOption questionOption : auditOptionType.getQuestionOptions()) {
				questionOption.setNumber(list.get(questionOption.getId()));
			}
			
			auditQuestionDAO.save(auditOptionType);
		}

		return SUCCESS;
	}

	public Integer[] getItem() {
		return item;
	}

	public void setItem(Integer[] item) {
		this.item = item;
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
