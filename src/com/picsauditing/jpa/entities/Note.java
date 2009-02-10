package com.picsauditing.jpa.entities;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
@Entity
@Table(name = "note")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class Note extends BaseTable implements java.io.Serializable {
	private Account account;
	private String summary = "";
	private NoteCategory noteCategory = NoteCategory.General;
	private LowMedHigh priority = LowMedHigh.Med;
	private Account viewableBy;
	private boolean canContractorView = false;
	private NoteStatus status = NoteStatus.Closed;
	private Date followupDate;
	private String body = null;
	private File attachment;

	public Note() {}
	
	public Note(Account account, User user, String summary) {
		super(user);
		this.account = account;
		this.summary = summary;
		setViewableById(Account.EVERYONE);
	}

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false, updatable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(name = "summary", length = 150, nullable = false)
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "noteCategory", nullable = false)
	public NoteCategory getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(NoteCategory noteCategory) {
		this.noteCategory = noteCategory;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "priority")
	public LowMedHigh getPriority() {
		return priority;
	}

	public void setPriority(LowMedHigh priority) {
		this.priority = priority;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "viewableBy")
	public Account getViewableBy() {
		return viewableBy;
	}

	public void setViewableBy(Account viewableBy) {
		this.viewableBy = viewableBy;
	}

	public void setViewableById(int viewableBy) {
		if (viewableBy == 0)
			this.viewableBy = null;
		this.viewableBy = new Account();
		this.viewableBy.setId(viewableBy);
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	public boolean isCanContractorView() {
		return canContractorView;
	}

	public void setCanContractorView(boolean canContractorView) {
		this.canContractorView = canContractorView;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status")
	public NoteStatus getStatus() {
		return status;
	}

	public void setStatus(NoteStatus status) {
		this.status = status;
	}

	@Temporal(TemporalType.DATE)
	public Date getFollowupDate() {
		return followupDate;
	}

	public void setFollowupDate(Date followupDate) {
		this.followupDate = followupDate;
	}

	public String getBody() {
		return body;
	}
	
	@Transient
	public String getBodyHtml() {
		return Utilities.escapeHTML(body);
	}

	public void setBody(String body) {
		this.body = body;
	}

	public File getAttachment() {
		return attachment;
	}

	public void setAttachment(File attachment) {
		this.attachment = attachment;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Note other = (Note) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
