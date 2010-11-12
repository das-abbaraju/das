<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<style type="text/css">
span.questionNumber{
display:none}
</style>
<title>Services Performed</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>

<script type="text/javascript">
	var auditID = '<s:property value="auditID"/>';
	var catDataID = '<s:property value="catDataID"/>';
	var conID = '<s:property value="id"/>';
	var mode = 'Edit';
</script>

</head>
<body>

<s:include value="registrationHeader.jsp"></s:include>

<div class="info">Answers on this page automatically save. Once you are finished, click <b>Next</b> at the bottom to go to the next step.</div>
<div id="auditViewArea">
	<s:iterator value="categories" id="category">
	<s:include value="../audits/audit_cat_view.jsp"/>
	</s:iterator>
</div>

<div class="buttons">
	<a id="next_link" class="picsbutton positive" href="ContractorRegistrationServices.action?id=<s:property value="id"/>&button=calculateRisk<s:if test="requestID > 0">&requestID=<s:property value="requestID" /></s:if>">Next &gt;&gt;</a>
</div>
</body>
</html>
