package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.AccountSkillGroup;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.calculator.SkillStatusCalculator;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class SkillFactory {

    public static Skill create(final AccountSkill accountSkill) {
        return new Skill.Builder()
                .accountId(accountSkill.getAccountId())
                .description(accountSkill.getDescription())
                .doesNotExpire(accountSkill.getIntervalType().doesNotExpire())
                .id(accountSkill.getId())
                .name(accountSkill.getName())
                .skillType(accountSkill.getSkillType())
                .build();
    }

    public static List<Skill> createFromAccountSkills(final List<AccountSkill> accountSkills) {
        if (CollectionUtils.isEmpty(accountSkills)) {
            return Collections.emptyList();
        }

        List<Skill> skills = new ArrayList<>();
        for (AccountSkill accountSkill : accountSkills) {
            skills.add(create(accountSkill));
        }

        return skills;
    }

    public static List<Skill> createFromAccountSkillGroups(final List<AccountSkillGroup> accountSkillGroups) {
        if (CollectionUtils.isEmpty(accountSkillGroups)) {
            return Collections.emptyList();
        }

        List<Skill> skills = new ArrayList<>();
        for (AccountSkillGroup accountSkillGroup : accountSkillGroups) {
            skills.add(create(accountSkillGroup.getSkill()));
        }

        return skills;
    }

    public static Skill create(final AccountSkill accountSkill, final AccountSkillEmployee accountSkillEmployee) {
        if (!accountSkill.equals(accountSkillEmployee.getSkill())) {
            throw new IllegalArgumentException("The AccountSkillEmployee must be for the AccountSkill");
        }

        return new Skill.Builder()
                .accountId(accountSkill.getAccountId())
                .description(accountSkill.getDescription())
                .doesNotExpire(accountSkill.getIntervalType().doesNotExpire())
                .id(accountSkill.getId())
                .name(accountSkill.getName())
                .skillType(accountSkill.getSkillType())
                .skillStatus(calculateSkillStatus(accountSkillEmployee))
                .endDate(accountSkillEmployee.getEndDate())
                .build();
    }

    private static SkillStatus calculateSkillStatus(final AccountSkillEmployee accountSkillEmployee) {
        return SkillStatusCalculator.calculateStatusFromSkill(accountSkillEmployee);
    }

    public static List<Skill> createSortedOperatorProjectAssignmentSkillHeader(final List<AccountSkill> jobRoleSkills,
                                                                               final List<AccountSkill> projectSkills,
                                                                               final List<AccountSkill> siteRequiredSkills,
                                                                               final List<AccountSkill> corporateRequiredSkills) {
        return createFromAccountSkills(sortSkills(buildSetOfSkills(jobRoleSkills, projectSkills, siteRequiredSkills,
                corporateRequiredSkills)));
    }

    public static List<AccountSkill> createSortedOperatorProjectAssignmentAccountSkillsHeader(final List<AccountSkill> jobRoleSkills,
                                                                                              final List<AccountSkill> projectSkills,
                                                                                              final List<AccountSkill> siteRequiredSkills,
                                                                                              final List<AccountSkill> corporateRequiredSkills) {
        return sortSkills(buildSetOfSkills(jobRoleSkills, projectSkills, siteRequiredSkills,
                corporateRequiredSkills));
    }

    private static Set<AccountSkill> buildSetOfSkills(final List<AccountSkill> jobRoleSkills,
                                                      final List<AccountSkill> projectSkills,
                                                      final List<AccountSkill> siteRequiredSkills,
                                                      final List<AccountSkill> corporateRequiredSkills) {
        Set<AccountSkill> accountSkills = new HashSet<>(jobRoleSkills);
        accountSkills.addAll(siteRequiredSkills);
        accountSkills.addAll(corporateRequiredSkills);
        accountSkills.addAll(projectSkills);
        return accountSkills;
    }

    private static List<AccountSkill> sortSkills(Set<AccountSkill> accountSkills) {
        List<AccountSkill> sortedSkills = new ArrayList<>(accountSkills);
        Collections.sort(sortedSkills);
        return sortedSkills;
    }
}
