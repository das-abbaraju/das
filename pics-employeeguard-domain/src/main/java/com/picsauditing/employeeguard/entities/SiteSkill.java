package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.util.Extractor;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

@Entity
@Table(name = "site_account_skill")
@Where(clause = "deletedDate IS NULL AND deletedBy = 0")
@SQLInsert(sql = "INSERT INTO site_account_skill (createdBy, createdDate, deletedBy, deletedDate, siteId, skillID, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
public class SiteSkill implements BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int siteId;

	@ManyToOne
	@JoinColumn(name = "skillID", nullable = false)
	private AccountSkill skill;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	public SiteSkill() {
	}

	public SiteSkill(int siteId, AccountSkill skill) {
		this.siteId = siteId;
		this.skill = skill;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public AccountSkill getSkill() {
		return skill;
	}

	public void setSkill(AccountSkill skill) {
		this.skill = skill;
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

		SiteSkill siteSkill = (SiteSkill) o;

		if (siteId != siteSkill.siteId) return false;
		if (skill != null ? !skill.equals(siteSkill.skill) : siteSkill.skill != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + siteId;
		result = 31 * result + (skill != null ? skill.hashCode() : 0);
		return result;
	}

	public static transient final Extractor<SiteSkill, AccountSkill> SKILL_EXTRACTOR = new Extractor<SiteSkill, AccountSkill>() {
		@Override
		public AccountSkill extract(SiteSkill siteSkill) {
			return siteSkill.getSkill();
		}
	};

	public static transient final Comparator<SiteSkill> COMPARATOR = new Comparator<SiteSkill>() {
		@Override
		public int compare(SiteSkill o1, SiteSkill o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}

			if (areEqual(o1, o2)) {
				return 0;
			}

			if (!o1.skill.equals(o2.skill)) {
				return -1;
			}

			return 1;
		}

		private boolean areEqual(SiteSkill o1, SiteSkill o2) {
			return ((o1.skill.equals(o2.skill)) && o1.siteId == o2.siteId);
		}
	};
}
