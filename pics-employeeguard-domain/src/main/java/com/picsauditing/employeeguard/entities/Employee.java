package com.picsauditing.employeeguard.entities;

import com.picsauditing.employeeguard.entities.duplicate.UniqueIndexable;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "account_employee")
@Where(clause = "deletedDate IS NULL")
@SQLInsert(sql = "insert into account_employee (accountId, createdBy, createdDate, deletedBy, deletedDate, email, emailToken, firstName, lastName, phone, positionName, profileID, slug, updatedBy, updatedDate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE deletedBy = 0, deletedDate = null, updatedBy = 0, updatedDate = null")
@SQLDelete(sql = "UPDATE account_employee SET deletedDate = NOW() WHERE id = ?")
public class Employee implements BaseEntity, Comparable<Employee> {

	private static final long serialVersionUID = 2426185581097186606L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private int accountId;

	@ManyToOne
	@JoinColumn(name = "profileID")
	private Profile profile;

	private String slug;
	private String firstName;
	private String lastName;
	private String positionName;
	private String email;
	private String phone;
	private String emailToken;

	private int createdBy;
	private int updatedBy;
	private int deletedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL AND groupID IN (SELECT g.id FROM account_group g WHERE g.type = 'Group')")
	private List<GroupEmployee> groups = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL AND groupID IN (SELECT r.id FROM account_group r WHERE r.type = 'Role')")
	private List<RoleEmployee> roles = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	private List<AccountSkillEmployee> skills = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@Where(clause = "deletedDate IS NULL")
	private List<ProjectRoleEmployee> projectRoles = new ArrayList<>();

	public Employee() {
	}

	public Employee(int id, int accountId) {
		this.id = id;
		this.accountId = accountId;
	}

	public Employee(Employee employee) {
		this.id = employee.id;
		this.accountId = employee.accountId;
		this.profile = employee.profile;
		this.slug = employee.slug;
		this.firstName = employee.firstName;
		this.lastName = employee.lastName;
		this.positionName = employee.positionName;
		this.email = employee.email;
		this.phone = employee.phone;
		this.emailToken = employee.emailToken;
		this.createdBy = employee.createdBy;
		this.createdDate = employee.createdDate;
		this.updatedBy = employee.updatedBy;
		this.updatedDate = employee.updatedDate;
		this.deletedBy = employee.deletedBy;
		this.deletedDate = employee.deletedDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmailToken() {
		return emailToken;
	}

	public void setEmailToken(String emailToken) {
		this.emailToken = emailToken;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public int getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(int deletedBy) {
		this.deletedBy = deletedBy;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public List<GroupEmployee> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupEmployee> groups) {
		this.groups = groups;
	}

	public List<RoleEmployee> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleEmployee> roleEmployee) {
		this.roles = roleEmployee;
	}

	public List<AccountSkillEmployee> getSkills() {
		return skills;
	}

	public void setSkills(List<AccountSkillEmployee> skills) {
		this.skills = skills;
	}

	public List<ProjectRoleEmployee> getProjectRoles() {
		return projectRoles;
	}

	public void setProjectRoles(List<ProjectRoleEmployee> roles) {
		this.projectRoles = roles;
	}

	@Transient
	public String getName() {
		return getFirstName() + " " + getLastName();
	}

    public final static class EmployeeAccountEmailUniqueKey implements UniqueIndexable {

        private final int id;
        private final int accountId;
        private final String email;

        public EmployeeAccountEmailUniqueKey(final int id, final int accountId, final String email) {
            this.id = id;
            this.accountId = accountId;
            this.email = email;
        }

        @Override
        public Map<String, Object> getUniqueIndexableValues() {
            return Collections.unmodifiableMap(new HashMap<String, Object>() {
	            {
		            put("accountId", accountId);
		            put("email", email);
	            }
            });
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public final static class EmployeeAccountSlugUniqueKey implements UniqueIndexable {

        private final int id;
        private final int accountId;
        private final String slug;

        public EmployeeAccountSlugUniqueKey(final int id, final int accountId, final String slug) {
            this.id = id;
            this.accountId = accountId;
            this.slug = slug;
        }

        @Override
        public Map<String, Object> getUniqueIndexableValues() {
            return Collections.unmodifiableMap(new HashMap<String, Object>() {
                {
                    put("accountId", accountId);
                    put("slug", slug);
                }
            });
        }

        @Override
        public int getId() {
            return id;
        }
    }

    public final static class EmployeeAccountEmailAndSlugUniqueKey implements UniqueIndexable {

        private final int id;
        private final int accountId;
        private final String email;
        private final String slug;

        public EmployeeAccountEmailAndSlugUniqueKey(final int id, final int accountId, final String email, final String slug) {
            this.id = id;
            this.accountId = accountId;
            this.email = email;
            this.slug = slug;
        }

        @Override
        public Map<String, Object> getUniqueIndexableValues() {
            return Collections.unmodifiableMap(new HashMap<String, Object>() {
                {
                    put("accountId", accountId);
                    put("email", email);
                    put("slug", slug);
                }
            });
        }

        @Override
        public int getId() {
            return id;
        }
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Employee that = (Employee) o;

		if (getAccountId() != that.getAccountId()) return false;
		if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = 31 * getAccountId();
		result = 31 * result + (getSlug() != null ? getSlug().hashCode() : 0);
		result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
		return result;
	}

	@Override
	public int compareTo(final Employee that) {
		if (getLastName() == null || getFirstName() == null) {
			return 0;
		}

		if (getFirstName().equalsIgnoreCase(that.getFirstName())) {
			return getLastName().compareToIgnoreCase(that.getLastName());
		}

		return getFirstName().compareToIgnoreCase(that.getFirstName());
	}

	@Override
	public String toString() {
		return id + " " + getName();
	}
}
