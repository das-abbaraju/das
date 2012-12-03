<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<label>
	<s:text name="ContractorRegistrationRequest.requestedByUser" />:
</label>

<s:select
	headerKey="0"
	headerValue="- %{getText('RequestNewContractor.Other')} -"
	id="requestedUser"
	list="getUsersList(opID)"
	listKey="id"
	listValue="name"
	name="newContractor.requestedByUser"
	value="%{newContractor.requestedByUser.id}"
/>

<s:textfield
	id="requestedOther"
	name="newContractor.requestedByUserOther"
	size="20"
/>

<div class="fieldhelp">
	<h3>
		<s:text name="ContractorRegistrationRequest.requestedByUser" />
	</h3>
	<s:text name="ContractorRegistrationRequest.requestedByUser.fieldhelp" />
</div>