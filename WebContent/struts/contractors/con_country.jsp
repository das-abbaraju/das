<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="getStateList(countryString).size() > 0">
	<label>
		<s:if test="countryString == 'CA'">
			<s:text name="ContractorAccount.province"/>:
		</s:if>
		<s:else>
			<s:text name="ContractorAccount.state"/>:
		</s:else>
	</label>
	<s:select list="getStateList(countryString)" id="state_sel" name="%{statePrefix + (needsSuffix ? '.isoCode' : '')}"  
		listKey="isoCode" listValue="simpleName" value="stateString" headerKey="" headerValue="- %{countryString == 'CA' ? getText('ContractorAccount.province') : getText('ContractorAccount.state')} -"/>
	<s:if test="stateString.length() < 1" >
		<span class="redMain" id="state_req">*</span>
	</s:if>
	<pics:fieldhelp title="State or Province">
		<s:text name="ContractorAccount.state.isoCode.fieldhelp"/>
	</pics:fieldhelp>
</s:if>