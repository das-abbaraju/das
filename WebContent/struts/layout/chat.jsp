<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<span class="chat">
    <a href="https://chat.picsorganizer.com/client.php?locale=<s:text name="Mibew.LanguageCode" />&amp;style=PICS" target="_blank" onclick="if(navigator.userAgent.toLowerCase().indexOf('opera') != -1 &amp;&amp; window.event.preventDefault) window.event.preventDefault();this.newWindow = window.open('https://chat.picsorganizer.com/client.php?locale=<s:text name="Mibew.LanguageCode" />&amp;style=PICS&amp;url='+escape(document.location.href)+'&amp;referrer='+escape(document.referrer), 'webim', 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,width=640,height=480,resizable=1');this.newWindow.focus();this.newWindow.opener=window;return false;"><s:text name="Header.Chat" /></a>
</span>