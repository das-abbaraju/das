package com.picsauditing.jpa.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.picsauditing.PICS.PICSFileType;

@SuppressWarnings("serial")
@Entity
@Table(name = "file_attachment")
public class FileAttachment extends BaseTable {

	protected String fileHash;
	protected Date modifiedDate;
	protected String fileName;
	protected String directory;
	protected String extension;
	protected long fileSize;
	protected PICSFileType tableType;
	protected int foreignKeyID;

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	@Enumerated(EnumType.STRING)
	public PICSFileType getTableType() {
		return tableType;
	}

	public void setTableType(PICSFileType tableType) {
		this.tableType = tableType;
	}

	public int getForeignKeyID() {
		return foreignKeyID;
	}

	public void setForeignKeyID(int foreignKeyID) {
		this.foreignKeyID = foreignKeyID;
	}

}
