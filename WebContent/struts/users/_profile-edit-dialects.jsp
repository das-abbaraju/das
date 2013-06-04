<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="supportedLanguages.getDialectCountriesBasedOn(language).size() > 0">
	<label for="dialect_selection">
		<s:text name="Registration.dialect" />
	</label>

	<select name="dialect" id="dialect_selection">
		<option value="">
			- <s:text name="Registration.dialectSelection" /> -
		</option>

		<s:iterator value="supportedLanguages.getDialectCountriesBasedOn(language)" var="language_dialect">
			<s:set var="is_language_dialect_selected" value="%{#language_dialect.isoCode.equals(dialect) ? 'selected=\"selected\"' : ''}" />

			<option value="${language_dialect.isoCode}" ${is_language_dialect_selected}>
				<s:text name="%{#language_dialect.i18nKey}" />
			</option>
		</s:iterator>
	</select>

	<s:fielderror fieldName="dialect" id="Registration_language_dialect_error" />
</s:if>