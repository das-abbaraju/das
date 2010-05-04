<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<div id="search">

<s:form id="form1" method="post"
	cssStyle="background-color: #F4F4F4;"
	onsubmit="runSearch('form1')">
	<s:hidden name="showPage" value="1" />
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />

	<div style="text-align: center; width: 100%">
	<div class="buttons"><a href="#" class="picsbutton positive"
		onclick="$('#form1').submit(); return false;">Search</a></div>
	</div>
	<br clear="all" />

	<div class="filterOption"><a href="#"
		onclick="toggleBox('auditorId'); return false;">Safety Professionals</a> = <span
		id="auditorId_query">ALL</span><br />
	<span id="auditorId_select" style="display: none" class="clearLink">
	<s:action name="AuditorsGet" executeResult="true">
		<s:param name="controlName" value="%{'auditorId'}" />
		<s:param name="presetValue" value="auditorId" />
	</s:action> 
	<script type="text/javascript">updateQuery('auditorId');</script> <br />
	<a class="clearLink" href="#"
		onclick="clearSelected('auditorId'); return false;">Clear</a> </span></div>

	<div class="filterOption"><a href="#"
		onclick="toggleBox('form1_auditTypeID'); return false;">Audit Type</a> = <span 
		id="form1_auditTypeID_query">ALL</span><br />
	<span id="form1_auditTypeID_select" style="display: none" class="clearLink">
	<s:select list="auditTypeList"
		cssClass="forms" name="auditTypeID" listKey="id"
		listValue="auditName" multiple="true" size="5" /> 
		<script	type="text/javascript">updateQuery('form1_auditTypeID');</script> <br />
	<a class="clearLink" href="#"
		onclick="clearSelected('form1_auditTypeID'); return false;">Clear</a>
	</span></div>
	
	<br clear="all" />
</s:form></div>

