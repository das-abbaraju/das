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
				<td align="center"><a>Type</a></td>
				<td align="center">Question</td>
				<td align="center">Answer</td>
				<td align="center">UpdatedDate</td>
				<s:if test="showContact">
					<td><s:text name="global.ContactPrimary" /></td>
					<td><s:text name="User.phone" /></td>
					<td><s:text name="User.email" /></td>
					<td><s:text name="global.OfficeAddress" /></td>
					<td><a href="javascript: changeOrderBy('form1','a.city,a.name');"><s:text name="global.City" /></a></td>
					<td><a href="javascript: changeOrderBy('form1','a.countrySubdivision,a.name');"><s:text name="CountrySubdivision" /></a></td>
					<td><s:text name="global.ZipPostalCode" /></td>
					<td><s:text name="ContractorAccount.webUrl" /></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:text name="Trade" /></td>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr id="audit_<s:property value="get('auditID')"/>">
				<td class="right"><s:property
					value="#stat.index + report.firstRowNumber" /></td>
				<td><a href="ContractorView.action?id=<s:property value="[0].get('id')"/>"><s:property value="[0].get('name')"/></a>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="[0].get('auditID')"/>"><s:text name="%{[0].get('atype.name')}" /></a></td>
				<td><s:property value="get('question')"/></td>
				<td><s:property value="get('answer')"/></td>
				<td><s:date name="get('updateDate')"/></td>
				<s:if test="showContact">
					<td><s:property value="get('contactname')"/></td>
					<td><s:property value="get('contactphone')"/></td>
					<td><s:property value="get('contactemail')"/></td>
					<td><s:property value="get('address')"/></td>
					<td><s:property value="get('city')"/></td>
					<td><s:property value="get('countrySubdivision')"/></td>
					<td><s:property value="get('zip')"/></td>
					<td><s:property value="get('web_URL')"/></td>
				</s:if>
				<s:if test="showTrade">
					<td><s:property value="get('main_trade')"/></td>
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
