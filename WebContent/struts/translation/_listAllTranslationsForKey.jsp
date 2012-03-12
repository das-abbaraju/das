<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="translation_key">${param.translation_key}</s:set>
<s:set var="include_locale_static">${param.include_locale_static}</s:set>

<s:if test="#include_locale_static != 'true' && #include_locale_static != 'false'">
    <s:set var="include_locale_static">true</s:set>
</s:if>

<section class="translation-list-summary">
    <h1>Translations</h1>
    
    <ul>
        <s:iterator value="findAllTranslations(#translation_key, #include_locale_static)" var="translation">
            <li>
                <s:property value="#translation.key" />: <s:property value="#translation.value" />
            </li>
        </s:iterator>
    </ul>
</section>