<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set var="client_site_list_position" value="%{'middle'}" />
<s:set var="client_site_list" value="generalContractorClientSites" />

<s:if test="generalContractor != null">
	<s:set name="generalContractorID" value="%{generalContractor.id}" />
</s:if>

<div class="alert-message">
	<s:if test="generalContractor.generalContractor">
		<s:text name="RegistrationAddClientSite.GeneralContractorsHelp">
			<s:param value="%{generalContractor.doContractorsPay == 'Yes' ? 1 : 0}" />
			<s:param value="%{generalContractor.name}" />
		</s:text>
	</s:if>
	<s:else>
		<s:text name="RegistrationAddClientSite.OperatorUsingGCHelp">
			<s:param value="%{generalContractor.name}" />
		</s:text>
	</s:else>
</div>

<s:include value="/struts/contractors/_registrationAddClientSiteList.jsp" />