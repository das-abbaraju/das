package com.picsauditing.jpa.entities;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EmployeeTest {

    @Test
    public void testCompareTo_SimpleLastName() throws Exception {
        Employee employee1 = new Employee();
        employee1.setLastName("Zoo");
        Employee employee2 = new Employee();
        employee2.setLastName("Aardvark");

        assertTrue(employee2.compareTo(employee1) < 0);
    }

    @Test
    public void testCompareTo_SimpleFirstName() throws Exception {
        Employee employee1 = new Employee();
        employee1.setFirstName("Zohar");
        employee1.setLastName("Smith");
        Employee employee2 = new Employee();
        employee2.setFirstName("Alex");
        employee2.setLastName("Smith");

        assertTrue(employee2.compareTo(employee1) < 0);
    }

    @Test
    public void testCompareTo_Title() throws Exception {
        Employee employee1 = new Employee();
        employee1.setFirstName("Alex");
        employee1.setLastName("Smith");
        employee1.setTitle("Zoologist");
        Employee employee2 = new Employee();
        employee2.setFirstName("Alex");
        employee2.setLastName("Smith");
        employee2.setTitle("Air Condition Specialist");

        assertTrue(employee2.compareTo(employee1) < 0);
    }

    @Test
    public void testCompareTo_Id() throws Exception {
        Employee employee1 = new Employee();
        employee1.setFirstName("Alex");
        employee1.setLastName("Smith");
        employee1.setTitle("Air Condition Specialist");
        employee1.setId(999);
        Employee employee2 = new Employee();
        employee2.setFirstName("Alex");
        employee2.setLastName("Smith");
        employee2.setTitle("Air Condition Specialist");
        employee2.setId(111);

        assertTrue(employee2.compareTo(employee1) < 0);

        employee2.setId(999);
        assertTrue(employee2.compareTo(employee1) == 0);
    }

}
