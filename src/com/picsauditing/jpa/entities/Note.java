package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "note")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "global")
public class Note implements java.io.Serializable {
	private int id;
	private Account account;
	private Date creationDate;
	private User createdBy;
	private Date updateDate;
	private User updatedBy;
	private String summary = "";
	private NoteCategory noteCategory;
	private LowMedHigh priority;
	private Account viewableBy;
	private boolean canContractorView = false;
	private NoteStatus status = NoteStatus.Closed;
	private Date followupDate;
	private String body;
	private File attachment;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "noteID")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accountID")
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@Temporal(TemporalType.DATE)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createdBy")
	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.DATE)
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updatedBy")
	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Column(name = "summary", nullable = false)
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
