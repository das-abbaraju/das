package com.picsauditing.employeeguard.viewmodel.model;

import com.picsauditing.employeeguard.entities.SkillType;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;

import java.util.Date;

public class Skill implements Comparable<Skill> {

    private int id;
    private int accountId;
    private Date endDate;
    private String name;
    private String description;
    private SkillType skillType;
    private SkillStatus skillStatus;
    private boolean doesNotExpire;

    public Skill(final Builder builder) {
        this.id = builder.id;
        this.accountId = builder.accountId;
        this.endDate = builder.endDate == null ? null : new Date(builder.endDate.getTime());
        this.name = builder.name;
        this.description = builder.description;
        this.skillType = builder.skillType;
        this.skillStatus = builder.skillStatus;
        this.doesNotExpire = builder.doesNotExpire;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public SkillStatus getSkillStatus() {
        return skillStatus;
    }

    public boolean isDoesNotExpire() {
        return doesNotExpire;
    }

    @Override
    public int compareTo(Skill that) {
        if (this.skillStatus == that.skillStatus) {
            return this.name.compareToIgnoreCase(that.name);
        }

        return this.skillStatus.compareTo(that.skillStatus);
    }

    public static class Builder {

        private int id;
        private int accountId;
        private Date endDate;
        private String name;
        private String description;
        private SkillType skillType;
        private SkillStatus skillStatus;
        private boolean doesNotExpire;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder accountId(int accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder endDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder skillType(SkillType skillType) {
            this.skillType = skillType;
            return this;
        }

        public Builder skillStatus(SkillStatus skillStatus) {
            this.skillStatus = skillStatus;
            return this;
        }

        public Builder doesNotExpire(boolean doesNotExpire) {
            this.doesNotExpire = doesNotExpire;
            return this;
        }

        public Skill build() {
            return new Skill(this);
        }
    }
}
