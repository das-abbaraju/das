<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="supportedLanguages.getDialectCountriesBasedOn(language).size() > 0">
    <div id="dialect_container">
        <label for="dialect_selection" data-content="Help text for dialect">
            Dialect
        </label>

        <select name="dialect" id="dialect_selection">
            <option value="">
                - Please select a dialect -
            </option>
            <s:iterator value="supportedLanguages.getDialectCountriesBasedOn(language)" var="language_dialect">
                <option value="${language_dialect.isoCode}"<s:if test="#language_dialect.isoCode.equals(dialect)"> selected="selected"</s:if>>
                    <s:text name="%{#language_dialect.i18nKey}" />
                </option>
            </s:iterator>
        </select>
    </div>
</s:if>