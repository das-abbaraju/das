<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Answers Recently Changed</title>
<s:include value="reportHeader.jsp" />
</head>
<body>
<h1>Answers Recently Changed</h1>

<s:include value="filters.jsp" />

<div><s:property value="report.pageLinksWithDynamicForm"
	escape="false" /></div>
<s:form id="assignScheduleAuditsForm" method="post" cssClass="forms">
	<table class="report">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor</a></td>
				<td align="center"><a href="javascript: changeOrderBy('form1','atype.auditName');">Type</a></td>
				<td align="center">Question</td>
				<td align="center">Answer</td>
				<td align="center">UpdatedDate</td>
				<s:if test="showContact">
					<td>Primary Contact</td>
					<td>Phone</td>
					<td>Email</td>
					<td>Office Address</td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');">City</a></td>
					<td><a href="javascript: changeOrderBy('form1','a.state,a.name');">State</a></td>
					<td>Zip</td>
					<td>Web_URL</td>
				</s:if>
				<s:if test="showTrade">
					<td>Trade</td>
					<td>Industry</td>			
				</s:if>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="get('auditID')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:property value="[0].get('auditName')"/></a></td>
				<td><s:property value="get('question')"/></td>
				<td><s:property value="get('answer')"/></td>
				<td><s:date name="get('updateDate')"/></td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('state')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
					<td><s:property value="get('industry')"/></td>
				</s:if>
			</tr>
		</s:iterator>
	</table>
</s:form>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
</body>
</html>
