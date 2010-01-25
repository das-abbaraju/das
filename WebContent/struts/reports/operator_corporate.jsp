<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Accounts Report</title>
<s:include value="reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
</head>
<body>
<h1>Manage <s:property value="accountType"/> Accounts</h1>
<s:if test="canEdit">
	<s:if test="accountType.equals('Corporate')">
		<div><a href="FacilitiesEdit.action?type=<s:property value="accountType"/>">Create New Corporate</a></div>
	</s:if>
	<s:else>
		<div><a href="FacilitiesEdit.action?type=<s:property value="accountType"/>">Create New</a></div>	
	</s:else>
</s:if>

<s:form id="form1">
	<s:hidden name="filter.ajax" />
	<s:hidden name="filter.destinationAction" />
	<s:hidden name="filter.allowMailMerge" />
	<s:hidden name="showPage" value="1" />
	<s:hidden name="filter.startsWith" />
	<s:hidden name="orderBy" />
	<s:hidden name="accountType" />
</s:form>

<div class="alphapaging"><s:property
value="report.startsWithLinksWithDynamicForm" escape="false" /></div>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
    <table class="report">
        <thead><tr> 
           <td></td>
			<th><s:if test="accountType.equals('Corporate')">
				<a href="?accountType=Corporate&orderBy=a.name">Name</a>
			</s:if> <s:else>
				<a href="?accountType=Operator&orderBy=a.name">Name</a>
			</s:else></th>
			<td>Industry</td>
			<td>City</td>
			<td>State</td>
			<td>Primary Contact</td>
			<td><s:if test="accountType.startsWith('O')">Contractors</s:if>
			<s:else>Operators</s:else>
			</td>
			<td></td>
		</tr></thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			
			<s:if test="%{get('active') == \"Y\"}">
			<td><a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>" 
					rel="OperatorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="operatorQuick" title="<s:property value="get('name')" />"
					><s:property value="get('name')" /></a></td>
			</s:if>
			<s:else>
			<td class="inactive"><a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>&type=<s:property value="get('type')"/>" 
					rel="OperatorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="operatorQuick" title="<s:property value="get('name')" />"
					><s:property value="get('name')" />*</a></td>
			</s:else>
			<td><s:property value="get('industry')"/></td>
			<td class="right"><s:property value="get('city')"/></td>
			<td class="right"><s:property value="get('state')"/></td>
			<td class="right"><s:property value="get('contactname')"/></td>
			<td><s:property value="get('subCount')"/></td>
			<td><s:if test="canDelete && get('subCount') == null && get('requestedBy') == null">
			<s:form action="ReportAccountList" method="POST">
			<s:submit value="Remove" name="button"/>
			<s:hidden value="%{get('id')}" name="AccountID"/>
			</s:form>
			</s:if>
			</td>
		</tr>
	</s:iterator>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>