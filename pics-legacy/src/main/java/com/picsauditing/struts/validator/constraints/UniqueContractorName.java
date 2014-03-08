package com.picsauditing.struts.validator.constraints;


import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContractorNameNotTakenConstraintValidator.class)
public @interface UniqueContractorName {
    String message() default "";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
