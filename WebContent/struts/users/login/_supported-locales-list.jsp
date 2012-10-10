<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="configEnvironment || i18nReady">
    <s:select
        label=""
        list="@com.picsauditing.actions.TranslationActionSupport@getSupportedLocales()"
        listKey="language"
        listValue="%{@com.picsauditing.util.Strings@capitalize(getDisplayLanguage(language))}"
        id="supported_locales"
        name="request_locale"
        tabindex="1"
        value="locale"
    />
</s:if>