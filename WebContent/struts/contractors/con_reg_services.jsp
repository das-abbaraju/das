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
<br/>
<h3 class="subCategory">General Info</h3>
<s:iterator value="infoQuestions">
	<s:set name="q" value="[0]" />
	<s:set name="a" value="answerMap.get(#q.id)" />
	<div id="node_<s:property value="#q.id"/>"
		class="clearfix question"><s:include value="../audits/audit_question_edit.jsp"></s:include></div>
</s:iterator>
<h3 class="subCategory">Services Performed</h3>
<h4 class="groupTitle">
Please select the services your company performs<br>
(C) denotes services you perform (S) denotes services performed by subcontractors
</h4>
<s:iterator value="serviceQuestions">
	<s:set name="q" value="[0]" />
	<s:if test="#q.current">
		<s:set name="a" value="answerMap.get(#q.id)" />
		<div id="node_<s:property value="#q.id"/>"
			class="clearfix question" style="padding-bottom:0px;"><s:include value="../audits/audit_question_edit.jsp"></s:include></div>
	</s:if>
</s:iterator>
<br clear="all" />

<div class="buttons">
	<a id="next_link" class="picsbutton positive" href="ContractorRegistrationServices.action?id=<s:property value="id"/>&button=calculateRisk<s:if test="requestID > 0">&requestID=<s:property value="requestID" /></s:if>">Next &gt;&gt;</a>
</div>
</body>
</html>
