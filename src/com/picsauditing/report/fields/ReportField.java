package com.picsauditing.report.fields;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.tables.FieldImportance;

@Target(METHOD)
@Retention(RUNTIME)
public @interface ReportField {

	FieldType type() default FieldType.String;

	int width() default Column.DEFAULT_WIDTH;

	boolean visible() default true;

	boolean filterable() default true;

	boolean sortable() default true;

	String sql() default "";

	String url() default "";

	String i18nKeyPrefix() default "";

	String i18nKeySuffix() default "";

	OpPerms requiredPermissions() default OpPerms.None;

	FieldImportance importance() default FieldImportance.Low;
}
