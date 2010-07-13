<%@ taglib prefix="s" uri="/struts-tags"%>

<%@page import="com.picsauditing.util.URLUtils"%><html>
<head>
<title>Contact Us</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />

</head>
<body>
<form action="Contact">
	<fieldset class="form">
	<h2 class="formLegend">PICS Info</h2>
	<ol>
		<li><label>Mailing Address: </label> 
				P.O. Box 51387, Irvine, CA 92619-1387</li>
		<li><label>Office Location: </label>
				17701 Cowan Suite 140, Irvine, CA 92614
		</li>
		<li><label>Phone:</label>949.387.1940</li>
		<li><label>Toll Free:</label>800.506.PICS (7427)</li>
		<li><label>Fax:</label>949.269.9177</li>
		<li><label>Email:</label>info@picsauditing.com</li>
	</ol>
	</fieldset>
	<s:if test="permissions.loggedIn && !permissions.picsEmployee">
		<fieldset class="form">
			<s:if test="permissions.contractor">
				<h2 class="formLegend">Customer Service</h2>
				<ol>
					<li><label>Name:</label><s:property value="contractorAccount.auditor.name"/></li>
					<li><label>Phone:</label><s:property value="contractorAccount.auditor.phone"/></li>
					<li><label>Fax:</label><s:property value="contractorAccount.auditor.fax"/></li>
					<li><label>Email:</label><s:property value="contractorAccount.auditor.email"/></li>
				</ol>
			</s:if>
			<s:elseif test="permissions.operatorCorporate">
				<h2 class="formLegend">Account Representative</h2>
				<ol>
					<li><label>Name:</label><s:property value="accountRep.user.name"/></li>
					<li><label>Phone:</label><s:property value="accountRep.user.phone"/></li>
					<li><label>Fax:</label><s:property value="accountRep.user.fax"/></li>
					<li><label>Email:</label><s:property value="accountRep.user.email"/></li>
				</ol>
			</s:elseif>	
		</fieldset>
	</s:if>
		<fieldset class="form">
			<h2 class="formLegend">Support Links</h2>
				<ol>
					<li><label>Help Center:</label>
						<s:if test="permissions.loggedIn && !permissions.picsEmployee">
							<s:if test="permissions.contractor">
								<a href="http://help.picsauditing.com/wiki/User_Manual_for_Contractors"> Help Center</a>
							</s:if>
							<s:elseif test="permissions.operatorCorporate">
								<a href="http://help.picsauditing.com/wiki/User_Manual_for_Operators"> Help Center</a>
							</s:elseif>
						</s:if>
						<s:else>
							<a href="http://help.picsauditing.com/wiki/Help_Center"> Help Center</a>
						</s:else>
					<li><label>Online Chat:</label>
					<a id="_lpChatBtn"
						href='<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;byhref=1&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a' 
						target='chat90511184'
						onClick="lpButtonCTTUrl = '<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hc/90511184/?cmd=file&amp;file=visitorWantsToChat&amp;site=90511184&amp;imageUrl=<%= URLUtils.getProtocol( request ) %>://server.iad.liveperson.net/hcp/Gallery/ChatButton-Gallery/English/General/3a&amp;referrer='+escape(document.location); lpButtonCTTUrl = (typeof(lpAppendVisitorCookies) != 'undefined' ? lpAppendVisitorCookies(lpButtonCTTUrl) : lpButtonCTTUrl); window.open(lpButtonCTTUrl,'chat90511184','width=475,height=400,resizable=yes');return false;" ><span>Chat</span></a>
					</li>
				</ol>
		</fieldset>
</form>	
</body>
</html>
