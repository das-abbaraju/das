package com.picsauditing.employeeguard.entities;

import com.picsauditing.database.domain.Identifiable;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "profile")
@Where(clause = "deletedDate IS NULL")
@SQLDelete(sql = "UPDATE profile SET deletedDate = NOW() WHERE id = ?")
public class Profile implements BaseEntity, Identifiable, Comparable<Profile> {

	private static final long serialVersionUID = -757907997992359311L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "appUserID", nullable = false)
	private int userId;

	@Column(nullable = false)
	private String slug;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String phone;

	@Embedded
	private Settings settings;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<Employee> employees = new ArrayList<>();

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 10)
	private List<ProfileDocument> documents = new ArrayList<>();

	@OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 10)
	private List<AccountSkillProfile> skills = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public List<ProfileDocument> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ProfileDocument> documents) {
		this.documents = documents;
	}

	public List<AccountSkillProfile> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillProfile> skills) {
		this.skills = skills;
	}

	@Transient
	public String getName() {
		return firstName + " " + lastName;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	@Override
	public int compareTo(Profile that) {
		if (getLastName() == null || getFirstName() == null) {
			return 0;
		}

		if (getFirstName().equalsIgnoreCase(that.getFirstName())) {
			if (getLastName().equalsIgnoreCase(that.getLastName())) {
				return getUserId() - that.getUserId();
			}

			return getLastName().compareToIgnoreCase(that.getLastName());
		}

		return getFirstName().compareToIgnoreCase(that.getFirstName());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Profile profile = (Profile) o;

		if (getSlug() != null ? !getSlug().equals(profile.getSlug()) : profile.getSlug() != null) return false;
		if (getEmail() != null ? !getEmail().equals(profile.getEmail()) : profile.getEmail() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getSlug() != null ? getSlug().hashCode() : 0);
		result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
		return result;
	}
}
