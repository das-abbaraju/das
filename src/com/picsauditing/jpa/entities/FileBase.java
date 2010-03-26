package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.util.FileUtils;

@Entity
@Table(name = "pfile", catalog = "pics_files")
public class FileBase {

	protected int id;
	protected String fileHash;
	protected Date modifiedDate;
	protected String mimeType;
	protected long fileSize;
	protected String extension;
	protected PICSFileType tableType;
	protected int foreignKeyID;
	protected byte[] fileData;

	public FileBase() {
	}

	public FileBase(File file, String fileContentType, String fileFileName, PICSFileType fileType, int foreignKeyID)
			throws IOException {
		
		setFileHash(FileUtils.getFileSHA(file));
		setMimeType(fileContentType);
		setExtension(FileUtils.getExtension(fileFileName));
		setModifiedDate(new Date());
		setTableType(fileType);
		setForeignKeyID(foreignKeyID);
		setFileSize(file.length());
		
		setFileData(FileUtils.getBytesFromFile(file));
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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

	@Lob
	@Basic(fetch=FetchType.LAZY)
	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (id == 0)
			return false;

		try {
			FileBase other = (FileBase) obj;
			if (other.getId() == 0)
				return false;

			return id == other.getId();
		} catch (Exception e) {
			System.out.println("Error comparing BaseFile objects: " + e.getMessage());
			return false;
		}
	}

	@Transient
	public String getFileName() {
		return tableType.toString() + foreignKeyID + "." + this.extension;
	}

}
