<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<s:include value="../jquery.jsp"/>
<style type="text/css">
.flagImage {
	height: 12px;
	width: 10px;
	display: inline-block;
	clear: none;
}
</style>
</head>
<body>
<s:include value="opHeader.jsp"></s:include>
<div id="dialog" style="display:none"></div>

<div style="vertical-align: top">
	<div id="mainThinkingDiv" style="position: absolute; top: -15px; left: 20px;"></div>
	<div id="growlBox"></div>
	<s:include value="op_manage_flag_criteria_impact.jsp"></s:include>
	<s:include value="op_manage_flag_criteria_list.jsp"></s:include>
</div>

</body>
</html>
