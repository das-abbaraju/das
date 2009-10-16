<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Email Wizard</title>

<s:include value="../reports/reportHeader.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/emailwizard.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
jQuery.noConflict();
</script>
<script type="text/javascript">
function selectList(listType) {
	jQuery('#filter_recipients').show();
	jQuery('#filter_recipients').html("<img src='images/ajax_process2.gif' width='48' height='48' />Loading filters for "+listType+"s");
	jQuery('#report_data').empty();
	jQuery('#target_recipients').hide('normal');
	jQuery('#back_to_step1').show();
	jQuery('#filter_recipients').load('ReportFilterAjax.action', {listType:listType}, 
			function(response, status){ 
				if (status=='success') {
					jQuery('#selectedListType').html(listType + " List").fadeIn('normal');
				}
			}
	);
}
function showLists() {
	jQuery('#back_to_step1').hide();
	jQuery('#target_recipients').show('normal');
	jQuery('#filter_recipients').fadeOut('normal');
	jQuery('#report_data').hide('normal');
	jQuery('#selectedListType').hide();

	jQuery.ajax({
		url: 'ReportFilterAjax.action',
		data: {
			clear: true
		}
	});
}
<s:if test="type != null">
jQuery(function(){
	selectList('<s:property value="type"/>');
});
</s:if>
</script>
</head>
<body>

<h1>Email Wizard</h1>
<div id="caldiv2"><!-- This is here so we don't get a JS error when clicking on the body --></div>

<s:if test="listSize > 0">
	<div id="alert">You already have <s:property value="listSize"/> records in your mailing list. 
		<a href="MassMailer.action">Skip to Step 4</a></div>
</s:if>

<div class="clear"></div>
<!--Step 1-->
<div class="target_sample">
	<div class="instructions">Choose a method to find your recipients</div>
	<span class="step_number">1</span>
	<div id="back_to_step1" style="display: none" onclick="showLists();return false;" class="back_to_step" title="Start Over">Start Over</div>
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
	<div id="filter_recipients"></div>
</div>

<!--Step 3-->
<div class="target_sample">
	<div class="instructions">Review your recipient list</div>
	<span class="step_number">3</span>
	<div id="report_data"></div>
</div>

<span id="email_previousSTP"><a href="#">Previous step</a></span>
</body>
</html>