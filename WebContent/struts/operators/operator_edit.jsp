<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="/exception_handler.jsp"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#opCountry').val(), stateString: '<s:property value="operator.state.isoCode"/>'});
}

function countryChanged(country) {
	// hide taxID and zip code
	if (country == 'AE') {
		$('#zip_li').hide();
	} else {
		$('#zip_li').show();
	}
	changeState(country);
}

$(function() {
	changeState($("#opCountry").val());
	$('.datepicker').datepicker();
});

</script>
</head>
<body>

<s:if test="operator == null">
	<h1>Create New <s:property value="type" /> Account</h1>
</s:if>
<s:else>
	<s:include value="opHeader.jsp"></s:include>
</s:else>

<s:if test="permissions.admin">
	<s:if test="id > 0 && operator.visibleAudits.size == 0">
		<div class="alert">This operator doesn't have access to any	audits. Please <a
			href="AuditOperator.action?oID=<s:property value="id"/>">double
		check the configuration</a>.</div>
	</s:if>
	<s:if test="id > 0 && operator.flagCriteriaInherited.size == 0">
		<div class="alert">This operator doesn't have any flag criteria
		defined. Please <a
			href="ManageFlagCriteriaOperator.action?id=<s:property value="id"/>">double
		check the configuration</a>.</div>
	</s:if>
</s:if>

<s:form id="save" method="POST" enctype="multipart/form-data">
	<div><input type="submit" class="picsbutton positive"
		name="button" value="Save" /></div>
	<br clear="all" />
	<s:hidden name="id" />
	<s:hidden name="type" />
	<fieldset class="form">
	<h2 class="formLegend">Details</h2>
	<ol>
		<li><label>Name:</label> <s:textfield name="operator.name"
			size="35" /></li>
		<s:if test="id > 0"><li><label>Primary Contact:</label> <s:select
			list="primaryOperatorContactUsers"
			name="contactID"
			listKey="id"
			listValue="name"
			headerKey=""
			headerValue="- Select a User -" 
			value="%{operator.primaryContact.id}"/>
			<s:if test="operator.primaryContact">
				<a href="UsersManage.action?accountId=<s:property value="operator.id"/>&user.id=<s:property value="operator.primaryContact.id"/>">View</a>
			</s:if>
			<s:else>
				<a class="add" href="UsersManage.action?button=newUser&accountId=<s:property value="operator.id"/>&user.isGroup=No&user.isActive=Yes">Add User</a>
			</s:else>
		</li>
		</s:if>
		<li><label>Industry:</label> <s:select list="industryList"
			name="operator.industry" listValue="description" /></li>
		<s:if test="permissions.admin && !permissions.hasPermission('UserRolePicsOperator')">
			<s:iterator value="accountManagers.keySet()" id="key">
				<li><label><s:property value="#key.description" /><s:if test="accountManagers.get(#key).size() > 1">s</s:if>:</label>
					<s:property value="@com.picsauditing.util.Strings@implode(accountManagers.get(#key), ', ')" /></li>
			</s:iterator>
		</s:if>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Primary Address</h2>
	<ol>
		<li><label>Address:</label>
			<s:textfield name="operator.address" size="35" /><br />
			<s:textfield name="operator.address2" size="35" />
		</li>
		<li><label>City:</label> <s:textfield name="operator.city"
			size="20" /></li>
		<li><label>Country:</label> <s:select 
			list="countryList"
			id="opCountry"
			name="country.isoCode" 
			listKey="isoCode"
			listValue="name" 
			headerKey="" headerValue="- Country -"
			value="operator.country.isoCode"
			onchange="countryChanged(this.value)" />
			<s:if test="permissions.admin && operator.operator">
				<div class="fieldhelp">
					<h3>Address</h3>
					<p>Please select the country that this operator works in. The contractor's audit 
						configuration is based it.</p> 
				</div>
			</s:if>
		</li>
		<li id="state_li"></li>
		<s:if test="operator.country.isoCode != 'AE'">
			<li id="zip_li"><label>Zip:</label>
				<s:textfield name="operator.zip" size="7" />
			</li>
		</s:if>
		<li><label>Main Phone:</label><s:textfield name="operator.phone" /></li>
		<li><label>Main Fax:</label><s:textfield name="operator.fax" /></li>
		<li><label>Web URL:</label> <s:textfield name="operator.webUrl"
			size="30" /></li>
	</ol>
	</fieldset>
	<fieldset class="form">
	<h2 class="formLegend">Company Identification</h2>
	<ol>
		<li><label>Description:</label> <s:textarea
			name="operator.description" cols="40" rows="15" /></li>
	</ol>
	</fieldset>
	<s:if test="permissions.admin">
		<fieldset class="form">
		<h2 class="formLegend">Admin Fields</h2>
		<ol>
			<li><label>Status:</label> 
				<s:select list="statusList" name="operator.status" /></li>
			<li><label>Reason:</label> <s:textarea name="operator.reason"
				rows="3" cols="25" /></li>
			<li><label>Required Tags:</label> <s:textfield
				name="operator.requiredTags" />
				<div class="fieldhelp">
					<h3>Required Tags</h3>
					<p>Example: 1,2,3|4,5 <a href="OperatorTags.action?id=<s:property value="id" />" target="_BLANK">Tags</a></p>
				</div>
			</li>
			<s:if test="operator.corporate">
				<li><label>Primary Corporate:</label> <s:checkbox
					name="operator.primaryCorporate"></s:checkbox></li>
			</s:if>
			<li><label>Approves Contractors:</label> <s:radio
				list="#{'Yes':'Yes','No':'No'}"
				name="operator.approvesRelationships" theme="pics" /></li>
			<li><label>Health &amp; Safety Organization:</label>
				<s:radio list="#{'OSHA':'OSHA','MSHA':'MSHA','COHS':'Canadian OHS'}"
					name="operator.oshaType" theme="pics" />
				<div class="fieldhelp">
					<h3>Health &amp; Safety Organization</h3>
					<p>The source of statistics that should be used to evaluate contractors</p>
				</div>
			</li>
			<s:if test="!operator.corporate">
				<li><label>Accepts Bid Only Contractor:</label> <s:checkbox
					name="operator.acceptsBids" /></li>
			</s:if>
			<li><label>InsureGUARD&trade;:</label> <s:radio
				list="#{'Yes':'Yes','No':'No'}" name="operator.canSeeInsurance"
				theme="pics" /></li>
			<li><label>Auto Approve/Reject Policies:</label> <s:checkbox
				name="operator.autoApproveInsurance" /></li>
			<li><label>Contractors pay:</label> <s:radio
				list="#{'Yes':'Yes','No':'No','Multiple':'Multiple'}"
				name="operator.doContractorsPay" theme="pics" /></li>
			<li><label>Requires OQ:</label> <s:checkbox
				name="operator.requiresOQ" /></li>
			<li><label>Requires Competency Review:</label> <s:checkbox
				name="operator.requiresCompetencyReview" /></li>
			<s:if test="type == 'Operator' || operator.operator">
				<li><label>Uses Onsite Service Contractors:</label>
					<s:checkbox name="operator.onsiteServices" /></li>
				<li><label>Uses Offsite Service Contractors:</label>
					<s:checkbox name="operator.offsiteServices" /></li>
				<li><label>Uses Material Suppliers:</label>
					<s:checkbox name="operator.materialSupplier" /></li>
			</s:if>
			<pics:permission perm="UserRolePicsOperator">
				<li id="act_li"><label>Activation Fee:</label>
					<s:textfield name="operator.activationFee" size="7" />
				</li>
			</pics:permission>
		</ol>
		</fieldset>
		<fieldset class="form">
		<h2 class="formLegend">Linked Accounts</h2>
		<ol>
			<s:if test="operator.corporate">
				<li><label>Facilities:</label> <s:select list="operatorList"
					listValue="name" listKey="id" name="facilities" multiple="7"
					size="15" /></li>
			</s:if>
			<s:if test="operator.operator">
				<s:if test="operator.corporateFacilities.size() > 0">
					<li><label>Parent Corporation / Division / Hub:</label> <s:select
						name="foreignKeys.parent" list="operator.corporateFacilities"
						listKey="corporate.id" listValue="corporate.name" headerKey="0"
						headerValue=" - Select a Parent Facility - "
						value="operator.parent.id" /> <a
						href="?id=<s:property value="operator.parent.id"/>">Go</a></li>
				</s:if>
				<li><label>Flag Criteria:</label> <s:select
					name="foreignKeys.inheritFlagCriteria"
					value="operator.inheritFlagCriteria.id" list="relatedFacilities"
					listKey="id" listValue="name"></s:select> <a
					href="?id=<s:property value="operator.inheritFlagCriteria.id"/>">Go</a></li>
				<li><label>Insurance Criteria:</label> <s:select
					name="foreignKeys.inheritInsuranceCriteria"
					value="operator.inheritInsuranceCriteria.id"
					list="relatedFacilities" listKey="id" listValue="name"></s:select>
				<a
					href="?id=<s:property value="operator.inheritInsuranceCriteria.id"/>">Go</a></li>
			</s:if>
			<s:if test="operator.corporateFacilities.size() > 0">
				<li><label>Corporate Facilities:</label>
					<ol>
					<s:iterator value="operator.corporateFacilities" id="facility">
						<li style="padding: 0 4px;"><a href="FacilitiesEdit.action?id=<s:property value="#facility.corporate.id"/>"><s:property value="#facility.corporate.name"/></a></li>
					</s:iterator>
					</ol>
				</li>
			</s:if>
		</ol>
		</fieldset>
		<s:if test="operator.id > 0">
			<pics:permission perm="UserRolePicsOperator" type="Edit">
				<fieldset class="form">
				<h2 class="formLegend">Manage Representatives</h2>
				<ol>
					<li><label>Sales Representatives:</label>
					<table class="report">
						<thead>
							<tr>
								<td>User</td>
								<td>Percent</td>
								<td>Start</td>
								<td>End</td>
								<td></td>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="operator.accountUsers" status="role">
								<s:hidden value="%{role}" name="accountRole" />
								<s:if test="role.description == 'Sales Representative' && current">
									<tr>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:property
											value="user.name" /></td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:property
											value="ownerPercent" />%</td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:date
											name="startDate" format="MM/dd/yyyy" /></td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:date
											name="endDate" format="MM/dd/yyyy" /></td>
										<td><a
											href="FacilitiesEdit.action?id=<s:property value="operator.id"/>&accountUserId=<s:property value="id"/>&button=Remove"
											class="remove">Remove</a></td>
									</tr>
									<tr id="show_<s:property value="id"/>" style="display: none;">
										<td colspan="4"><nobr><s:textfield
											name="operator.accountUsers[%{#role.index}].ownerPercent"
											value="%{ownerPercent}" size="3" />%&nbsp;&nbsp; <s:textfield
											cssClass="blueMain datepicker" size="10"
											name="operator.accountUsers[%{#role.index}].startDate"
											id="startDate[%{id}]"
											value="%{@com.picsauditing.PICS.DateBean@format(startDate, 'MM/dd/yyyy')}" />
										&nbsp;&nbsp;<s:textfield cssClass="blueMain datepicker"
											size="10"
											name="operator.accountUsers[%{#role.index}].endDate"
											id="endDate[%{id}]"
											value="%{@com.picsauditing.PICS.DateBean@format(endDate, 'MM/dd/yyyy')}" />
										</nobr></td>
										<td><input type="submit" class="picsbutton positive"
											name="button" value="Save Role" /></td>
									</tr>
								</s:if>
							</s:iterator>
							<tr>
								<td colspan="4"><s:select name="salesRep.user.id"
									list="userList" listKey="id" listValue="name" headerKey="0"
									headerValue="- Select a User -" /></td>
								<td><s:hidden value="PICSSalesRep" name="salesRep.role" /><input
									type="submit" class="picsbutton positive" name="button"
									value="Add Role" /></td>
							</tr>
						</tbody>
					</table>
					</li>

					<li><label>Account Managers: </label>
					<table class="report">
						<thead>
							<tr>
								<td>User</td>
								<td>Percent</td>
								<td>Start</td>
								<td>End</td>
								<td></td>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="operator.accountUsers" status="role">
								<s:if test="role.description == 'Account Manager' && current">
									<tr>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:property
											value="user.name" /></td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:property
											value="ownerPercent" />%</td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:date
											name="startDate" format="MM/dd/yyyy" /></td>
										<td onclick="$('#show_<s:property value="id"/>').show();"><s:date
											name="endDate" format="MM/dd/yyyy" /></td>
										<td><a
											href="FacilitiesEdit.action?id=<s:property value="operator.id"/>&accountUserId=<s:property value="id"/>&button=Remove"
											class="remove">Remove</a></td>
									</tr>
									<tr id="show_<s:property value="id"/>" style="display: none;">
										<td colspan="4"><nobr><s:textfield
											name="operator.accountUsers[%{#role.index}].ownerPercent"
											value="%{ownerPercent}" size="3" />%&nbsp;&nbsp; <s:textfield
											cssClass="blueMain datepicker" size="10"
											name="operator.accountUsers[%{#role.index}].startDate"
											id="startDate[%{id}]"
											value="%{@com.picsauditing.PICS.DateBean@format(startDate, 'MM/dd/yyyy')}" />
										&nbsp;&nbsp;<s:textfield cssClass="blueMain datepicker"
											size="10"
											name="operator.accountUsers[%{#role.index}].endDate"
											id="endDate[%{id}]"
											value="%{@com.picsauditing.PICS.DateBean@format(endDate, 'MM/dd/yyyy')}" />
										</nobr></td>
										<td><input type="submit" class="picsbutton positive"
											name="button" value="Save Role" /></td>
									</tr>
								</s:if>
							</s:iterator>
							<tr>
								<td colspan="4"><s:select name="accountRep.user.id"
									list="userList" listKey="id" listValue="name" headerKey="0"
									headerValue="- Select a User -" /></td>
								<td><s:hidden value="PICSAccountRep"
									name="accountRep.role" /><input type="submit"
									class="picsbutton positive" name="button" value="Add Role" /></td>
							</tr>
						</tbody>
					</table>
					</li>
					
					<s:if test="previousManagers.keySet().size() > 0">
						<li><label>Previous Representatives: </label>
						<table class="report">
							<thead>
								<tr>
									<td>User</td>
									<td>Role</td>
									<td>Percent</td>
									<td>Start</td>
									<td>End</td>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="previousManagers.keySet()" id="key">
									<s:iterator value="previousManagers.get(#key)">
										<tr>
											<td><s:property value="user.name" /></td>
											<td><s:property value="#key.description" /></td>
											<td><s:property value="ownerPercent" />%</td>
											<td><s:date name="startDate" format="MM/dd/yyyy" /></td>
											<td><s:date name="endDate" format="MM/dd/yyyy" /></td>
										</tr>
									</s:iterator>
								</s:iterator>
							</tbody>
						</table>
						</li>
					</s:if>
				</ol>
				</fieldset>
			</pics:permission>
		</s:if>
	</s:if>
	<fieldset class="form submit"><input type="submit" class="picsbutton positive"
		name="button" value="Save" /></fieldset>
</s:form>
</body>
</html>