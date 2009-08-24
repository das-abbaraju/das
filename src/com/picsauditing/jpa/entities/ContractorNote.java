package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_notes")
public class ContractorNote implements java.io.Serializable {

	protected int id = 0;
	protected String note = null;
	protected String adminNote = null;
	protected int badNotes = 0;
	protected int badAdminNotes = 0;
	
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Lob
	@Column( name="notes")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Lob
	@Column( name="adminnotes")
	public String getAdminNote() {
		return adminNote;
	}

	public void setAdminNote(String adminNote) {
		this.adminNote = adminNote;
	}

	public int getBadNotes() {
		return badNotes;
	}

	public void setBadNotes(int badNotes) {
		this.badNotes = badNotes;
	}

	
	public int getBadAdminNotes() {
		return badAdminNotes;
	}

	public void setBadAdminNotes(int badAdminNotes) {
		this.badAdminNotes = badAdminNotes;
	}

}
