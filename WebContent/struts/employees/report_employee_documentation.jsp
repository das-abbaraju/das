<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<head>
	<title><s:text name="ReportEmployeeDocumentation.title"/></title>
	<s:include value="../reports/reportHeader.jsp" />
</head>
<body>
	<h1><s:text name="ReportEmployeeDocumentation.title"/></h1>

	<s:include value="../reports/filters_employee.jsp"/>

	<div id="report_data">
		<s:include value="report_employee_documentation_data.jsp" />
	</div>
	<script type="text/javascript">
		$(function() {
			$('#report_data').delegate('thead a', 'click', function (event) {
				var orderBy = $(this).attr('data-orderby');
				if (orderBy) {
					changeOrderBy('form1', orderBy);
				}
			});
		});
	</script>
</body>