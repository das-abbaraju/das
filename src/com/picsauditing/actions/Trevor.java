package com.picsauditing.actions;

import java.sql.SQLException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionValue;

@SuppressWarnings("serial")
public class Trevor extends PicsActionSupport {
	@Autowired
	AuditOptionValueDAO auditOptionValueDAO;
	private int optionGroupId;
	@Anonymous
	public String execute() throws SQLException {

		

		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	@Anonymous
	public String getItemsInJson() {
		List<AuditOptionValue> countries = auditOptionValueDAO.findOptionValuesByOptionGroupId(optionGroupId);
		
	
		JSONArray result = new JSONArray();
		for (AuditOptionValue country : countries) {
			result.add(toTaggitJson(country));
		}
		
		jsonArray = result;
		
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
	
	
} 