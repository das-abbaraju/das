package com.picsauditing.report.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.AutocompleteType;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.tables.FieldCategory;

@Target(METHOD)
@Retention(RUNTIME)
public @interface ReportField {
	FilterType filterType() default FilterType.String;

	AutocompleteType autocomplete() default AutocompleteType.None;
	
	int width() default 0;

	boolean visible() default true;

	boolean filterable() default true;

	boolean sortable() default true;

	String sql() default "";

	String url() default "";

	String i18nKeyPrefix() default "";

	String i18nKeySuffix() default "";

	FieldCategory category() default FieldCategory.General;

	OpPerms[] requiredPermissions() default {};
}
