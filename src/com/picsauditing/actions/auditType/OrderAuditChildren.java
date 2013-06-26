package com.picsauditing.actions.auditType;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.model.i18n.EntityTranslationHelper;

@SuppressWarnings("serial")
public class OrderAuditChildren extends PicsActionSupport {
	protected Map<Integer, Integer> list = new HashMap<Integer, Integer>();
	protected Integer[] item = {};
	protected int id;
	protected String type;

	@Autowired
	protected AuditTypeDAO auditTypeDAO;
	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;
	@Autowired
	protected AuditOptionValueDAO auditQuestionOptionDAO;

	public String execute() {
		if (type == null) {
			return SUCCESS;
		}

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
			EntityTranslationHelper.saveRequiredTranslationsForAuditCategory(auditCategory, permissions);
		}

		// Change the Order numbers of the AuditQuestions
		if (type.equals("AuditCategoryQuestions")) {
			AuditCategory auditSubCategory = auditCategoryDAO.find(id);
			for (AuditQuestion question : auditSubCategory.getQuestions()) {
				question.setNumber(list.get(question.getId()));
			}
			auditCategoryDAO.save(auditSubCategory);
			EntityTranslationHelper.saveRequiredTranslationsForAuditCategory(auditSubCategory, permissions);
		}

		// Change the Order numbers of the AuditQuestions
		if (type.equals("AuditOptionValue")) {
			AuditOptionGroup auditOptionType = auditQuestionOptionDAO.findOptionGroup(id);
			for (AuditOptionValue optionValue : auditOptionType.getValues()) {
				optionValue.setNumber(list.get(optionValue.getId()));
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
