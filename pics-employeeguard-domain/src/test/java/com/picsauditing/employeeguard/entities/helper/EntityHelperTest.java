package com.picsauditing.employeeguard.entities.helper;

import com.picsauditing.employeeguard.entities.Employee;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class EntityHelperTest {

    private static final Date NOW = new Date();
    private static final int USER_ID = 123;

    @Test
    public void testSetCreateAuditFields() {
        Employee employee = new Employee();

        EntityHelper.setCreateAuditFields(employee, USER_ID, NOW);

        assertEquals(NOW, employee.getCreatedDate());
        assertEquals(USER_ID, employee.getCreatedBy());
    }

    @Test
    public void testSetCreateAuditFields_ListOfEntities() {
        List<Employee> employees = getEmployees();

        EntityHelper.setCreateAuditFields(employees, USER_ID, NOW);

        verifyEmployeeListCreateAuditFields(employees);
    }

    private void verifyEmployeeListCreateAuditFields(List<Employee> employees) {
        for (Employee employee : employees) {
            assertEquals(NOW, employee.getCreatedDate());
            assertEquals(USER_ID, employee.getCreatedBy());
        }
    }

    @Test
    public void testSetUpdateAuditFields() {
        Employee employee = new Employee();

        EntityHelper.setUpdateAuditFields(employee, USER_ID, NOW);

        assertEquals(NOW, employee.getUpdatedDate());
        assertEquals(USER_ID, employee.getUpdatedBy());
    }

    @Test
    public void testSetUpdateAuditFields_ListOfEntities() {
        List<Employee> employees = getEmployees();

        EntityHelper.setUpdateAuditFields(employees, USER_ID, NOW);

        verifyEmployeeListUpdateAuditFields(employees);
    }

    private void verifyEmployeeListUpdateAuditFields(List<Employee> employees) {
        for (Employee employee : employees) {
            assertEquals(NOW, employee.getUpdatedDate());
            assertEquals(USER_ID, employee.getUpdatedBy());
        }
    }

    @Test
    public void testSetDeleteAuditFields() {
        Employee employee = new Employee();

        EntityHelper.softDelete(employee, USER_ID);

        assertEquals(USER_ID, employee.getDeletedBy());
    }

    @Test
    public void testSetDeleteAuditFields_ListOfEntities() {
        List<Employee> employees = getEmployees();

        EntityHelper.softDelete(employees, USER_ID);

        verifyEmployeeListDeleteAuditFields(employees);
    }

    private void verifyEmployeeListDeleteAuditFields(List<Employee> employees) {
        for (Employee employee : employees) {
            assertEquals(USER_ID, employee.getDeletedBy());
        }
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(new Employee(), new Employee(), new Employee());
    }
}
