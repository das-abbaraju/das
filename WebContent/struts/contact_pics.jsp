<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<head>
	<link rel="stylesheet" type="text/css" href="js/jquery/facebox/facebox.css?v=${version}" media="screen"/>
	<title><s:text name="Contact.title"/></title>
</head>

<h1><s:text name="Contact.title"/></h1>

<div id="${actionName}_${methodName}_page" class="${actionName}-page page">
	<form action="INVALIDACTION">
		<fieldset class="form">
			<h2 class="formLegend"><s:text name="Contact.PICSinfo"/></h2>

			<ol>
				<li>
					<label><s:text name="Contact.MailingAddress"/>: </label>
					P.O. Box 51387, Irvine, CA 92619-1387
				</li>
				<li>
					<label><s:text name="Contact.OfficeLocation"/>: </label>
					17701 Cowan Suite 140, Irvine, CA 92614
				</li>
				<li>
					<label><s:text name="User.phone"/>:</label>
					${picsPhoneNumber}
				</li>
				<li>
					<label><s:text name="Contact.tollfree"/>:</label>
					${salesPhoneNumber}
				</li>
				<li>
					<label><s:text name="User.fax"/>:</label>
					${faxNumber}
				</li>
				<li>
					<label><s:text name="User.email"/>:</label>
					info@picsauditing.com
				</li>
			</ol>
		</fieldset>

		<s:if test="permissions.loggedIn && !permissions.picsEmployee">
			<fieldset class="form">
				<s:if test="permissions.contractor">
					<h2 class="formLegend"><s:text name="Report.Category.CSR"/></h2>

					<ol>
						<li>
							<label><s:text name="User.name"/>:</label>
								${contractorAccount.currentCsr.name}
						</li>
						<li>
							<label><s:text name="User.phone"/>:</label>
							<s:set var="contractor_current_csr_phone"
							       value="getLocalizedPhoneNumberForUser(contractorAccount.currentCsr, contractorAccount.country)"/>
								${contractor_current_csr_phone}
						</li>
						<li>
							<label><s:text name="User.fax"/>:</label>
								${contractorAccount.currentCsr.fax}
						</li>
						<li>
							<label><s:text name="User.email"/>:</label>
								${contractorAccount.currentCsr.email}
						</li>
					</ol>
				</s:if>
				<s:elseif test="permissions.operatorCorporate">
					<h2 class="formLegend"><s:text name="Contact.AccountRep"/></h2>

					<ol>
						<li>
							<label><s:text name="User.name"/>:</label>
								${accountRepUser.name}
						</li>
						<li>
							<label><s:text name="User.phone"/>:</label>
							<s:set var="contractor_current_manager_phone"
							       value="getLocalizedPhoneNumberForUser(accountRepUser)"/>
								${contractor_current_manager_phone}
						</li>
						<li>
							<label><s:text name="User.fax"/>:</label>
								${accountRepUser.fax}
						</li>
						<li>
							<label><s:text name="User.email"/>:</label>
								${accountRepUser.email}
						</li>
					</ol>
				</s:elseif>
			</fieldset>
		</s:if>

		<fieldset class="form bottom">
			<h2 class="formLegend"><s:text name="Contact.links"/></h2>

			<ol>
				<li>
					<label><s:text name="Header.HelpCenter"/>:</label>

					<a href="<s:property value='helpURL' />" target="_BLANK">
						<s:text name="Header.HelpCenter"/>
					</a>
				</li>

				<pics:toggle name="${MibewChatEnabled}">
					<s:set var="mibew_language_code" value="getText('Mibew.LanguageCode')"/>

					<s:url value="https://chat.picsorganizer.com/client.php" var="mibew_href_url">
						<s:param name="locale">${mibew_language_code}</s:param>
						<s:param name="style">PICS</s:param>
						<s:param name="name">${User.name}</s:param>
						<s:param name="email">${User.email}</s:param>
					</s:url>

					<s:url value="https://chat.picsorganizer.com/client.php" var="mibew_onclick_url">
						<s:param name="locale">${mibew_language_code}</s:param>
						<s:param name="style">PICS</s:param>
						<s:param name="name">${User.name}</s:param>
						<s:param name="email">${User.email}</s:param>
						<s:param name="url">${requestURL}</s:param>
						<s:param name="referrer">${referer}</s:param>
					</s:url>

					<li>
						<label><s:text name="Header.Chat"/>:</label>

						<a href="${mibew_href_url}"
						   target="_blank"
						   onclick="if(navigator.userAgent.toLowerCase().indexOf('opera') != -1 &amp;&amp; window.event.preventDefault) window.event.preventDefault();this.newWindow = window.open('${mibew_onclick_url}', 'webim', 'toolbar=0,scrollbars=0,location=0,status=1,menubar=0,width=640,height=480,resizable=1');this.newWindow.focus();this.newWindow.opener=window;return false;">
							<s:text name="Header.Chat"/>
						</a>
					</li>
				</pics:toggle>
				<pics:toggleElse>
					<s:if test="liveChatEnabled">
						<li>
							<label><s:text name="Header.Chat"/>:</label>

							<a id="_lpChatBtn"
							   href="https://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=https://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/${DisplayLanguage}/General/3a"
							   target='chat90511184'
							   onClick="lpButtonCTTUrl = 'https://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=https://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/${DisplayLanguage}/General/3a&amp;referer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;">
								<span><s:text name="Header.Chat"/></span>
							</a>
						</li>
					</s:if>
				</pics:toggleElse>
			</ol>
		</fieldset>
	</form>
</div>