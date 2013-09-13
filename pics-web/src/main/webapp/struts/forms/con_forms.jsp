<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:text name="global.Resources" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
</head>
<body>
<s:include value="../contractors/conHeader.jsp"></s:include>

<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Resource" /></th>
			<th><s:text name="global.OtherLanguages" /></th>
			<th><s:text name="global.Operator" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="resources" var="topform">
		<tr>
			<s:set name="bestResource" value="#topform.getMostApplicableForm(permissions.locale)" />
			<td>
			<s:set name="displayName" value="#bestResource.locale.getDisplayName(#bestResource.locale)" />
			<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
			<a href="Resources!download.action?id=<s:property value="#topform.id" />&amp;loc=<s:property value="#bestResource.locale.toString()" />"  target="_blank" title="<s:text name="global.ClickToView" />">
				<s:property value="#bestResource.formName"/> - <s:property value="#localeName" />
			</a></td>
			<td>
			<s:set name="addDelimiter" value="false" />
			<s:iterator value="#topform.allForms" var="altform">
				<s:if test="#bestResource.id!=#altform.id" >
					<s:if test="addDelimiter" > | </s:if>
					<s:set name="addDelimiter" value="true" />
					<s:set name="displayName" value="#altform.locale.getDisplayName(#altform.locale)" />
					<s:set name="localeName" value="#displayName.substring(0,1).toUpperCase() + #displayName.substring(1)" />
					<a href="Resources!download.action?id=<s:property value="#topform.id" />&amp;loc=<s:property value="#altform.locale.toString()" />"  target="_blank" title="<s:text name="global.ClickToView" />">
						<s:property value="#localeName" />
					</a>
				</s:if>
			</s:iterator>
			</td>
			<td><s:property value="account.name"/></td>
		</tr>
		</s:iterator>
	</tbody>
</table>
</body>
</html>
