<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><s:text name="%{scope}.title" /></title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1><s:text name="%{scope}.title" /></h1>
<div id="report_data">
	<s:if test="report.allRows > 0">
		<table class="report">
			<thead>
				<tr>
					<th><s:text name="global.Contractor" /></th>
					<th><s:text name="AuditType.232.name" /></th>
					<th><s:text name="AuditType.1.name" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="data">
					<tr>
						<td><s:property value="get('name')" /></td>
						<td class="center">
							<a href="Audit.action?auditID=<s:property value="get('importPqfID')" />" class="preview"></a>
						</td>
						<td class="center">
							<a href="Audit.action?auditID=<s:property value="get('pqfID')" />" class="edit"></a>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
	<s:else>
		<div class="alert"><s:text name="Report.message.NoRowsFound" /></div>
	</s:else>
</div>
</body>
</html>