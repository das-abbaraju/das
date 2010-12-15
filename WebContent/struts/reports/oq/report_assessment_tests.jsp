<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="subHeading" /></title>
<s:include value="../reportHeader.jsp" />
<script type="text/javascript">
function changeOrderBy(orderBy) {
	$('#form1 input[name=orderBy]').val(orderBy);
	startThinking({ div: "report_data", type: "large" });
	$('#report_data').load('ReportAssessmentTestsAjax.action?' + $('#form1').serialize());
}
</script>
</head>
<body>

<h1><s:property value="subHeading" /></h1>

<s:include value="../filters_employee.jsp" />

<div id="report_data">
	<s:include value="report_assessment_tests_data.jsp"></s:include>
</div>

</body>
</html>
