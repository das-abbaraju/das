<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%-- set variables from parameter includes --%>
<s:set var="step_current">${param.step_current}</s:set>
<s:set var="step_last">${param.step_last}</s:set>

<%-- set correct enable/disable/current steps based on current step + furthest step --%>
<s:if test="#step_current == 3">
	<s:set var="step_1">enabled</s:set>
	<s:set var="step_2">enabled</s:set>
	<s:set var="step_3">current</s:set>
</s:if>
<s:elseif test="#step_current == 2">
	<s:set var="step_1">enabled</s:set>
	<s:set var="step_2">current</s:set>
	
	<s:if test="#step_last == 3">
		<s:set var="step_3">enabled</s:set>
	</s:if>
	<s:else>
		<s:set var="step_3"></s:set>
	</s:else>
</s:elseif>
<s:else>
	<s:set var="step_1">current</s:set>
	
	<s:if test="#step_last == 3">
		<s:set var="step_2">enabled</s:set>
		<s:set var="step_3">enabled</s:set>
	</s:if>
	<s:elseif test="#step_last == 2">
		<s:set var="step_2">enabled</s:set>
		<s:set var="step_3"></s:set>
	</s:elseif>
	<s:else>
		<s:set var="step_2"></s:set>
		<s:set var="step_3"></s:set>
	</s:else>
</s:else>

<%-- display registration steps (only allow links to enabled steps) --%>
<div class="registration-step">
	<ul>
		<li class="${step_1}">
			<s:if test="#step_1 == 'enabled'">
				<s:a action="RegistrationAddClientSite"><s:text name="Number.1"/></s:a>
			</s:if>
			<s:else>
				<span><s:text name="Number.1"/></span>
			</s:else>
			
			<s:text name="RegistrationAddClientSite.AddClientSites"/>
		</li>
		<li class="${step_2}">
			<s:if test="#step_2 == 'enabled'">
				<s:a action="RegistrationServiceEvaluation"><s:text name="Number.2"/></s:a>
			</s:if>
			<s:else>
				<span><s:text name="Number.2"/></span>
			</s:else>
			
			<s:text name="Registration.ServiceEvaluation"/>
		</li>
		<li class="${step_3}">
			<s:if test="#step_3 == 'enabled'">
				<s:a action="RegistrationMakePayment"><s:text name="Number.3"/></s:a>
			</s:if>
			<s:else>
				<span><s:text name="Number.3"/></span>
			</s:else>
			
			<s:text name="Registration.Join"/>
		</li>
	</ul>
</div>