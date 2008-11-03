<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Wizard</title>

<s:include value="../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css" />

<script type="text/javascript">
function selectList(listType) {
	$('filter_recipients').show();
	$('filter_recipients').innerHTML = "<img src='images/ajax_process2.gif' width='48' height='48' />Loading filters for "+listType+"s";
	$('report_data').innerHTML = "";
	var pars = "listType="+listType;
	var myAjax = new Ajax.Updater('filter_recipients','ReportFilterAjax.action', 
	{
		method: 'get', 
		parameters: pars,
		onSuccess: function(transport) {
			Effect.Shrink('target_recipients');
			$('back_to_step1').show();
			$('selectedListType').innerHTML = listType + " List";
		}
	});
}
function showLists() {
	Effect.Grow('target_recipients');
	$('back_to_step1').hide();
	$('filter_recipients').innerHTML = "<div id='caldiv2'><!-- This is here so we don't get a JS error when clicking on the body --></div>";
	$('report_data').innerHTML = "";
	$('selectedListType').innerHTML = "";
	
	var myAjax = new Ajax.Updater('','ReportFilterAjax.action?clear=true');
}
</script>
</head>
<body <s:if test="type != null">onload="selectList('<s:property value="type"/>')"</s:if> >
<img src="images/beta.jpg" width="98" height="100" style="float: right;" title="This is a new feature. Please send us your feedback or suggestions." />

<h1>Email Wizard</h1>

<div class="clear"></div>
<!--Step 1-->
<div class="target_sample">
	<div class="instructions">Choose a method to find your recipients</div>
	<span class="step_number">1</span>
	<div id="back_to_step1" style="display: none" onclick="showLists();" class="back_to_step" title="Start Over">Start Over</div>
	<h2 id="selectedListType" style="text-align: center"></h2>
	<div id="target_recipients">
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
</div>

<!--Step 2-->
<div class="target_sample">
	<div class="instructions">Find recipients by specifying search criteria</div>
	<span class="step_number">2</span>
	<div id="filter_recipients">
		<div id="caldiv2"><!-- This is here so we don't get a JS error when clicking on the body --></div>
	</div>
</div>

<!--Step 3-->
<div class="target_sample">
	<div class="instructions">Review your recipient list</div>
	<span class="step_number">3</span>
	<div id="report_data"></div>
	</div>
</div>

</div>
</div>
<span id="email_previousSTP"><a href="#">Previous step</a></span>
</body>
</html>