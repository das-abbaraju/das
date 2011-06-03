package com.picsauditing.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level annotation to specific overrides for the values to be indexed
 * 
 * <pre>
 * <bold>Example:</bold><code>
 * &#064;IndexableOverride(overrides = { &#064;IndexOverrideType(methodName = "getId", weight = 4) })
 * </code>
 * </pre>
 * 
 * @author David Tomberlin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IndexableOverride {

	IndexOverrideWeight[] overrides() default {};

	IndexOverrideIgnore[] ignores() default {};
}
