<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
	<head>
		<title><s:text name="ReportSubcontractors.title" /></title>
		<s:include value="reportHeader.jsp" />
	</head>
	<body>
		<div id="${actionName}-page">
			<h1><s:text name="ReportSubcontractors.title" /></h1>
			
			<s:include value="filters.jsp" />
			
			<s:form id="link_subs">
				<s:text name="ReportSubcontractors.LinkSubcontractors" />
				<s:select
					list="%{gcOperator.gcContractorOperatorAccounts}"
					listKey="id"
					listValue="name"
					name="operatorToLink"
				/>
				<input type="button" value="<s:text name="button.Save" />" class="picsbutton positive save" data-url="ReportSubcontractorsAjax!save.action" />
				<div id="report_data">
					<s:include value="report_subcontractors_data.jsp" />
				</div>
			</s:form>
		</div>
	</body>
</html>
