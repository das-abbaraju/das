<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<li>
	<s:if test="permissions.operator">
		<label><s:text name="ContractorAccount.requestedBy" /></label>
		<s:textfield
			disabled="true"
			key="id"
			name="requestRelationship.operatorAccount"
			required="true"
		/>
	</s:if>
	<s:else>
		<s:select
			headerKey="0" 
			headerValue="RequestNewContractor.header.SelectAnOperator" 
			id="operator_list"
			label="ContractorAccount.requestedBy"
			list="operatorList"
			listKey="id"
			listValue="name" 
			name="requestRelationship.operatorAccount"
			required="true"
			theme="formhelp"
			value="%{requestRelationship.operatorAccount.id}"
		/>
	</s:else>
</li>
<li id="user_list">
	<s:include value="operator_users.jsp" />
</li>
<li>
	<s:textfield
		cssClass="datepicker"
		id="regDate"
		name="requestRelationship.deadline"
		required="true"
		size="10"
		theme="formhelp"
		value="%{requestRelationship.deadline != null ? getTextParameterized('short_dates', requestRelationship.deadline) : ''}"
	/>
</li>
<li>
	<s:textarea
		id="reasonForRegistration"
		name="requestRelationship.reasonForRegistration"
		required="true"
		theme="formhelp"
	/>
</li>