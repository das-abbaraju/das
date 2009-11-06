<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<style type="text/css"/>
span.questionNumber{
display:none}
</style>
<title>Services Performed</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=20091105" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />

<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/validateForms.js?v=20091105"></script>
<script type="text/javascript" src="js/audit_cat_edit.js?v=20091105"></script>

<script type="text/javascript">
	var auditID = '<s:property value="auditID"/>';
	var catDataID = '<s:property value="catDataID"/>';
	var conID = '<s:property value="id"/>';
	var mode = 'Edit';
</script>

</head>
<body>

<s:include value="registrationHeader.jsp"></s:include>


<div id="info">Answers on this page automatically save. Once you are finished, click <b>Next</b> at the bottom to go to the next step.</div>
<br/>
<h3 class="subCategory">General Info</h3>
<s:iterator value="infoQuestions">
	<s:set name="q" value="[0]" />
	<s:set name="a" value="answerMap.get(#q.id)" />
	<s:set name="shaded" value="!#shaded" scope="action" />
	<div id="node_<s:property value="#q.id"/>"
		class="question <s:if test="#shaded">shaded</s:if>"><s:include value="../audits/audit_cat_edit.jsp"></s:include></div>
</s:iterator>
<h3 class="subCategory">Services Performed</h3>
<h4 class="groupTitle">
Please select the services your company performs<br>
(C) denotes services you perform (S) denotes services performed by subcontractors
</h4>
<s:iterator value="serviceQuestions">
	<s:set name="q" value="[0]" />
	<s:if test="#q.visible">
		<s:set name="a" value="answerMap.get(#q.id)" />
		<s:set name="shaded" value="!#shaded" scope="action" />
		<div id="node_<s:property value="#q.id"/>"
			class="question <s:if test="#shaded">shaded</s:if>"><s:include value="../audits/audit_cat_edit.jsp"></s:include></div>
	</s:if>
</s:iterator>


<div class="buttons">
	<a class="picsbutton positive" href="ContractorRegistrationServices.action?id=<s:property value="id"/>&button=calculateRisk">Next &gt;&gt;</a>
</div>
</body>
</html>
