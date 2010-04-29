<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Home</title>
<s:include value="jquery.jsp"/>
<script src="js/FusionCharts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css" />
<script type="text/javascript">
function hidePanel(panel) {
	$('#'+panel+"_hide").hide();
	$('#'+panel+"_show").show();
	
	$('#'+panel+"_content").hide();
}

function showPanel(panel) {
	$('#'+panel+"_hide").show();
	$('#'+panel+"_show").hide();

	$('#'+panel+"_content").show();
}

$(function() {
	$('#rebranding').show('slow');
});
</script>
</head>
<body>

<s:if test="permissions.contractor">
	<s:include value="contractors/conHeader.jsp" />
</s:if>
<s:else>
	<h1>Welcome to PICS Online <span class="sub"><s:property value="account.name" /></span></h1>
</s:else>

<s:if test="showRebranding">
<div class="info" id="rebranding" style="display:none">
	<a href="http://www.picsauditing.com/about/2010-rebranding/" target="_BLANK">PICS has new Branding and Service Names</a>
</div>
</s:if>

<s:iterator value="columns">
	<div class="column" id="column<s:property value="key"/>" style="width: <s:property value="columnWidth"/>%">
		<s:iterator value="value">
			<div class="panel_placeholder" id="panel<s:property value="widgetID"/>_holder">
			<div class="panel" id="panel<s:property value="widgetID"/>">
			<div class="panel_header"><s:property value="caption" escape="false" />
				<pics:permission perm="DevelopmentEnvironment">
					<a href="<s:property value="url"/>" target="_BLANK">URL</a>
				</pics:permission>
			</div>
			<div id="panel<s:property value="widgetID"/>_content" class="panel_content" style="display: block;">
				<s:property value="content" escape="false" />
			</div>
			</div>
			</div>
		</s:iterator>
	</div>
</s:iterator>

<br clear="all" />
</body>
</html>
