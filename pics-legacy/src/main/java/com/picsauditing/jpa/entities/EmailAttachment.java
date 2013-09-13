package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "email_attachment")
public class EmailAttachment implements java.io.Serializable {
	private int id;
	private EmailQueue emailQueue;
	private String fileName = "";
	private int fileSize = 0;
	private byte[] content;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "emailID", nullable = false)
	public EmailQueue getEmailQueue() {
		return emailQueue;
	}

	public void setEmailQueue(EmailQueue emailQueue) {
		this.emailQueue = emailQueue;
	}

	@Column(name = "fileName", length = 150)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Column(name = "fileSize", nullable = false)
	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	@Lob
	@Column(name = "content", nullable = false)
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
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
		final EmailAttachment other = (EmailAttachment) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
