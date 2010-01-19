<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<%@page import="com.picsauditing.PICS.DateBean"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<s:include value="../jquery.jsp" />
<script type="text/javascript">
function changeState(country) {
	$('#state_li').load('StateListAjax.action',{countryString: $('#opCountry').val(), stateString: '<s:property value="operator.state.isoCode"/>'});
}

function countryChanged(country) {
	changeState(country);
}

$(function() {
	showPrimaryContactInfo(<s:property value="operator.primaryContact.id"/>);
	changeState($("#opCountry").val());
	$('.datepicker').datepicker();
});

function showPrimaryContactInfo(user) {
	$('#contact_info').load('ContactInfoAjax.action',{userid: user});
}
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
	<s:if test="id > 0 && operator.flagQuestionCriteriaInherited.size == 0">

		<div class="alert">This operator doesn't have any flag criteria
		defined. Please <a
			href="OperatorFlagCriteria.action?id=<s:property value="id"/>">double
		check the configuration</a>.</div>
	</s:if>
</s:if>

<s:form id="save" method="POST" enctype="multipart/form-data">
	<div><input type="submit" class="picsbutton positive"
		name="button" value="Save" /></div>
	<br clear="all" />
	<s:hidden name="id" />
	<s:hidden name="type" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
			<fieldset class="form"><legend><span>Details</span></legend>
			<ol>
				<li><label>Name:</label> <s:textfield name="operator.name"
					size="35" /></li>
				<li><label>Primary Contact:</label> <s:select
					list="primaryOperatorContactUsers"
					name="contactID"
					listKey="id"
					listValue="name"
					value="%{operator.primaryContact.id}"
					onchange="showPrimaryContactInfo(this.value)"/>
				</li>
				<li id="contact_info"></li>
				<li><label>Industry:</label> <s:select list="industryList"
					name="operator.industry" listValue="description" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Primary
			Address</span></legend>
			<ol>
				<li><label>Address:</label> <s:textfield
					name="operator.address" size="35" /></li>
				<li><label>City:</label> <s:textfield name="operator.city"
					size="20" /></li>
				<li><label>Country:</label> <s:select 
					list="countryList"
					id="opCountry"
					name="country.isoCode" 
					listKey="isoCode"
					listValue="name" 
					value="operator.country.isoCode"
					onchange="countryChanged(this.value)" /></li>
				<li id="state_li"></li>
				<li><label>Zip:</label> <s:textfield name="operator.zip"
					size="7" /></li>
				<li><label>Web URL:</label> <s:textfield name="operator.webUrl"
					size="30" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Company
			Identification</span></legend>
			<ol>
				<li><label>Description:</label> <s:textarea
					name="operator.description" cols="40" rows="15" /></li>
			</ol>
			</fieldset>
			<s:if test="permissions.admin">
				<fieldset class="form"><legend><span>Visible
				Audits</span></legend>
				<ol>
					<s:iterator value="operator.visibleAudits">
						<li><label><s:property value="auditType.auditName" />:</label>
						Risk >= <s:property value="minRiskLevel" />, Status >= <s:property
							value="requiredAuditStatus" />, Flag = <s:property
							value="requiredForFlag" /> <s:if test="canEdit">, Editable = Yes</s:if></li>
					</s:iterator>
				</ol>
				</fieldset>
			</s:if></td>

			<s:if test="permissions.admin">
				<td style="vertical-align: top; width: 50%; padding-left: 10px;">
				<fieldset class="form"><legend><span>Admin
				Fields</span></legend>
				<ol>
					<li><label>Visible?</label> <s:radio
						list="#{'Y':'Yes','N':'No'}" name="operator.active" theme="pics" /></li>
					<li><label>Reason:</label> <s:textarea name="operator.reason"
						rows="3" cols="25" /></li>
					<s:if test="operator.corporate">
						<li><label>Primary Corporate:</label> <s:checkbox
							name="operator.primaryCorporate"></s:checkbox></li>
					</s:if>
					<li><label>Receive contractor activation emails:</label> <s:radio
						list="#{'Yes':'Yes','No':'No'}"
						name="operator.doSendActivationEmail" theme="pics" /></li>
					<li><label>Approves Contractors:</label> <s:radio
						list="#{'Yes':'Yes','No':'No'}"
						name="operator.approvesRelationships" theme="pics" /></li>
					<li><label
						title="The source of statistics that should be used to evaluate contractors">Health
					&amp; Safety Organization:</label> <s:radio
						list="#{'OSHA':'OSHA','MSHA':'MSHA','COHS':'Canadian OHS'}"
						name="operator.oshaType" theme="pics" /></li>
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
					<li><label>Send Emails to:</label> <s:textarea
						name="operator.activationEmails" rows="3" cols="40" /> <br />
					* separate emails with commas ex: a@bb.com, c@dd.com</li>

				</ol>
				</fieldset>
				<fieldset class="form"><legend><span>Linked
				Accounts</span></legend>
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

						<li>
						<div style="font-weight: bold; text-align: center;">Operator
						Configuration Inheritance</div>
						</li>
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
						<li><label>Policy Types:</label> <s:select
							name="foreignKeys.inheritInsurance"
							value="operator.inheritInsurance.id" list="relatedFacilities"
							listKey="id" listValue="name"></s:select> <a
							href="?id=<s:property value="operator.inheritInsurance.id"/>">Go</a></li>
						<li><label>Audit Types:</label> <s:select
							name="foreignKeys.inheritAudits"
							value="operator.inheritAudits.id" list="relatedFacilities"
							listKey="id" listValue="name"></s:select> <a
							href="?id=<s:property value="operator.inheritAudits.id"/>">Go</a></li>
						<li><label>Audit Categories:</label> <s:select
							name="foreignKeys.inheritAuditCategories"
							value="operator.inheritAuditCategories.id"
							list="relatedFacilities" listKey="id" listValue="name"></s:select>
						<a
							href="?id=<s:property value="operator.inheritAuditCategories.id"/>">Go</a></li>
					</s:if>

				</ol>
				</fieldset>
				<s:if test="operator.id > 0">
					<pics:permission perm="UserRolePicsOperator" type="Edit">
						<fieldset class="form"><legend><span>Manage
						Representatives</span></legend>
						<ol>
							<li><nobr><label>Sales Representatives :</label></nobr></li>
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
										<s:if test="role.description == 'Sales Representative'">
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

							<li><nobr><label>Account Managers : </label></nobr></li>
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
										<s:if test="role.description == 'Account Manager'">
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
						</ol>
						</fieldset>
					</pics:permission>
				</s:if></td>
			</s:if>
		</tr>
	</table>
	<br clear="all">
	<div><input type="submit" class="picsbutton positive"
		name="button" value="Save" /></div>
</s:form>
</body>
</html>