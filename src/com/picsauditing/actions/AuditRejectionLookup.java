package com.picsauditing.actions;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.AuditRejectionCodeDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditRejectionCode;
import com.picsauditing.jpa.entities.ContractorAuditOperator;

@SuppressWarnings("serial")
public class AuditRejectionLookup extends PicsActionSupport {

	@Autowired
	private AuditRejectionCodeDAO auditRejectionCodeDao;
	@Autowired
	private ContractorAuditOperatorDAO contractorAuditOperatorDao;
	
	private int caoId;

	@Override
	public String execute() throws Exception {
		ContractorAuditOperator cao = contractorAuditOperatorDao.find(caoId);
		if (cao != null) {
			List<AuditRejectionCode> auditRejectionCodes = auditRejectionCodeDao.findByCaoPermissions(cao.getCaoPermissions());
			jsonArray = populateJsonArray(auditRejectionCodes);
		}
		
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray populateJsonArray(List<AuditRejectionCode> auditRejectionCodes) {
		JSONArray jsonArray = new JSONArray();
		if (CollectionUtils.isEmpty(auditRejectionCodes)) {
			return jsonArray;
		}
		
		for (AuditRejectionCode auditRejectionCode : auditRejectionCodes) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", auditRejectionCode.getAuditSubStatus().toString());
			jsonObject.put("value", auditRejectionCode.getRejectionReason());
			jsonArray.add(jsonObject);
		}
		
		return jsonArray;
	}
	
	public int getCaoId() {
		return caoId;
	}
	
	public void setCaoId(int caoId) {
		this.caoId = caoId;
	}

}
