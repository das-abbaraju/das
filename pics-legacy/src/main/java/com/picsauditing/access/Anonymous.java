package com.picsauditing.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this to any Action class method that do NOT require a logged in user. By default, users must be logged in.
 * See also SecurityAware and com.picsauditing.interceptors.SecurityInterceptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Anonymous {
}
