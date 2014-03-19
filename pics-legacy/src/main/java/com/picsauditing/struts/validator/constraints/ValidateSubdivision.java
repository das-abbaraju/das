package com.picsauditing.struts.validator.constraints;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountrySubdivisionValidation.class)
public @interface ValidateSubdivision {
    String message() default "";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
