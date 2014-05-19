package com.picsauditing.employeeguard.services.calculator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class SkillStatusTest {

  @Test
  public void testIsExpired() throws Exception {
    SkillStatus skillStatus = SkillStatus.Expired;
    assertTrue("Expected skill status to be expired", skillStatus.isExpired());
  }

  @Test
  public void testIsExpiring() throws Exception {
    SkillStatus skillStatus = SkillStatus.Expiring;
    assertTrue("Expected skill status to be expiring", skillStatus.isExpiring());
  }

  @Test
  public void testIsPending() throws Exception {
    SkillStatus skillStatus = SkillStatus.Pending;
    assertTrue("Expected skill status to be pending", skillStatus.isPending());
  }

  @Test
  public void testIsCompleted() throws Exception {
    SkillStatus skillStatus = SkillStatus.Completed;
    assertTrue("Expected skill status to be completed", skillStatus.isCompleted());
  }

  @Test
  public void testLowestStatus_ExpiredOverridesExpiring() throws Exception {
    SkillStatus currentStatus = SkillStatus.Expiring;
    SkillStatus calculatedStatus = SkillStatus.Expired;
    assertTrue(calculatedStatus.compareTo(currentStatus)<0);
  }

  @Test
  public void testLowestStatus_ExpiringOverridesPending() throws Exception {
    SkillStatus currentStatus = SkillStatus.Pending;
    SkillStatus calculatedStatus = SkillStatus.Expiring;
    assertTrue(calculatedStatus.compareTo(currentStatus)<0);
  }

  @Test
  public void testLowestStatus_PendingOverridesComplete() throws Exception {
    SkillStatus currentStatus = SkillStatus.Completed;
    SkillStatus calculatedStatus = SkillStatus.Pending;
    assertTrue(calculatedStatus.compareTo(currentStatus)<0);
  }

  @Test
  public void testLowestStatus_ExpiringOverridesComplete() throws Exception {
    SkillStatus currentStatus = SkillStatus.Completed;
    SkillStatus calculatedStatus = SkillStatus.Expiring;
    assertTrue(calculatedStatus.compareTo(currentStatus)<0);
  }

  @Test
  public void testLowestStatus_ExpiredOverridesComplete() throws Exception {
    SkillStatus currentStatus = SkillStatus.Completed;
    SkillStatus calculatedStatus = SkillStatus.Expired;
    assertTrue(calculatedStatus.compareTo(currentStatus)<0);
  }

}//--  SkillStatusTest
