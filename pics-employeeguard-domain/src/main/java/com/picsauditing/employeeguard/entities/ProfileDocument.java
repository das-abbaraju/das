package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "profiledocument")
@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
public class ProfileDocument implements BaseEntity, Comparable<ProfileDocument> {
	private static final long serialVersionUID = 7654576030939128656L;

	public static final Date END_OF_TIME = new Date(64060588800000l); // 4000-01-01 00:00:00.000 UTC

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "profileID", nullable = false)
	private Profile profile;

	@Enumerated(EnumType.STRING)
	@Type(type = "com.picsauditing.jpa.entities.EnumMapperWithEmptyStrings", parameters = {
			@Parameter(name = "enumClass", value = "com.picsauditing.employeeguard.entities.DocumentType"),
			@Parameter(name = "identifierMethod", value = "getDbValue"),
			@Parameter(name = "valueOfMethod", value = "fromDbValue")})
	private DocumentType documentType;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "finishDate")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	private String fileName;
	private String fileType;
	private int fileSize; // TODO: We may want to change this to a long

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public int getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	@Transient
	public boolean isDoesNotExpire() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		return calendar.get(Calendar.YEAR) > 3000;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ProfileDocument profileDocument = (ProfileDocument) o;

		if (getProfile() != null ? !getProfile().equals(profileDocument.getProfile()) : profileDocument.getProfile() != null) return false;
		if (getDocumentType() != null ? !(getDocumentType() == profileDocument.getDocumentType()) : profileDocument.getDocumentType() != null) return false;
		if (getName() != null ? !getName().equals(profileDocument.getName()) : profileDocument.getName() != null) return false;
//		if (getStartDate() != null ? !getStartDate().equals(profileDocument.getStartDate()) : profileDocument.getStartDate() != null) return false;
//		if (getEndDate() != null ? !getEndDate().equals(profileDocument.getEndDate()) : profileDocument.getEndDate() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getProfile() != null ? getProfile().hashCode() : 0);
		result = 31 * result + (getDocumentType() != null ? getDocumentType().hashCode() : 0);
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
//		result = 31 * result + (getStartDate() != null ? getStartDate().hashCode() : 0);
//		result = 31 * result + (getEndDate() != null ? getEndDate().hashCode() : 0);
		return result;
	}

    @Override
    public int compareTo(final ProfileDocument that) {
        if (this == that) {
            return 0;
        }

        int comparison = this.getName().compareToIgnoreCase(that.getName());
        if (comparison != 0) {
            return comparison;
        }

        return 0;
    }

}
