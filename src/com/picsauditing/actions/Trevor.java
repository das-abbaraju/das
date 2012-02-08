package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditOptionValue;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Autowired
	AuditOptionValueDAO auditOptionValueDAO;
	@Autowired
	AuditDataDAO auditDataDAO;

	private int optionGroupId;
	private int questionId;
	private int auditId;

	@Anonymous
	public String execute() throws SQLException {

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Anonymous
	public String getItemsInJson() {
		List<AuditOptionValue> countries = auditOptionValueDAO
				.findOptionValuesByOptionGroupId(optionGroupId);

		JSONArray result = new JSONArray();
		for (AuditOptionValue country : countries) {
			result.add(toTaggitJson(country));
		}

		jsonArray = result;

		return "jsonArray";

	}

	@Anonymous
	public String getItemsSelected() {
		AuditData answerStored = auditDataDAO.findAnswerByAuditQuestion(
				auditId, questionId);
		JSONArray itemsAlreadySelected = (JSONArray) JSONValue
				.parse(answerStored.getAnswer());

		jsonArray = itemsAlreadySelected;

		return "jsonArray";
	}

	@SuppressWarnings("unchecked")
	private JSONObject toTaggitJson(AuditOptionValue auditOptionValue) {
		JSONObject obj = new JSONObject();
		obj.put("id", auditOptionValue.getUniqueCode());
		obj.put("value", auditOptionValue.getName());
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