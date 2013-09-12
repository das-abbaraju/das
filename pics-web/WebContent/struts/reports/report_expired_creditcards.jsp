<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:property value="reportName" /></title>
<s:include value="reportHeader.jsp" />
<script type="text/javascript">
function setAllChecked(elm) {
	$('.massCheckable').attr({checked: $(elm).is(':checked')});
	return false;
}
</script>
</head>
<body>
<h1><s:property value="reportName" /></h1>

<s:include value="filters.jsp"/>
<div id="report_data">
<s:include value="report_expired_creditcards_data.jsp"></s:include>
</div>

</body>
</html>
