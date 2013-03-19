<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="supportedLanguages.getDialectCountriesBasedOn(language).size() > 0">
    <label for="dialect_selection" data-content="Help text for dialect">
        <s:text name="Registration.dialect" />
    </label>

    <!-- FIXME this inline style is temporary and should be removed and replaced by Jason Roos -->
    <select name="dialect" id="dialect_selection" style="float: left;">
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
    <s:fielderror fieldName="contractor.dialect" />
</s:if>