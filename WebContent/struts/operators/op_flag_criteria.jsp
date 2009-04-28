<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js"></script>
<script type="text/javascript" src="js/op_flag_criteria.js"></script>

<style type="text/css">
#OverlayContainer {
	position: absolute;
	width: 100%;
	height: 700px;
	z-index: 1;
	background: #333333;
	left: 0px;
	top: 0px;
}

#ModalBox {
	position: absolute;
	width: 428px;
	height: 258px;
	z-index: 1;
	left: 270px;
	top: 0px;
	background-color: #FFFFFF;
	padding: 10px;
}

#ModalContent {
	position: absolute;
	width: 419px;
	height: 249px;
	z-index: 1;
	left: 13px;
	top: 11px;
}
</style>
</head>
<body>
<h1>Manage Flag Criteria <span class="sub"><s:property value="operator.name" /></span></h1>

<div id="criteriaList"><s:include value="op_flag_criteria_list.jsp"></s:include></div>

<div id="criteriaEdit">
Edit Criteria
</div>

<div id="criteriaAdd" style="display: none">Add Criteria Search for Question</div>

<a href="#" onclick="Effect.Appear('OverlayContainer'); return false;">Yay! Click me! </a>

<div id="OverlayContainer" style="float: left; display: none;">
<div id="ModalBox">
<div class="style1" id="ModalContent">Add text here! <a href='#' onclick='closeModal()'>Close Me!</a></div>
</div>
</div>
Viola! there you have it, but wait... how do we open it?! simple, just add this as the link:

</body>
</html>
