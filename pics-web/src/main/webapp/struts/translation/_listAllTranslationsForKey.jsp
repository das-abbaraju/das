<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>
<%@ page import="com.picsauditing.service.i18n.TranslateUI" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="http://picsauditing.com/taglibs" %>

<s:set var="translation_key">${param.translation_key}</s:set>

<pics:toggle name="<%=FeatureToggle.TOGGLE_USE_TRANSLATION_SERVICE_ADAPTER%>">
    <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>${translation_key}" target="_BLANK">
        <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
    </a></td>
</pics:toggle>
<pics:toggleElse>
    <pics:toggle name="<%=FeatureToggle.TOGGLE_USE_NEW_TRANSLATIONS_DATASOURCE%>">
        <td><a class="edit translate" href="<%=TranslateUI.SHOW_TRANSLATION_URL%>${translation_key}" target="_BLANK">
            <%=TranslateUI.SHOW_TRANSLATION_URL_LINK_TEXT%>
        </a></td>
    </pics:toggle>
    <pics:toggleElse>
        <s:set var="include_locale_static">${param.include_locale_static}</s:set>
        <s:if test="#include_locale_static != 'true' && #include_locale_static != 'false'">
            <s:set var="include_locale_static">true</s:set>
        </s:if>

        <section class="translation-list-summary">
        <h1>Translations</h1>

        <ul>

        <s:iterator value="findAllTranslations(#translation_key, #include_locale_static)" var="translation">
            <li>
                <a href="ManageTranslations.action?key=${translation_key}&localeFrom=${user.locale}&localeTo=<s:property value="#translation.key.language" />">
                    <s:property value="#translation.key.getDisplayName(getUser().getLocale())" />: <s:property value="#translation.value" />
                </a>
            </li>
        </s:iterator>
        </ul>
        </section>
    </pics:toggleElse>
</pics:toggleElse>
