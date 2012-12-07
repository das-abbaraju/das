<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="com.picsauditing.toggle.FeatureToggle" %>

<%--
http://solutions.liveperson.com/tagGen/gallery/General3-Blue-fr.asp

Locales:

- English (e.g. https://base.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a)
- French
- German
- Hebrew
- Portuguese
- Spanish
--%>

<pics:toggle name="<%= FeatureToggle.TOGGLE_MIBEW_ON_REGISTRATION %>">
    <span class="chat">
        <a href="https://chat.picsorganizer.com/client.php?locale=<s:text name="Mibew.LanguageCode" />&amp;style=PICS" target="_blank" onclick="if(navigator.userAgent.toLowerCase().indexOf('opera') != -1 &amp;&amp; window.event.preventDefault) window.event.preventDefault();this.newWindow = window.open('https://chat.picsorganizer.com/client.php?locale=<s:text name="Mibew.LanguageCode" />&amp;style=PICS&amp;url='+escape(document.location.href)+'&amp;referrer='+escape(document.referrer), 'webim', 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,width=640,height=480,resizable=1');this.newWindow.focus();this.newWindow.opener=window;return false;"><s:text name="Header.Chat" /></a>
    </span>
</pics:toggle>
<pics:toggleElse>
    <s:set name="chat_url" value="%{chatUrl}"></s:set>

	<span class="chat">
		<a class="live-chat" href="javascript:;" target="chat90511184" onClick="lpButtonCTTUrl = '${chat_url}' + escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;">
	        <span class="link"><s:text name="Header.Chat" /></span>
		</a>
	</span>
</pics:toggleElse>