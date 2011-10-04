<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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
	
<s:set name="chat_icon" value="%{protocol + 
	'://server.iad.liveperson.net/hc/90511184/' + 
	'?cmd=repstate' +
	'&amp;site=90511184' +
	'&amp;channel=web' +
	'&amp;ver=1' +
	'&amp;imageUrl=' + protocol + '://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/' + locale.getDisplayLanguage() + '/General/3a'}" />

<s:set name="chat_url" value="%{protocol +
	'://server.iad.liveperson.net/hc/90511184/' + 
	'?cmd=file' + 
	'&amp;file=visitorWantsToChat' +
	'&amp;site=90511184' + 
	'&amp;imageUrl=' + protocol + '://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' + 
	'&amp;referrer='}" />

<span class="chat">
	<a class="live-chat" href="javascript:;" target="chat90511184" onClick="lpButtonCTTUrl = '${chat_url}' + escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;">
		<img src="images/chat-icon.png" /><span class="link"><s:text name="Header.Chat" /></span>
	</a>
</span>