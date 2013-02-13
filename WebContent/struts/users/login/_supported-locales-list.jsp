<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:select
    label=""
    list="supportedLanguages.stableLanguagesSansDialect"
    listKey="key"
    listValue="value"
    id="supported_locales"
    name="request_locale"
    tabindex="1"
    value="locale"
/>