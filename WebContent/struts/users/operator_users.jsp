<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<label>
	<s:text name="ContractorRegistrationRequest.requestedByUser" />:
</label>

<s:select list="getUsersList(opID)" listKey="id" listValue="name" id="requestedUser"
	name="newContractor.requestedByUser" value="%{newContractor.requestedByUser.id}"
	headerKey="0" headerValue="- %{getText('RequestNewContractor.Other')} -" />
	
<s:textfield name="newContractor.requestedByUserOther" id="requestedOther" size="20" />

<div class="fieldhelp">
	<h3><s:text name="ContractorRegistrationRequest.requestedByUser" /></h3>
	<s:text name="ContractorRegistrationRequest.requestedByUser.fieldhelp" />
</div>