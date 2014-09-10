package com.picsauditing.jpa.entities;

import org.junit.Test;

import static org.junit.Assert.*;

public class CountryBusinessSizeTest {

    @Test
    public void testIsSmallBusiness_US_Small() throws Exception {
        boolean smallBusiness = CountryBusinessSize.isSmallBusiness("US", 1);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("US", 5);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("US", 8);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("US", 10);
        assertTrue(smallBusiness);
    }

    @Test
    public void testIsSmallBusiness_US_NotSmall() throws Exception {
        boolean smallBusiness = CountryBusinessSize.isSmallBusiness("US", 11);
        assertFalse(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("US", 50);
        assertFalse(smallBusiness);
    }

    @Test
    public void testIsSmallBusiness_UK_Small() throws Exception {
        boolean smallBusiness = CountryBusinessSize.isSmallBusiness("UK", 1);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("UK", 2);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("UK", 4);
        assertTrue(smallBusiness);

        smallBusiness = CountryBusinessSize.isSmallBusiness("UK", 5);
        assertTrue(smallBusiness);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_US_Invalid_Zero_NumberOfEmployees() throws Exception {
        CountryBusinessSize.isSmallBusiness("US", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_US_Invalid_NumberOfEmployees() throws Exception {
        CountryBusinessSize.isSmallBusiness("US", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_US_Invalid_Country() throws Exception {
        CountryBusinessSize.isSmallBusiness("", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_US_Invalid_null_Country() throws Exception {
        CountryBusinessSize.isSmallBusiness("null", 0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_UK_Invalid_Zero_NumberOfEmployees() throws Exception {
        CountryBusinessSize.isSmallBusiness("UK", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_UK_Invalid_NumberOfEmployees() throws Exception {
        CountryBusinessSize.isSmallBusiness("UK", -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_UK_Invalid_Country() throws Exception {
        CountryBusinessSize.isSmallBusiness("", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSmallBusiness_UK_Invalid_null_Country() throws Exception {
        CountryBusinessSize.isSmallBusiness("null", 0);
    }
}