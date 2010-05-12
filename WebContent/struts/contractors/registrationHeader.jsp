<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<h1><s:if test="permissions.loggedIn">
	<s:property value="contractor.name" />
</s:if> <span class="sub"> <s:property value="subHeading" /> </span></h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<s:if test="permissions.loggedIn">
		<li><a id="edit_contractor" href="ContractorEdit.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('edit')">class="current"</s:if>>1) Edit Details</a>
		</li>
		<li><a id="conServicesLink" href="ContractorRegistrationServices.action?id=<s:property value="id" />"
			<s:if test="requestURI.contains('con_reg_services')">class="current"</s:if>>2) Services Performed</a></li>
		<s:if test="contractor.riskLevel != null">
			<li><a id="conFacilitiesLink" href="ContractorFacilities.action?id=<s:property value="id" />"
				<s:if test="requestURI.contains('contractor_facilities')">class="current"</s:if>>3) Add Facilities</a></li>
			<s:if test="contractor.operators.size > 0 && (contractor.requestedBy != null || currentOpertors == 1)">
				<li><a id="conPaymentLink" href="ContractorPaymentOptions.action?id=<s:property value="id" />"
					<s:if test="requestURI.contains('payment')">class="current"</s:if>>4) Add Payment Options</a></li>
			</s:if>
			<s:else>
				<li><a class="inactive">4) Add Payment Options</a></li>
			</s:else>
			<s:if test="(contractor.paymentMethodStatusValid || !contractor.mustPayB) && contractor.operators.size > 0">
				<li><a id="conConfirmLink" href="ContractorRegistrationFinish.action"
					<s:if test="requestURI.contains('finish')">class="current"</s:if>>5) Confirm</a></li>
			</s:if>
			<s:else>
				<li><a class="inactive">5) Confirm</a></li>
			</s:else>
		</s:if>
		<s:else>
			<li><a class="inactive">3) Add Facilities</a></li>
			<li><a class="inactive">4) Add Payment Options</a></li>
			<li><a class="inactive">5) Confirm</a></li>
		</s:else>
	</s:if>
	<s:else>
		<li><a id="conRegisterLink" href="ContractorRegistration.action"
			<s:if test="requestURI.contains('con_registration')">class="current"</s:if>>1) Register</a></li>
		<li><a class="inactive">2) Services Performed</a></li>
		<li><a class="inactive">3) Add Facilities</a></li>
		<li><a class="inactive">4) Add Payment Options</a></li>
		<li><a class="inactive">5) Confirm</a></li>
	</s:else>
</ul>
</div>

<s:include value="../actionMessages.jsp" />
