<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Home</title>
<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script src="js/FusionCharts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css" />
<script type="text/javascript">
function hidePanel(panel) {
	$(panel+"_hide").hide();
	$(panel+"_show").show();
	
	$(panel+"_content").hide();
}

function showPanel(panel) {
	$(panel+"_hide").show();
	$(panel+"_show").hide();

	$(panel+"_content").show();
}
</script>
</head>
<body>
<h1>Welcome to PICS Online
<span class="sub"><s:property value="account.name" /></span>
</h1>

<s:iterator value="columns">
<div class="column" id="column<s:property value="key"/>" style="width: <s:property value="columnWidth"/>%">

<s:iterator value="value">
<div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
<div class="panel" id="panel<s:property value="widgetID"/>">
<div class="panel_header">
	<div class="panel_edit">
		<a id="panel<s:property value="widgetID"/>_hide" href="#" class="panel_edit" style="display: block" onclick="hidePanel('panel<s:property value="widgetID"/>'); return false;">Hide</a>
		<a id="panel<s:property value="widgetID"/>_show" href="#" class="panel_edit" style="display: none" onclick="showPanel('panel<s:property value="widgetID"/>'); return false;">Show</a>
	</div>
	<s:property value="caption" escape="false" />
</div>
<div id="panel<s:property value="widgetID"/>_content" class="panel_content" style="display: block;"><s:property value="content" escape="false" /></div>
<div class="panel_footer" onclick=""></div>
</div>
</div>
</s:iterator>

</div>
</s:iterator>

<br clear="all" />
</body>
</html>
