package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account_group")
@DiscriminatorValue("Group")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "INSERT INTO account_group (accountID, createdBy, createdDate, deletedBy, deletedDate, description, name, type, updatedBy, updatedDate) VALUES (?, ?, ?, ?, ?, ?, ?, 'Group', ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_group SET deletedDate = NOW() WHERE id = ?")
public class Group extends AccountGroup implements Comparable<Group> {

	private static final long serialVersionUID = 7074027976165804080L;

	@OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	@BatchSize(size = 5)
	private List<ProjectRole> projects = new ArrayList<>();

	public List<ProjectRole> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectRole> projects) {
		this.projects = projects;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Group that = (Group) o;

		if (getAccountId() != that.getAccountId()) return false;
		if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * getAccountId();
		result = 31 * result + (getName() != null ? getName().hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final Group that) {
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
