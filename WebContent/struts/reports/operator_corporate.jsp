<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Accounts Report</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<script type="text/javascript">
$(document).ready(function() {
	$('.cluetip').cluetip({
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false
	});
});
</script>
</head>
<body>
<h1>Manage <s:property value="accountType"/> Accounts</h1>
<s:if test="canEditCorp">
	<div><a href="FacilitiesEdit.action?type=Corporate" class="add">Create New Corporate</a></div>
</s:if>
<s:if test="canEditOp">
	<div><a href="FacilitiesEdit.action?type=Operator" class="add">Create New Operator</a></div>
</s:if>	
<s:if test="canEditAssessment">
	<div><a href="AssessmentCenterEdit.action" class="add">Create New Assessment Center</a></div>
</s:if>

<s:include value="filters_operator_corporate.jsp" />

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
		<tr>
			<th></th>
			<th>Name</th>
			<th>Type</th>
			<th>Status</th>
			<th>Industry</th>
			<th><a href="#" class="cluetip help" title="Contractors / Operators" rel="#watchtip">Contractors / Operators</a>
				<div id="watchtip">
					For Operators, this number shows how many contractors are under the operator.
					For Corporate, this number shows how many operators are under the corporate account.  
				</div>
			</th>
			<s:if test="filter.primaryInformation">
				<th>Primary Contact</th>
				<th>Phone</th>
				<th>Email</th>
				<th>Address</th>
				<th>City</th>
				<th>State</th>
				<th>Country</th>
				<th>Zip</th>
			</s:if>
			<s:if test="canDeleteOp || canDeleteCorp">
				<th>Remove</th>
			</s:if>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td>
				<s:if test="get('type') == 'Operator' || get('type') == 'Corporate'">
					<a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>"
						rel="OperatorQuickAjax.action?id=<s:property value="get('id')"/>"
						class="operatorQuick account<s:property value="get('status')" />"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</s:if>
				<s:else>
					<a href="AssessmentCenterEdit.action?id=<s:property value="get('id')"/>"
						class="account<s:property value="get('status')" />"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</s:else>
			</td>
			<td><s:property value="get('type')"/></td>
			<td><s:property value="get('status')"/></td>
			<td><s:property value="get('industry')"/></td>
			<td class="right">
				<s:if test="get('type') == 'Operator'"><s:property value="get('opCount')"/></s:if>
				<s:if test="get('type') == 'Corporate'"><s:property value="get('corpCount')"/></s:if>
			</td>
			<s:if test="filter.primaryInformation">
				<td><s:property value="get('contactname')"/></td>
				<td><s:property value="get('contactphone')"/></td>
				<td><s:property value="get('contactemail')"/></td>
				<td><s:property value="get('address')"/></td>
				<td><s:property value="get('city')"/></td>
				<td><s:property value="get('state')"/></td>
				<td><s:property value="get('country')"/></td>
				<td><s:property value="get('zip')"/></td>
			</s:if>
			<s:if test="canDeleteOp || canDeleteCorp">
				<td>
					<s:if test="(canDeleteOp && get('opCount') == null && get('conCount') == null) || (canDeleteCorp && get('corpCount') == null))">
						<s:form action="ReportAccountList" method="POST">
							<s:submit value="Remove" name="button" type="picsbutton negative" />
							<s:hidden value="%{get('id')}" name="accountID"/>
						</s:form>
					</s:if>
				</td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>