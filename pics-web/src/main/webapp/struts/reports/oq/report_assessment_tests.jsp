<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:text name="%{scope}.title" /></title>
<s:include value="../reportHeader.jsp" />
<script type="text/javascript">
function changeOrderBy(orderBy) {
	$('#form1 input[name="orderBy"]').val(orderBy);
	startThinking({ div: "report_data", type: "large" });
	$('#report_data').load('ReportAssessmentTests!data.action?' + $('#form1').serialize());
}

$(function() {
	$('#report_data').delegate('a.excel', 'click', function(e) {
		e.preventDefault();
		download('ReportAssessmentTests');
	});
});
</script>
</head>
<body>

<h1><s:text name="%{scope}.title" /></h1>

<s:include value="../filters_employee.jsp" />

<div id="report_data">
	<s:include value="report_assessment_tests_data.jsp"></s:include>
</div>

</body>
</html>
