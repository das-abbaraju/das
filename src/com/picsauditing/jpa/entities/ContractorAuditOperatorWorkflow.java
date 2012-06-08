package com.picsauditing.jpa.entities;

import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.cglib.core.Local;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.base.Strings;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.util.JSONUtilities;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_audit_operator_workflow")
public class ContractorAuditOperatorWorkflow extends BaseTable {

	private ContractorAuditOperator cao;
	private AuditStatus status;
	private AuditStatus previousStatus;
	private String notes;

	@ManyToOne
	@JoinColumn(name = "caoID", nullable = false)
	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Enumerated(EnumType.STRING)
	public AuditStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(AuditStatus previousStatus) {
		this.previousStatus = previousStatus;
	}

	@Column(length = 1000)
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * In the event that the notes is NOT JSON, return the notes as a String.
	 * 
	 * If the notes is JSON, then parse it and return the concatenated String
	 * from the Rejection Reasons and the comment.
	 *  
	 * JSON Format = {"noteType":"insurance","noteCodes":["Insurance.Rejection.Code.123","456"],"additionalComment":"optionalComment"} 
	 * 
	 * @return
	 */
	@Transient
	public String getMappedNote() {
		if (!JSONUtilities.mayBeJSON(notes)) {
			return notes;
		}
		
		// return the notes field as is in the event the JSON Parsing fails
		JSONObject jsonObject = parseJson();
		if (jsonObject == null) {
			return notes;
		}
				
		return buildNotes(jsonObject);
	}
	
	private JSONObject parseJson() {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(notes);
		} catch (Exception nothingWeCanDoExceptLogIt) {
			System.out.println("CAOW Note JSON Parser Error when parsing " + notes);
		}
		
		return jsonObject;
	}
	
	// TODO: change this so that it will be set to the correct locale
	private String buildNotes(JSONObject jsonObject) {
		StringBuilder concatenatedNotes = new StringBuilder();
		JSONArray reasonCodes = (JSONArray) jsonObject.get("noteCodes");
		
		for (int index = 0; index < reasonCodes.size(); index++) {
			String reasonCode = (String) reasonCodes.get(index);
			if (!Strings.isNullOrEmpty(reasonCode)) {
				concatenatedNotes.append(I18nCache.getInstance().getText("Insurance.Rejection.Reason.Code." + reasonCode, Locale.US) + "\n");
			}
		}
		
		concatenatedNotes.append(Strings.nullToEmpty((String) jsonObject.get("additionalComment")));
		
		return concatenatedNotes.toString();
	}
	
}
