package com.picsauditing.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used on getter methods to say that a value needs to be
 * indexed Uses an <code>IndexValueType</code> to specific how to treat the
 * value and <code>weight</code> to give a weight to the value
 * 
 * @see IndexValueType
 * @author David Tomberlin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IndexableField {
	IndexValueType type();

	int weight();
}
