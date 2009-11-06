<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page language="java" errorPage="../../exception_handler.jsp"%>
<html>
<head>
<title>Edit Operator/Audit Permissions</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css?v=20091105" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
function save(id) {
	$('#button'+id).hide();
	var data = $('#ao'+id).toObj();

	startThinking({'div':'td'+id, 'message':'Saving audit/operator data'});
	$('#td'+id).load('AuditOperatorSaveAjax.action', data, function() {
			$(this).effect('highlight', {color: '#FFFF11'}, 1000);
		}
	);
}
</script>
</head>
<body>
<h1>Edit Operator/Audit Permissions <span class="sub"><s:property
	value="aName" /><s:property value="oName" /></span></h1>
<div>

<div>
<table style="width: 100%;">
	<tr>
		<s:if test="inheritsAudits.size > 0">
			<td style="padding: 10px;">
			<h3>Companies that inherit the Audit configuration</h3>
			<ul>
				<s:iterator value="inheritsAudits">
					<li><a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="name" /></a></li>
				</s:iterator>
			</ul>
			</td>
		</s:if>
		<s:if test="inheritsInsurance.size > 0">
			<td style="padding: 10px;">
			<h3>Companies that inherit the InsureGUARD&trade; configuration</h3>
			<ul>
				<s:iterator value="inheritsInsurance">
					<li><a href="FacilitiesEdit.action?id=<s:property value="id"/>"><s:property value="name" /></a></li>
				</s:iterator>
			</ul>
			</td>
		</s:if>
	</tr>
</table>
</div>



	<s:if test="oID > 0">
		<s:select list="operators" listKey="id" cssClass="blueMain"
			onchange="location = '?oID='+this.options[this.selectedIndex].value;"
			listValue="name" value="oID"></s:select>
	</s:if>
	<s:else>
		<s:select list="auditTypes" listKey="id" cssClass="blueMain"
			onchange="location = '?aID='+this.options[this.selectedIndex].value;"
			listValue="auditName" value="aID"></s:select>
		<br /><a href="ManageAuditType.action?id=<s:property value="aID"/>">Manage Audit Definition</a>
	</s:else>
	<br />
	<a href="AuditOperator.action?<s:if test="aID > 0">aID=<s:property value="aID" /></s:if><s:if test="oID > 0">oID=<s:property value="oID" /></s:if>" class="refresh">Refresh Page</a>
</div>

<table class="report" cellspacing="1" cellpadding="3" border="0">
	<thead>
	<tr>
		<th>Operator/Audit</th>
		<th>&nbsp;&nbsp;Save&nbsp;&nbsp;</th>
		<th style="text-align: left;">&nbsp;V&nbsp;&nbsp;| 
			&nbsp;E&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;Minimum Risk Level&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;Flag Color&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|
			&nbsp;&nbsp;Required Status 
		</th>
	</tr>
	</thead>

	<s:iterator value="data" status="stat">
		<tr class="blueMain">
			<td>
			<s:if test="oID > 0">
				<a
					href="AuditOperator.action?aID=<s:property value="auditType.id" />"><s:property
					value="auditType.auditName" /></a>
			</s:if>
			<s:else>
				<a
					href="AuditOperator.action?oID=<s:property value="operatorAccount.id" />"><s:property
					value="operatorAccount.name" /></a>
			</s:else>
			</td>
			<td>
			
				<div class="buttons" style="margin: 0; padding: 0;">
					<button type="button" id="button<s:property value="htmlID"/>"
						onclick="save('<s:property value="htmlID"/>')"
						style="padding: 2px; display: none;">Save</button>
				</div>
			</td>
			<td id="td<s:property value="htmlID" />">
				<s:include value="audit_operator_include.jsp"></s:include>
			</td>
		</tr>
	</s:iterator>
</table>
</body>
</html>
