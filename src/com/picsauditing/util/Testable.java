package com.picsauditing.util;

/**
 * Marks that a method has been changed from "private" scope to "default package"
 * scope for the purpose of Unit testing.
 * 
 * This has been deprecated since the introduction of Mockito and PowerMock.
 */
@Deprecated
public @interface Testable {
}
