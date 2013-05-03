package com.picsauditing.actions.tagit;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OptionGroupTagit extends PicsActionSupport implements TagitAction {
	@Autowired
	AuditOptionValueDAO auditOptionValueDAO;
	@Autowired
	AuditDataDAO auditDataDAO;

	private int optionGroupId;
	private int questionId;
	private int auditId;

	@SuppressWarnings("unchecked")
	@Override
	public String getItemsInJson() {
		List<AuditOptionValue> countries = auditOptionValueDAO
				.findOptionValuesByOptionGroupId(optionGroupId);

		JSONArray result = new JSONArray();
		for (AuditOptionValue country : countries) {
			result.add(toTaggitJson(country));
		}

		jsonArray = result;

		return JSON_ARRAY;
	}

	@Override
	public String getItemsSelected() {
		JSONArray itemsAlreadySelected = null;

		AuditData answerStored = auditDataDAO.findAnswerByAuditQuestion(
				auditId, questionId);

		if (answerStored != null && !Strings.isEmpty(answerStored.getAnswer())) {
			itemsAlreadySelected = (JSONArray) JSONValue
				.parse(answerStored.getAnswer());
		}

		if (itemsAlreadySelected == null)
			itemsAlreadySelected = new JSONArray();

		jsonArray = itemsAlreadySelected;

		return JSON_ARRAY;
	}

	@SuppressWarnings("unchecked")
	private JSONObject toTaggitJson(AuditOptionValue auditOptionValue) {
		JSONObject obj = new JSONObject();
		obj.put("id", auditOptionValue.getUniqueCode());
		obj.put("text", auditOptionValue.getName().toString());
		return obj;
	}

	public int getOptionGroupId() {
		return optionGroupId;
	}

	public void setOptionGroupId(int optionGroupId) {
		this.optionGroupId = optionGroupId;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getAuditId() {
		return auditId;
	}

	public void setAuditId(int auditId) {
		this.auditId = auditId;
	}

}
