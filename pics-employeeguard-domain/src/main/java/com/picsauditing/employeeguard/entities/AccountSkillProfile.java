package com.picsauditing.employeeguard.entities;

import com.picsauditing.util.Strings;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "account_skill_profile")
public class AccountSkillProfile implements BaseEntity {

	private static final long serialVersionUID = 2117121655234863607L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "skillID", nullable = false)
	private AccountSkill skill;

	@ManyToOne
	@JoinColumn(name = "profileID", nullable = false)
	private Profile profile;

	@ManyToOne
	@JoinColumn(name = "documentID")
	private ProfileDocument profileDocument;

	@Column(nullable = false)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "finishDate")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public static transient final Comparator<AccountSkillProfile> COMPARATOR = new Comparator<AccountSkillProfile>() {

		@Override
		public int compare(AccountSkillProfile o1, AccountSkillProfile o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.getSkill().equals(o2.getSkill())) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(AccountSkillProfile o1, AccountSkillProfile o2) {
			return ((o1.getSkill().equals(o2.getSkill())) && o1.getProfile().equals(o2.getProfile()));
		}
	};

	public AccountSkillProfile() {
	}

	public AccountSkillProfile(AccountSkill skill, Profile profile) {
		this.skill = skill;
		this.profile = profile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AccountSkill getSkill() {
		return skill;
	}

	public void setSkill(AccountSkill skill) {
		this.skill = skill;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public ProfileDocument getProfileDocument() {
		return profileDocument;
	}

	public void setProfileDocument(ProfileDocument profileDocument) {
		this.profileDocument = profileDocument;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountSkillProfile accountSkillProfile = (AccountSkillProfile) o;

		if (getSkill() != null ? !getSkill().equals(accountSkillProfile.getSkill()) : accountSkillProfile.getSkill() != null)
			return false;
		if (getProfile() != null ? !getProfile().equals(accountSkillProfile.getProfile()) : accountSkillProfile.getProfile() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getSkill() != null ? getSkill().hashCode() : 0);
		result = 31 * result + (getProfile() != null ? getProfile().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		String string = Strings.EMPTY_STRING;

		if (getProfile() != null) {
			string += " " + getProfile().toString();
		}

		if (skill != null) {
			string += " " + skill.getName();
		}

		return string;
	}
}
