<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Wizard</title>

<script src="js/prototype.js" type="text/javascript"></script>
<script src="js/scriptaculous/scriptaculous.js?load=effects" type="text/javascript"></script>
<script src="js/CalendarPopup.js" type="text/javascript"></script>
<script src="js/Search.js" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/calendar.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css" />

<script type="text/javascript">
function selectList(listType) {
	$('filter_recipients').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' />Loading filters for "+listType+"s";
	$('report_data').innerHTML = "";
	var pars = "listType="+listType;
	var myAjax = new Ajax.Updater('filter_recipients','ReportFilterAjax.action', 
	{
		method: 'get', 
		parameters: pars
	});
}
</script>
</head>
<body>
<img src="images/beta.jpg" align="right" title="This is a new feature. Please send us your feedback or suggestions." />

<h1>Email Wizard</h1>

<!--Step 1-->
<div class="target_sample" id="select_recipient">
	<div class="instructions">Select a list type below to begin generating your emails</div>
	<span class="step_number">1</span>
	<div class="target_recipient" onclick="selectList('Contractor');">
		<h2>Contractors</h2>
		<ul class="samples" title="Sample emails you might send to contractors">
			<li><span>"Notification of Annual Contractor Training Meeting"</span> to all contractors</li>
			<li><span>"Invitation to Contractor Improvement Program"</span> to contractors with an Amber Flag color</li>
			<li><span>"Crane Safety Policy Updates"</span> to all contractors who do "Cranes &amp; Rigging"</li>
		</ul>
	
		<span class="footer_instruction">* One email per contractor</span>
		<span class="shadow_sample"></span>
	</div>
	
	<div class="target_recipient" onclick="selectList('User');">
		<h2>Users</h2>
		<ul class="samples" title="Sample emails you might send to users">
			<li><span>"Login Instructions"</span> to all users who haven't logged in yet</li>
			<li><span>"New Policy Change"</span> to all users</li>
		</ul>
		
		<span class="footer_instruction">* One email per end user</span>
		<span class="shadow_sample"></span>
	</div>
	
	<div class="target_recipient" onclick="selectList('Audit');">
		<h2>Contractors by Audit</h2>
		<ul class="samples" title="Sample emails you might send to contractors regarding one of their audits">
			<li><span>"Thank you for completing your Audit"</span> to contractors who have completed a Desktop or Office Audit this month</li>
			<li><span>"Reminder to complete PQF"</span> to contractors with an outstanding PQF audit to complete</li>
			<li><span>"Reminder to close requirements"</span> to contractors with open requirements on a Desktop, Office, or Field audit</li>
		</ul>
		
		<span class="footer_instruction">* One email per audit</span>
		<span class="shadow_sample"></span>
	</div>
	<div class="clear"></div>
</div>

<!--Step 2-->
<div class="target_sample">
<div class="instructions">Select a list type below to begin generating your emails <span id="email_previousSTP"><a href="#">Previous step</a></span></div>
<span class="step_number">2</span>
<div id="filter_recipients"></div>
</div>

<!--Step 3-->
<div class="target_sample">
<div class="instructions">Select a list type below to begin generating your emails <span id="email_previousSTP"><a href="#">Previous step</a></span></div>
<span class="step_number">3</span>
<div id="report_data"></div>
</div>
</div>

</div>
</div>

</body>
</html>