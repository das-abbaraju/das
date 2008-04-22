<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Schedule &amp; Assign Audits</title>
<script type="text/javascript" src="js/Search.js" />
<script type="text/javascript" src="js/prototype.js" />
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects" />
<style>
td.reportDate {
	text-align: center;
}
</style>
</head>
<body>
<h1>Schedule &amp; Assign Audits</h1>

<s:form id="form1" method="post">
	<table>
	<tr>
		<td style="vertical-align: middle;"><s:textfield name="name" cssClass="forms" size="8" onfocus="clearText(this)" />
			<s:select list="auditTypeList" cssClass="forms" name="auditTypeID" listKey="auditTypeID" listValue="auditName" />
			<s:select list="operatorList" cssClass="forms" name="operator" listKey="id" listValue="name" />
		</td>
		<td><s:submit name="imageField" type="image" src="images/button_search.gif" onclick="runSearch( 'form1')" /></td>
	</tr>
	</table>
	<s:hidden name="showPage" value="1"/>
	<s:hidden name="startsWith" />
	<s:hidden name="orderBy" />
</s:form>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
	<td><a class="blueMain" href="AuditAssignments.action">Reset</a></td>
	<td align="left"><s:property value="report.startsWithLinksWithDynamicForm" escape="false"/></td>
	<td align="right"><s:property value="report.pageLinksWithDynamicForm" escape="false"/></td>
  </tr>
</table>

<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
<table border="0" cellpadding="1" cellspacing="1" align="center" width="100%">
	<tr bgcolor="#003366" class="whiteTitle"> 
		<td><a href="?orderBy=a.name" class="whiteTitle">Contractor</a></td>
		<td align="center"><a href="?orderBy=auditType" class="whiteTitle">Type</a></td>
		<td align="center"><a href="?orderBy=createdDate DESC" class="whiteTitle">Created</a></td>
		<td align="center"><a href="?orderBy=auditorID DESC,name" class="whiteTitle">Auditor</a></td>
		<td align="center"><a href="?orderBy=assignedDate DESC" class="whiteTitle">Assigned</a></td>
		<td align="center"><a href="?orderBy=scheduledDate,a.name" class="whiteTitle">Scheduled</a></td>
		<td align="center"><a href="?orderBy=auditLocation,a.name" class="whiteTitle">Location</a></td>
		<td align="center">Exempt</td>
		<td></td>
	</tr>
	<s:iterator value="data">
	<tr class="blueMain" <s:property value="color.nextBgColor" escape="false" />>
		<td><a href="pqf_view.jsp?auditID=<s:property value="[0].get('auditID')"/>" 
			class="blueMain"><s:property value="[0].get('name')"/></a>
		</td>
		<td><s:property value="[0].get('auditName')"/></td>
		<td class="reportDate"><s:date name="[0].get('createdDate')" format="M/d/yy" /></td>
		<td><s:select list="auditorList" cssClass="forms" value="[0].get('auditorID')" name="operator" listKey="id" listValue="name" /></td>
		<td class="reportDate"><s:date name="[0].get('assignedDate')" format="M/d/yy" /></td>
		<td><s:date name="[0].get('scheduledDate')" format="M/d/yy" /></td>
		<td><s:textfield name="auditLocation" /></td>
		<td></td>
		<td><s:submit value="Save" onclick="saveAudit();" cssClass="forms" /></td>
	</tr>
	</s:iterator>
</table>
</s:form>
<center>
<s:property value="report.pageLinksWithDynamicForm" escape="false"/>
</center>
		
</body>
</html>
