package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class AccountSkillEmployeeBuilderTest {

    private static final int ID = 17828;
    private static final Date START_DATE = DateBean.buildDate(1, 1, 2013);
    private static final Date END_DATE = DateBean.buildDate(2, 1, 2013);

    private static final Date CREATED_DATE = DateBean.buildDate(3, 1, 2013);
    private static final Date UPDATED_DATE = DateBean.buildDate(4, 1, 2013);
    private static final Date DELETED_DATE = DateBean.buildDate(5, 1, 2013);

    private static final int CREATED_USER = 123;
    private static final int UPDATED_USER = 456;
    private static final int DELETED_USER = 789;

    private static final String PROFILE_DOCUMENT_NAME = "Test Document Name";
    private static final String SKILL_NAME = "Test Skill Name";
    private static final String EMPLOYEE_FIRST_NAME = "Bob";
    private static final String EMPLOYEE_LAST_NAME = "Johnson";

    @Test
    public void testBuild() {
        AccountSkillEmployee result = new AccountSkillEmployeeBuilder().id(ID)
                .accountSkill(new AccountSkillBuilder().name(SKILL_NAME).build())
                .employee(new EmployeeBuilder().firstName(EMPLOYEE_FIRST_NAME).lastName(EMPLOYEE_LAST_NAME).build())
                .profileDocument(new ProfileDocumentBuilder().name(PROFILE_DOCUMENT_NAME).build())
                .startDate(START_DATE).endDate(END_DATE)
                .createdBy(CREATED_USER).updatedBy(UPDATED_USER).deletedBy(DELETED_USER)
                .createdDate(CREATED_DATE).updatedDate(UPDATED_DATE).deletedDate(DELETED_DATE)
                .build();

        verify(result);
    }

    private void verify(AccountSkillEmployee accountSkillEmployee) {
        assertEquals(ID, accountSkillEmployee.getId());
        assertEquals(PROFILE_DOCUMENT_NAME, accountSkillEmployee.getProfileDocument().getName());
        assertEquals(SKILL_NAME, accountSkillEmployee.getSkill().getName());
        assertEquals(EMPLOYEE_FIRST_NAME, accountSkillEmployee.getEmployee().getFirstName());
        assertEquals(EMPLOYEE_LAST_NAME, accountSkillEmployee.getEmployee().getLastName());

        assertEquals(START_DATE, accountSkillEmployee.getStartDate());
        assertEquals(END_DATE, accountSkillEmployee.getEndDate());
        assertEquals(CREATED_DATE, accountSkillEmployee.getCreatedDate());
        assertEquals(UPDATED_DATE, accountSkillEmployee.getUpdatedDate());
        assertEquals(DELETED_DATE, accountSkillEmployee.getDeletedDate());
        assertEquals(CREATED_USER, accountSkillEmployee.getCreatedBy());
        assertEquals(UPDATED_USER, accountSkillEmployee.getUpdatedBy());
        assertEquals(DELETED_USER, accountSkillEmployee.getDeletedBy());
    }

}
