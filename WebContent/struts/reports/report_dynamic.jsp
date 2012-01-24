<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<html>
	<head>
		<title><s:property value="report.summary"/></title>
		
		<style>

		.x-body
		{
			background: #FFFFFF;
		}
		
		</style>
	</head>
	<body>
		<script type="text/javascript">
			var reportID = '<s:property value="report.id"/>';
			var reportURL = 'ReportDynamic!data.action?<s:if test="report.id > 0">report=<s:property value="report.id"/></s:if><s:else>report.base=<s:property value="report.base"/></s:else>';
			var availableFields = <s:property value="availableFields" escape="false" />;
			var storeFields = <s:property value="storeFields" escape="false" />;
			var gridColumns = <s:property value="gridColumns" escape="false" />;
			
			var reportMenu = [];
			
			function addReportMenuItem(id, text) {
				reportMenu.push({
					text: text,
					href: 'ReportDynamic.action?report=' + id
				});
			}
			
			<s:iterator value="availableReports">
			addReportMenuItem('<s:property value="id"/>', '<s:property value="summary"/>');
			</s:iterator>
		</script>
	
		<script type="text/javascript" src="js/pics/app.js"></script>
	</body>
</html>