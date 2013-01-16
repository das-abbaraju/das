<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<li>
	<s:textfield name="employee.firstName" label="Employee.firstName" theme="formhelp" />
</li>

<li>
	<s:textfield name="employee.lastName" label="Employee.lastName" theme="formhelp" />
</li>

<li>
	<s:textfield id="titleSuggest" name="employee.title" label="Employee.title" theme="formhelp" data-json="${previousTitlesJSON}" />
</li>

<li>
	<s:select
		name="employee.classification"
		label="Employee.classification"
		list="@com.picsauditing.jpa.entities.EmployeeClassification@values()"
		listValue="getText(getI18nKey('description'))"
		theme="formhelp" />
</li>

<li>
	<s:textfield
		name="employee.hireDate"
		label="Employee.hireDate"
		value="%{@com.picsauditing.util.PicsDateFormat@formatDateIsoOrBlank(employee.hireDate)}"
		cssClass="datepicker"
		theme="formhelp" />
	<s:property value="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
</li>

<li id="termDate">
	<s:textfield
		name="employee.fireDate"
		label="Employee.fireDate"
		value="%{@com.picsauditing.util.PicsDateFormat@formatDateIsoOrBlank(employee.fireDate)}"
		cssClass="datepicker"
		theme="formhelp" />
	<s:property value="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
</li>

<li>
	<s:textfield name="employee.email" label="Employee.email" theme="formhelp" />
</li>

<li>
	<s:textfield name="employee.phone" label="Employee.phone" theme="formhelp" />
</li>

<li>
	<s:textfield
		name="employee.twicExpiration"
		label="Employee.twicExpiration"
		value="%{@com.picsauditing.util.PicsDateFormat@formatDateIsoOrBlank(employee.twicExpiration)}"
		cssClass="datepicker"
		theme="formhelp" />
	<s:property value="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
</li>
