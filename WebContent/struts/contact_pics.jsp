<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.picsauditing.util.URLUtils"%>
<%@ page import="com.picsauditing.actions.TranslationActionSupport" %>
<%
	Locale locale = TranslationActionSupport.getLocaleStatic();
%>
<head>
<title><s:text name="Contact.title" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
<form action="Contact">
	<fieldset class="form">
	<h2 class="formLegend"><s:text name="Contact.PICSinfo" /></h2>
	<ol>
		<li><label><s:text name="Contact.MailingAddress" />: </label> 
				P.O. Box 51387, Irvine, CA 92619-1387</li>
		<li><label><s:text name ="Contact.OfficeLocation" />: </label>
				17701 Cowan Suite 140, Irvine, CA 92614
		</li>
		<li><label><s:text name="User.phone" />:</label><s:text name="PicsMainPhone" /></li>
		<li><label><s:text name="Contact.tollfree" />:</label><s:text name="PicsTollFreePhone" /></li>
		<li><label><s:text name="User.fax" />:</label><s:text name="PicsFax" /></li>
		<li><label><s:text name="User.email" />:</label>info@picsauditing.com</li>
	</ol>
	</fieldset>
	<s:if test="permissions.loggedIn && !permissions.picsEmployee">
		<fieldset class="form">
			<s:if test="permissions.contractor">
				<h2 class="formLegend"><s:text name="Report.Category.CSR" /></h2>
				<ol>
					<li><label><s:text name="User.name" />:</label><s:property value="contractorAccount.auditor.name"/></li>
					<li><label><s:text name="User.phone" />:</label><s:property value="contractorAccount.auditor.phone"/></li>
					<li><label><s:text name="User.fax" />:</label><s:property value="contractorAccount.auditor.fax"/></li>
					<li><label><s:text name="User.email" />:</label><s:property value="contractorAccount.auditor.email"/></li>
				</ol>
			</s:if>
			<s:elseif test="permissions.operatorCorporate">
				<h2 class="formLegend"><s:text name="Contact.AccountRep" /></h2>
				<ol>
					<li><label><s:text name="User.name" />:</label><s:property value="accountRepUser.name"/></li>
					<li><label><s:text name="User.phone" />:</label><s:property value="accountRepUser.phone"/></li>
					<li><label><s:text name="User.fax" />:</label><s:property value="accountRepUser.fax"/></li>
					<li><label><s:text name="User.email" />:</label><s:property value="accountRepUser.email"/></li>
				</ol>
			</s:elseif>	
		</fieldset>
	</s:if>
		<fieldset class="form bottom">
			<h2 class="formLegend"><s:text name="Contact.links" /></h2>
				<ol>
					<li>
						<label><s:text name="Header.HelpCenter" />:</label>
						<a href="<s:property value='helpURL' />" target="_BLANK">
							<s:text name="Header.HelpCenter" />
						</a>
					</li>
						<pics:toggle name="LiveAgent">
							<li><label><s:text name="Header.Chat" />:</label>
								<script type="text/javascript" id="la_x2s6df8d" src="//chat.picsorganizer.com/scripts/trackjs.php"></script>
								<img src="//chat.picsorganizer.com/scripts/pix.gif" onLoad="LiveAgentTracker.createButton('<s:text name="LiveAgent.ButtonCode"/>', this);"/>
							</li>
 						</pics:toggle>
						<pics:toggleElse>
							<s:if test="liveChatEnabled">
								<li>
									<label><s:text name="Header.Chat" />:</label>
									<a id="_lpChatBtn"
										href="<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a" 
										target='chat90511184'
										onClick="lpButtonCTTUrl = '<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/<%=locale.getDisplayLanguage() %>/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" >
										<span><s:text name="Header.Chat" /></span>
									</a>
								</li>
							</s:if>
					</pics:toggleElse>
				</ol>
		</fieldset>
</form>	
</div>
