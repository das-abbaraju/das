<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Accounts Report</title>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
</head>
<body>
<h1>Manage <s:property value="accountType"/> Accounts</h1>
<s:if test="canEdit">
	<s:if test="accountType.equals('Corporate')">
		<div><a href="accounts_new_operator.jsp?type=<s:property value="accountType"/>">Create New Corporate</a></div>
	</s:if>
	<s:else>
		<div><a href="accounts_new_operator.jsp?type=<s:property value="accountType"/>">Create New</a></div>	
	</s:else>
</s:if>

<s:form id="form1" name="form1" method="post">

<s:hidden name="showPage" value="1"/>
<s:hidden name="startsWith" />
<s:hidden name="orderBy" />
</s:form>

<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
    <table class="report">
        <thead><tr> 
           <td></td>
			<th><a href="?orderBy=a.name">Name</a></th>
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
			
			<s:if test="%{[0].get('active') == \"Y\"}">
			<td><a href="accounts_edit_operator.jsp?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
			</td></s:if>
			<s:else>
			<td class="inactive"><a href="accounts_edit_operator.jsp?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/>*</a>
			</td>
			</s:else>
			<td><s:property value="get('industry')"/></td>
			<td class="right"><s:property value="get('city')"/></td>
			<td class="right"><s:property value="get('state')"/></td>
			<td class="right"><s:property value="get('contact')"/></td>
			<td><s:property value="get('subCount')"/></td>
			<td><s:if test="canDelete && get('subCount') == null">
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
  			