<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title><s:property value="operator.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
</head>
<body>
<s:if test="operator == null">
	<h1>Create New <s:property value="type" /> Account</h1>
</s:if>
<s:else>
	<s:include value="opHeader.jsp"></s:include>
</s:else>
<s:include value="../actionMessages.jsp" />

<s:if test="id > 0 && operator.visibleAudits.size == 0">
	<div id="alert">This operator doesn't have access to any audits. Please <a
		href="AuditOperator.action?oID=<s:property value="id"/>">double check the configuration</a>.</div>
</s:if>
<s:if test="id > 0 && operator.flagQuestionCriteriaInherited.size == 0">

	<div id="alert">This operator doesn't have any flag criteria defined. Please <a
		href="OperatorFlagCriteria.action?id=<s:property value="id"/>">double check the configuration</a>.</div>
</s:if>

<s:form id="save" method="POST" enctype="multipart/form-data">
	<div><input type="submit" class="picsbutton positive" name="button" value="Save" /></div>
	<br clear="all" />
	<s:hidden name="id" />
	<s:hidden name="type" />
	<table>
		<tr>
			<td style="vertical-align: top; width: 50%;">
			<fieldset class="form"><legend><span>Details</span></legend>
			<ol>
				<li><label>Name:</label> <s:textfield name="operator.name" size="35" /></li>
				<li><label>Primary Contact:</label> <s:textfield name="operator.contact" /></li>
				<li><label>Industry:</label> <s:select list="industryList" name="operator.industry" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Primary Address</span></legend>
			<ol>
				<li><label>Address:</label> <s:textfield name="operator.address" size="35" /></li>
				<li><label>City:</label> <s:textfield name="operator.city" size="20" /></li>
				<li><label>Country:</label>
					<s:select list="@com.picsauditing.PICS.Inputs@COUNTRY_ARRAY" 
					name="operator.country"
					onchange="(this.value == 'USA' || this.value == 'Canada') ? $('state_li').show() : $('state_li').hide();"
				/></li>
				<li id="state_li" 
					<s:if test="operator.country != 'USA' && operator.country != 'Canada'">style="display: none"</s:if>
					><label>State/Province:</label>
					<s:select list="StateList" name="operator.state"/>
				</li>
				<li><label>Zip:</label> <s:textfield name="operator.zip" size="7" /></li>
				<li><label>Phone:</label> <s:textfield name="operator.phone" size="15" /></li>
				<li><label>Phone 2:</label> <s:textfield name="operator.phone2" size="15" /></li>
				<li><label>Fax:</label> <s:textfield name="operator.fax" size="15" /></li>
				<li><label>Email:</label> <s:textfield name="operator.email" size="30" /></li>
				<li><label>Web URL:</label> <s:textfield name="operator.webUrl" size="30" /></li>
			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Visible Audits</span></legend>
			<ol>
				<s:iterator value="operator.visibleAudits">
					<li><label><s:property value="auditType.auditName" />:</label> Risk >= <s:property value="minRiskLevel" />,
					Status >= <s:property value="requiredAuditStatus" />, Flag = <s:property value="requiredForFlag" /> <s:if
						test="canEdit">, Editable = Yes</s:if></li>
				</s:iterator>
			</ol>
			</fieldset>
			</td>
			<td style="vertical-align: top; width: 50%; padding-left: 10px;">
			<fieldset class="form"><legend><span>Admin Fields</span></legend>
			<ol>
				<li><label>Visible?</label> <s:radio list="#{'Y':'Yes','N':'No'}" name="operator.active" theme="pics" /></li>
				<li><label>Reason:</label>
					<s:textarea name="operator.reason" rows="3" cols="25"/>
				</li>
				<li><label>Receive contractor activation emails:</label> <s:radio list="#{'Yes':'Yes','No':'No'}"
					name="operator.doSendActivationEmail" theme="pics" /></li>
				<li><label>Approves Contractors:</label> <s:radio list="#{'Yes':'Yes','No':'No'}"
					name="operator.approvesRelationships" theme="pics" /></li>
				<s:if test="!operator.corporate">
					<li><label title="The source of statistics that should be used to evaluate contractors">Health &amp;
					Safety Organization:</label> <s:radio list="#{'OSHA':'OSHA','MSHA':'MSHA','COHS':'Canadian OHS'}" name="operator.oshaType"
						theme="pics" /></li>
				</s:if>
				<li><label>InsureGuard&trade;:</label> <s:radio list="#{'Yes':'Yes','No':'No'}" name="operator.canSeeInsurance"
					theme="pics" /></li>

				<li><label>Contractors pay:</label> <s:radio list="#{'Yes':'Yes','No':'No','Multiple':'Multiple'}"
					name="operator.doContractorsPay" theme="pics" /></li>
				<li><label>Send Emails to:</label> <s:textarea name="operator.activationEmails" rows="3" cols="40" /> <br />
				* separate emails with commas ex: a@bb.com, c@dd.com</li>

			</ol>
			</fieldset>
			<fieldset class="form"><legend><span>Linked Accounts</span></legend>
			<ol>
				<s:if test="operator.corporate">
					<li><label>Facilities:</label> <s:select list="operatorList" listValue="name" listKey="id" name="facilities"
						multiple="7" size="15" /></li>
				</s:if>
				<s:if test="operator.operator">
					<s:if test="operator.corporateFacilities.size() > 0">
						<li><label>Parent Corporation / Division / Hub:</label> <s:select name="foreignKeys.parent"
							list="operator.corporateFacilities" listKey="corporate.id" listValue="corporate.name" headerKey="0"
							headerValue=" - Select a Parent Facility - " value="operator.parent.id" /> <a
						href="?id=<s:property value="operator.parent.id"/>">Go</a></li>
					</s:if>

					<li>
					<div style="font-weight: bold; text-align: center;">Operator Configuration Inheritance</div>
					</li>
					<li><label>Flag Criteria:</label> <s:select name="foreignKeys.inheritFlagCriteria"
						value="operator.inheritFlagCriteria.id" list="relatedFacilities" listKey="id" listValue="name"></s:select> <a
						href="?id=<s:property value="operator.inheritFlagCriteria.id"/>">Go</a></li>
					<li><label>Insurance Criteria:</label> <s:select name="foreignKeys.inheritInsuranceCriteria"
						value="operator.inheritInsuranceCriteria.id" list="relatedFacilities" listKey="id" listValue="name"></s:select> <a
						href="?id=<s:property value="operator.inheritInsuranceCriteria.id"/>">Go</a></li>
					<li><label>Policy Types:</label> <s:select name="foreignKeys.inheritInsurance"
						value="operator.inheritInsurance.id" list="relatedFacilities" listKey="id" listValue="name"></s:select> <a
						href="?id=<s:property value="operator.inheritInsurance.id"/>">Go</a></li>
					<li><label>Audit Types:</label> <s:select name="foreignKeys.inheritAudits" value="operator.inheritAudits.id"
						list="relatedFacilities" listKey="id" listValue="name"></s:select> <a
						href="?id=<s:property value="operator.inheritAudits.id"/>">Go</a></li>
					<li><label>Audit Categories:</label> <s:select name="foreignKeys.inheritAuditCategories"
						value="operator.inheritAuditCategories.id" list="relatedFacilities" listKey="id" listValue="name"></s:select> <a
						href="?id=<s:property value="operator.inheritAuditCategories.id"/>">Go</a></li>
				</s:if>

			</ol>
			</fieldset>
			<pics:permission perm="UserRolePicsOperator" type="Edit">
			<fieldset class="form"><legend><span>Manage Representatives</span></legend>
			<ol><s:iterator value="operator.accountUsers" status="role">
					<li><label><nobr><s:property value="role.description"/>:</nobr></label><li>
					<li><nobr><s:select name="roleMap[%{id}]" list="userList" value="%{user.id}" listKey="id" listValue="name"/>
						<s:textfield name="operator.accountUsers[%{#role.index}].ownerPercent" value="%{ownerPercent}" size="3"/>%
						&nbsp;<a href="FacilitiesEdit.action?id=<s:property value="operator.id"/>&accountUserId=<s:property value="id"/>&button=Remove" class="remove">Remove</a></nobr>
					</li>
				</s:iterator>
			</ol>
			<ol>
				<li><label>Add New:</label></li>
				<li><s:select name="accountRole" list="roleList" listValue="description"/>
					<s:select name="userid" list="userList" listKey="id" listValue="name" headerKey="0" headerValue="- Select a User -"/><br/>
					<input type="submit" class="picsbutton positive" name="button" value="Add Role" />
				</li>
			</ol>
			</fieldset>
			</pics:permission>
			</td>
		</tr>
	</table>
	<br clear="all">
	<div><input type="submit" class="picsbutton positive" name="button" value="Save" /></div>
</s:form>
<div id="caldiv1"
	style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>
</body>
</html>
