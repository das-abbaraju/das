<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="supportedLanguages.getCountriesBasedOn(localeForm.language).size() > 0">
    <label for="dialect_selection" data-content="Help text for dialect">
        <s:text name="Registration.dialect" />
    </label>

    <select class="select2Min" name="localeForm.dialect" id="dialect_selection">
        <option value="">
            - <s:text name="Registration.dialectSelection" /> -
        </option>

        <s:iterator value="supportedLanguages.getCountriesBasedOn(language)" var="language_dialect">
            <s:set var="is_language_dialect_selected" value="%{#language_dialect.key.equals(dialect) ? 'selected=\"selected\"' : ''}" />

            <option value="${language_dialect.key}" ${is_language_dialect_selected}>
                ${language_dialect.value}
            </option>
        </s:iterator>
    </select>

    <s:fielderror fieldName="localeForm.dialect" id="Registration_language_dialect_error" />
</s:if>