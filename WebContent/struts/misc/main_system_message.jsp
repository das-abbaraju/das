<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%@ page import="com.picsauditing.PICS.I18nCache" %>
<%@ page import="com.picsauditing.PICS.MainPage" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	I18nCache i18nCache = I18nCache.getInstance();
	Locale userLocale = TranslationActionSupport.getLocaleStatic();
	MainPage mainPage = new MainPage(request, session);
	Map<Locale, String> systemMessages = mainPage.getSystemMessages();
	
	if (!systemMessages.isEmpty()) {
		int position = 1;
%>
<div id="systemMessage">
<%		for (Locale system_message_locale : systemMessages.keySet()) { %>
	<div class="system-message-container <%=system_message_locale.getLanguage()%>">
		<a href="javascript:;" class="system-message-locale" data-locale="<%=system_message_locale.getLanguage()%>">
			<%=i18nCache.getText("SYSTEM.message." + system_message_locale.getLanguage(), userLocale) %>
		</a>
		<span class="system-message-value" data-locale="<%=system_message_locale.getLanguage()%>">
			<%=systemMessages.get(system_message_locale)%>
		</span>
		<%=(position < systemMessages.size() ? "|" : "") %>
	</div>
<%			position++;
		} %>
	<div class="clear"></div>
</div>
<%	} %>