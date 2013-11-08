package com.picsauditing.employeeguard.viewmodel.contractor;

import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.entities.RuleType;

import java.util.List;

public class SkillModel implements Comparable<SkillModel> {

    private int id;
    private String name;
    private int numberOfEmployees;
    private RuleType ruleType;
    List<AccountSkillGroup> groups;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(int numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public List<AccountSkillGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<AccountSkillGroup> groups) {
        this.groups = groups;
    }

    @Override
    public int compareTo(SkillModel that) {
        if (this == that) {
            return 0;
        }

        return this.name.compareToIgnoreCase(that.name);
    }
}
