<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%@ page import="com.picsauditing.PICS.I18nCache" %>
<%@ page import="com.picsauditing.PICS.MainPage" %>
<%@ page import="com.picsauditing.PICS.MainPage.SystemMessage" %>
<%@ page import="com.picsauditing.util.Strings" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
	I18nCache i18nCache = I18nCache.getInstance();
	Locale userLocale = TranslationActionSupport.getLocaleStatic();
	MainPage mainPage = new MainPage(request, session);
	List<SystemMessage> systemMessages = mainPage.getSystemMessages();
	
	if (!systemMessages.isEmpty()) {
		int position = 1;
%>
<div id="systemMessage">
<%		for (SystemMessage systemMessage : systemMessages) {
			String localeLanguage = systemMessage.getLocale().getLanguage(); 
			String displayValue = "inline"; %>
	<div class="system-message-container <%=localeLanguage%>">
		<%	if (!Strings.isEmpty(systemMessage.getLink())) {
				displayValue = "none"; %>
		<a href="javascript:;" class="system-message-locale" data-locale="<%=localeLanguage%>">
			<%=i18nCache.getText("SYSTEM.message." + localeLanguage, userLocale) %>
		</a>
		<%	} %>
		<span class="system-message-value" data-locale="<%=localeLanguage%>" style="display: <%=displayValue %>">
			<%=systemMessage.getValue()%>
		</span>
		<%=(position < systemMessages.size() ? "|" : "") %>
	</div>
<%			position++;
		} %>
	<div class="clear"></div>
</div>
<%	} %>