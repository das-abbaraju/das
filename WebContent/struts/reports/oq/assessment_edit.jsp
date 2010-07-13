<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<html>
<head>
<title><s:property value="center.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/menu1.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<s:include value="../../jquery.jsp" />
<script type="text/javascript">
function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#centerCountry').val(), stateString: '<s:property value="center.state.isoCode"/>'});
}

function countryChanged(country) {
	changeState(country);
}

$(function() {
	changeState($("#centerCountry").val());
	$('.datepicker').datepicker();
});

</script>
</head>
<body>

<s:include value="assessmentHeader.jsp" />

<s:form id="save" method="POST" enctype="multipart/form-data">
	<div><input type="submit" class="picsbutton positive" name="button" value="Save" /></div>
	<br clear="all" />
	<s:hidden name="id" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
			<fieldset class="form">
			<h2 class="formLegend">Details</h2>
				<ol>
					<li><label>Name:</label> <s:textfield name="center.name" size="35" /></li>
					<s:if test="id > 0">
						<li><label>Primary Contact:</label>
							<s:select list="users" name="contactID" listKey="id" listValue="name" 
								headerKey="" headerValue="- Select a User -"  
								value="%{center.primaryContact.id}"/>
							<a href="UsersManage.action?button=newUser&accountId=<s:property value="center.id"/>&isActive=<s:property value="isActive"/>&isGroup=<s:property value="isGroup"/>&user.isGroup=No&user.isActive=Yes">Add User</a>
						</li>
					</s:if>				
				</ol>
			</fieldset>
			<fieldset class="form">
				<h2 class="formLegend">Primary Address</h2>
				<ol>
					<li><label>Address:</label> <s:textfield name="center.address" size="35" /></li>
					<li><label>City:</label> <s:textfield name="center.city"
						size="20" /></li>
					<li><label>Country:</label>
						<s:select list="countryList" id="centerCountry" name="country.isoCode" 
						listKey="isoCode" listValue="name" headerKey="" headerValue="- Country -"
						value="locale.country" onchange="countryChanged(this.value)" /></li>
					<li id="state_li"></li>
					<li><label>Zip:</label> <s:textfield name="center.zip" size="7" /></li>
					<li><label>Main Phone:</label><s:textfield name="center.phone" /></li>
					<li><label>Main Fax:</label><s:textfield name="center.fax" /></li>
					<li><label>Web URL:</label> <s:textfield name="center.webUrl" size="30" /></li>
				</ol>
			</fieldset>
			<fieldset class="form">
			<h2 class="formLegend">Company Identification</h2>
			<ol>
				<li><label>Description:</label>
					<s:textarea name="center.description" cols="40" rows="15" /></li>
			</ol>
			</fieldset>
			</td>

			<s:if test="permissions.admin">
				<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form">
				<h2 class="formLegend">Admin Fields</h2>
				<ol>
					<li><label>Status:</label><s:select list="statusList" name="center.status" /></li>
					<li><label>Reason:</label> <s:textarea name="center.reason" rows="3" cols="25" /></li>
				</ol>
				</fieldset>
				</td>
			</s:if>
		</tr>
	</table>
	<br clear="all">
	<div><input type="submit" class="picsbutton positive"
		name="button" value="Save" /></div>
</s:form>
</body>
</html>