<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<h1><s:if test="permissions.loggedIn"><s:property value="contractor.name"/></s:if>
<span class="sub">
	<s:property value="subHeading" />
</span></h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<s:if test="permissions.loggedIn">
		<li><a id="conEditLink" href="ContractorEdit.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('edit')">class="current"</s:if>>1- Edit Details</a></li>
		<li><a id="conFacilitiesLink" href="ContractorFacilities.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('contractor_facilities')">class="current"</s:if>>2- Add Facilities</a></li>
		<li><a id="conPaymentLink" href="ContractorPaymentOptions.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('payment')">class="current"</s:if>>3- Add Payment Options</a></li>
		<li><a id="conConfirmLink" href="contractor_new_confirm.jsp"
			<s:if test="requestURI.contains('confirm')">class="current"</s:if>>4- Finish</a></li>
		<li><a id="conLogoutLink" href="Login.action?button=logout"
			<s:if test="requestURI.contains('login')">class="current"</s:if>>5- Logout</a></li>
	</s:if>
	<s:else>
		<li><a id="conRegisterLink" href="ContractorRegistration.action"
			<s:if test="requestURI.contains('con_registration')">class="current"</s:if>>1- Register</a></li>
		<li><a class="inactive">2- Add Facilities</a></li>
		<li><a class="inactive">3- Add Payment Options</a></li>
		<li><a class="inactive">4- Finish</a></li>
	</s:else>
</ul>
</div>
<s:include value="../actionMessages.jsp" />
