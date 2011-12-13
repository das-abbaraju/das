<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="registration-header">
	<section>
		<s:include value="/struts/contractors/registrationStep.jsp">
			<s:param name="step_current" value="3" />
			<s:param name="step_last" value="getLastStepCompleted()" />
		</s:include>
	</section>
</div>

<s:if test="hasActionErrors()">
	<s:actionerror cssClass="action-error alert-message error" />
</s:if>

<div class="make-payment">
	<h1><s:text name="RegistrationMakePayment.Check" /></h1>
	<div><p class="info">You selected check. Click <s:a method="changePaymentToCC">Change Payment To Credit Card</s:a> to change your payment method.</p></div>
</div>