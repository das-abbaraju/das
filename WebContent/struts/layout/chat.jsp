<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<s:set var="mibew_language_code" value="getText('Mibew.LanguageCode')"/>

<s:url value="https://chat.picsorganizer.com/client.php" var="mibew_href">
    <s:param name="locale">${mibew_language_code}</s:param>
    <s:param name="style">PICS</s:param>
</s:url>

<span class="chat">
    <a class="chat-link" href="${mibew_href}" target="_blank"><s:text name="Header.Chat" /></a>
</span>