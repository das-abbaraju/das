package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.util.Strings;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "account_skill_group")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO account_skill_group (createdBy, createdDate, deletedBy, deletedDate, groupID, skillID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_skill_group SET deletedDate = NOW() WHERE id = ?")
public class AccountSkillSiteAssignment implements BaseEntity {

	private static final long serialVersionUID = -7630640628273395059L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(nullable = false)
	private int id;

	@ManyToOne
	@JoinColumn(name = "skillID", nullable = false)
	private AccountSkill skill;

	@ManyToOne
	@JoinColumn(name = "groupID", nullable = false)
	private SiteAssignment siteAssignment;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public static transient final Comparator<AccountSkillSiteAssignment> COMPARATOR = new Comparator<AccountSkillSiteAssignment>() {
		@Override
		public int compare(AccountSkillSiteAssignment o1, AccountSkillSiteAssignment o2) {
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

		private boolean areEqual(AccountSkillSiteAssignment o1, AccountSkillSiteAssignment o2) {
			return ((o1.getSkill().equals(o2.getSkill())) && (o1.getSiteAssignment().equals(o2.getSiteAssignment())));
		}
	};

	public AccountSkillSiteAssignment() {
	}

	public AccountSkillSiteAssignment(SiteAssignment siteAssignment, AccountSkill accountSkill) {
		this.siteAssignment = siteAssignment;
		this.skill = accountSkill;
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

	public SiteAssignment getSiteAssignment() {
		return siteAssignment;
	}

	public void setSiteAssignment(SiteAssignment group) {
		this.siteAssignment = group;
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
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AccountSkillSiteAssignment that = (AccountSkillSiteAssignment) o;

		if (getSkill() != null ? !getSkill().equals(that.getSkill()) : that.getSkill() != null) return false;
		if (getSiteAssignment() != null ? !getSiteAssignment().equals(that.getSiteAssignment()) : that.getSiteAssignment() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * (getSkill() != null ? getSkill().hashCode() : 0);
		result = 31 * result + (getSiteAssignment() != null ? getSiteAssignment().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		String string = Strings.EMPTY_STRING;

		if (skill != null) {
			string += skill.toString();
		}

		if (siteAssignment != null) {
			string += " " + siteAssignment.toString();
		}

		return string;
	}

	public static transient final Extractor<AccountSkillSiteAssignment, AccountSkill> SKILL_EXTRACTOR = new Extractor<AccountSkillSiteAssignment, AccountSkill>() {
		@Override
		public AccountSkill extract(AccountSkillSiteAssignment accountSkillGroup) {
			return accountSkillGroup.getSkill();
		}
	};

	public static transient final Extractor<AccountSkillSiteAssignment, SiteAssignment> SITE_ASSIGNMENT_EXTRACTOR = new Extractor<AccountSkillSiteAssignment, SiteAssignment>() {
		@Override
		public SiteAssignment extract(AccountSkillSiteAssignment accountSkillGroup) {
			return accountSkillGroup.getSiteAssignment();
		}
	};
}
