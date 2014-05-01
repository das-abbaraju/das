package com.picsauditing.employeeguard.entities;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "project_account_skill_group")
@Where(clause = "deletedDate IS NULL")
@SQLDelete(sql = "UPDATE project_account_skill_group SET deletedDate = NOW() WHERE id = ?")

/**
 * This entity was used to auto attach the skill to the project, if the skill existed in a role attached to a project.
 * This is no longer used - remove entity and drop table in future.
 * @deprecated
 */
@Deprecated
public class ProjectSkillRole implements BaseEntity {

    private static final long serialVersionUID = -7630640628273395059L;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "projectGroupID", nullable = false)
    private ProjectRole projectRole;

    @ManyToOne
    @JoinColumn(name = "projectSkillID", nullable = false)
    private ProjectSkill projectSkill;

    private int createdBy;
    private int updatedBy;
    private int deletedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;

    public static transient final Comparator<ProjectSkillRole> COMPARATOR = new Comparator<ProjectSkillRole>() {
        @Override
        public int compare(ProjectSkillRole o1, ProjectSkillRole o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }

            if (areEqual(o1, o2)) {
                return 0;
            }

            if (!o1.projectSkill.equals(o2.projectSkill)) {
                return -1;
            }

            return 1;
        }

        private boolean areEqual(ProjectSkillRole o1, ProjectSkillRole o2) {
            return o1.projectSkill.equals(o2.projectSkill) && o1.projectRole.equals(o2.projectRole);
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProjectRole getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(ProjectRole projectRole) {
        this.projectRole = projectRole;
    }

    public ProjectSkill getProjectSkill() {
        return projectSkill;
    }

    public void setProjectSkill(ProjectSkill projectSkill) {
        this.projectSkill = projectSkill;
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

        ProjectSkillRole that = (ProjectSkillRole) o;

        if (projectRole != null ? !projectRole.equals(that.projectRole) : that.projectRole != null) return false;
        if (projectSkill != null ? !projectSkill.equals(that.projectSkill) : that.projectSkill != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * (projectRole != null ? projectRole.hashCode() : 0);
        result = 31 * result + (getProjectSkill() != null ? getProjectSkill().hashCode() : 0);
        return result;
    }

}
