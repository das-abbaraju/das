<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="EmployeeList.title" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
$(function() {
	<s:if test="report.allRows > 500">
		$('a.excel').live('click', function(e) {
			return confirm(translate('JS.ConfirmDownloadAllRows', ['<s:property value="report.allRows" />']));
		});
	</s:if>
});
</script>
</head>
<body>
<h1><s:text name="EmployeeList.title" /></h1>

<s:include value="../actionMessages.jsp" />
<s:include value="filters_employee.jsp" />

<div class="right">
	<a class="excel" href="EmployeeList!download.action" target="_BLANK"
		title="<s:text name="javascript.DownloadAllRows"><s:param value="%{report.allRows}" /></s:text>">
		<s:text name="global.Download" />
	</a>
</div>

<div id="report_data">
<s:include value="employee_list_data.jsp"></s:include>
</div>

</body>
</html>
