<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Untagged Contractors</title>
<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
<h1>Untagged Contractors</h1>
<s:include value="../reports/filters.jsp" />

<div id="report_data">
<s:if test="report.allRows == 0">
	<div class="info">No contractor is missing a tag</div>
</s:if>
<s:else>

<div>
<s:property value="report.pageLinks" escape="false" />
</div>
<s:form>
<s:hidden name="opID" />
<table class="report">
	<thead>
	<tr>
		<th colspan="2">Contractor Name</th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a
				href="ContractorView.action?id=<s:property value="get('id')"/>"
				rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
				class="contractorQuick" title="<s:property value="get('name')" />"
				><s:property value="get('name')" /></a>
			</td>
			<td><s:checkbox name="contractors" fieldValue="%{get('id')}" />
		</tr>
	</s:iterator>
	<s:if test="data.size() > 0">
		<tr>
			<td colspan="3" class="right">
				<s:select list="operator.tags" listKey="id" listValue="%{isRequired(id) ? tag + ' (required tag)' : tag}"
					headerValue="- Contractor Tags -" headerKey="0" name="tagID" />
				<input type="submit" class="picsbutton positive" value="Save" name="button" />
			</td>
		</tr>
	</s:if>
	</tbody>
</table>
</s:form>
<div>
<s:property value="report.pageLinks" escape="false" />
</div>
</s:else>
</div>

</body>
</html>
