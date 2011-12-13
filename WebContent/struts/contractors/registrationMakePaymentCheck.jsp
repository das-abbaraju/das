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
	<div><p><s:text name="ContractorRegistrationFinish.FullAccessOnPayment"/></p>
		<s:form><s:submit method="changePaymentToCC" value="Change Payment Method To Credit Card"/></s:form></div>
	<h1><s:text name="RegistrationMakePayment.Check" /></h1>
	
	<div class="membership">
		<p class="process-check-info">
			<s:text name="ContractorRegistrationFinish.FullAccessOnPayment"/>
		</p>
	</div>
	
	<s:form><s:submit method="changePaymentToCC" value="Change Payment Method To Credit Card"/></s:form>
</div>