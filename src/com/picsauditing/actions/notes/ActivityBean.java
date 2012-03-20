package com.picsauditing.actions.notes;

import java.util.Date;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;

/**
 * A bean that has all of the same fields as the Notes entity, and more, but is
 * filled with data from various sources besides Notes.
 * 
 */
public abstract class ActivityBean {

	protected int id;
	protected Account account;
	protected String summary = "";
	protected NoteCategory noteCategory = NoteCategory.Other;
	protected LowMedHigh priority = LowMedHigh.None;
	protected Employee employee;
	protected String body = null;
	protected String attachment;
	protected User createdBy;
	protected User updatedBy;
	protected Date creationDate;
	protected Date updateDate;
	protected Date sortDate;
	private AuditType auditType;
	private String auditFor;
	private AuditStatus status;
	private AuditStatus previousStatus;
	private Account operator;
	private int auditId;

	public String getBodyHtml() {
		return Utilities.escapeHTML(body);
	}

	public String getBodyHtml(int maxLength) {
		return Utilities.escapeHTML(body, maxLength);
	}

	abstract public boolean needsComplexSummaryWithTranlations();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public NoteCategory getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(NoteCategory noteCategory) {
		this.noteCategory = noteCategory;
	}

	public LowMedHigh getPriority() {
		return priority;
	}

	public void setPriority(LowMedHigh priority) {
		this.priority = priority;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setAuditType(AuditType auditType) {
		this.auditType = auditType;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;

	}

	public void setPreviousStatus(AuditStatus previousStatus) {
		this.previousStatus = previousStatus;

	}

	public AuditType getAuditType() {
		return auditType;
	}

	public String getAuditFor() {
		return auditFor;
	}

	public AuditStatus getStatus() {
		return status;
	}

	public AuditStatus getPreviousStatus() {
		return previousStatus;
	}

	public void setOperator(Account operator) {
		this.operator = operator;
	}

	public Account getOperator() {
		return operator;
	}

	public int getAuditId() {
		return auditId;
	}

	public void setAuditId(int auditId) {
		this.auditId = auditId;
	}

	public Date getSortDate() {
		return sortDate;
	}

	public void setSortDate(Date sortDate) {
		this.sortDate = sortDate;
	}

	/**
	 * Whether or not this bean's noteCategory is in the given list
	 * An empty or null list always retruns true (i.e. not being filtered)
	 */
	public boolean inNoteCategory(NoteCategory[] filterCategory) {
		if (filterCategory == null || filterCategory.length == 0)
			return true;
		for (int i = 0; i < filterCategory.length; i++) {
			if (filterCategory[i].equals(getNoteCategory()))
				return true;
		}
		return false;
	}

	abstract public boolean hasDetails();

}
