<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:select
	headerKey="0"
	headerValue="RequestNewContractor.Other"
	id="requesting_user"
	label="ContractorOperator.requestedBy"
	list="operatorUsers"
	listKey="id"
	listValue="name"
	name="requestRelationship.requestedBy"
	required="true"
	theme="formhelp"
	value="%{requestRelationship.requestedBy == null ? permissions.userId : requestRelationship.requestedBy.id}"
/>

<s:set var="placeholder_other">
	- <s:text name="RequestNewContractor.Other" /> -
</s:set>

<s:textfield
	name="requestRelationship.requestedByOther"
	id="requesting_other"
	size="20"
	placeholder="${placeholder_other}"
/>