<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Services Performed</title>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />

<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects,controls"></script>
<script type="text/javascript" src="js/validateForms.js"></script>
<script type="text/javascript" src="js/audit_cat_edit.js"></script>

<script type="text/javascript">
	var auditID = '<s:property value="auditID"/>';
	var catDataID = '<s:property value="catDataID"/>';
	var conID = '<s:property value="id"/>';
	var mode = 'Edit';
</script>

</head>
<body>

<s:include value="registrationHeader.jsp"></s:include>

<h4 class="groupTitle">
Please select the services your company performs<br>
(C) denotes services you perform (S) denotes services performed by subcontractors
</h4>

<s:iterator value="questions">
	<s:set name="q" value="[0]" />
	<s:if test="#q.visible">
		<s:set name="a" value="answerMap.get(#q.id)" />
		<s:set name="paid" value="0" />
		<s:set name="shaded" value="!#shaded" scope="action" />
		<div id="node_<s:property value="#attr.paid"/>_<s:property value="#q.id"/>"
			class="question <s:if test="#shaded">shaded</s:if>"><s:include value="../audits/audit_cat_edit.jsp"></s:include></div>
	</s:if>
</s:iterator>

</body>
</html>
