<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/op_flag_criteria.js"></script>

</head>
<body>
<h1>Manage Flag Criteria
<span class="sub"><s:property value="operator.name"/></span>
</h1>

<div id="criteriaList">
<s:include value="op_flag_criteria_list.jsp"></s:include>
</div>

<div id="criteriaEdit" style="display: none">
Edit Criteria
</div>

<div id="criteriaAdd" style="display: none">
Add Criteria

Search for Question
</div>

</body>
</html>
