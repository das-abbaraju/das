<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/exception_handler.jsp" %>
<html>
	<head>
		<title>
			<s:text name="AuditCategoryMatrix.title"/>
		</title>
		<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
		<style type="text/css">
			table.report thead a
			{
				background: none;
				padding-right: 0;
			}
			
			table.report thead a:hover
			{
				text-decoration: none;
			}
		</style>
	</head>
	<body>
		<h1>
			<s:text name="AuditCategoryMatrix.title"/>
		</h1>
		<div id="AuditCategoryMatrix">
			<div id="messages">
				<s:include value="../actionMessages.jsp" />
			</div>
			<div id="search">
				<s:form id="form1">
					<div class="filterOption">
						<s:text name="AuditType" />
						<s:select
							id="select_audit_type"
							list="#{100:getTextNullSafe('AuditType.100.name')}"
							headerKey="0"
							headerValue="%{getText('global.SelectAuditType')}" 
							name="auditType"
						/>
					</div>
					<div class="clear"></div>
					<div class="filterOption">
						<div id="categoryLoad">
							<s:hidden name="category" value="0" />
						</div>
					</div>
					<div class="clear"></div>
					<div id="filterLoad"></div>
				</s:form>
			</div>
			<br />
			<div id="table"></div>
		</div>
	</body>
</html>