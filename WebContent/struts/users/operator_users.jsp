<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:select
	headerKey="0"
	headerValue="RequestNewContractor.Other"
	id="requestedUser"
	label="ContractorOperator.requestedBy"
	list="operatorUsers"
	listKey="id"
	listValue="name"
	name="requestRelationship.requestedBy"
	required="true"
	theme="formhelp"
	value="%{requestRelationship.requestedByUser.id}"
/>
	
<s:textfield name="requestRelationship.requestedByOther" id="requestedOther" size="20" />