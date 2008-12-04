<%@page language="java" errorPage="exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" /> 
<script src="js/validate_contractor.js" type="text/javascript"></script>

<script type="text/javascript">
	function toggleVerify(auditId, questionId, catDataId ) {
		var comment = $F($('comment_' + questionId));
		var answer = $F($('answer_' + questionId)); 

		$('status_'+questionId).innerHTML="<img src='images/ajax_process.gif' />";

		var pars = 'auditData.audit.id='+auditId+'&catDataID='+catDataId+'&auditData.question.id=' + questionId + '&auditData.answer=' + answer + '&auditData.comment=' + comment;
		var divName = 'qid_' + questionId;
		var myAjax = new Ajax.Updater(divName,'AuditToggleVerifyAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function() {
			$('status_'+questionId).innerHTML='';
			}
		});
		return false;
	}
	function toggleOSHAVerify( oshaId ) {
		var comment = $F($('comment_' + oshaId));
		var applicable = $F($('applicable_' + oshaId));
	
		$('status_'+oshaId).innerHTML="<img src='images/ajax_process.gif' />";
	
		var pars = '';
		
		var manHours = $F($('manHours_' + oshaId)); 
		var fatalities = $F($('fatalities_' + oshaId)); 
		var lwc = $F($('lwc_' + oshaId)); 
		var lwd = $F($('lwd_' + oshaId)); 
		var imc = $F($('imc_' + oshaId)); 
		var rwc = $F($('rwc_' + oshaId)); 
		var tii = $F($('tii_' + oshaId)); 

		pars = 'id='+oshaId+'&osha.comment=' + comment +'&osha.applicable='+applicable+'&osha.manHours='+manHours+'&osha.fatalities='+fatalities+'&osha.lostWorkCases='+lwc+'&osha.lostWorkDays='+lwd+'&osha.injuryIllnessCases='+imc+'&osha.restrictedWorkCases='+rwc+'&osha.recordableTotal='+tii + '&button=toggleVerify';

		var divName = 'oid_' + oshaId;
		var myAjax = new Ajax.Updater(divName,'AuditToggleOSHAVerifyAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function() {
			$('status_'+oshaId).innerHTML='';
			}
		});
		return false;
	}
	
	 function openOsha(oshaId) {
		url = 'DownloadOsha.action?id='+oshaId;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
        window.open(url,title,pars);
	 }
</script>


</head>
<body >
<s:include value="conHeader.jsp" />

<div id="auditHeader">
	<fieldset>
	<ul>
		<li><label>CSR:</label>
			<strong><s:property value="contractor.auditor.name" /></strong>
		</li>
		<li><label>Risk Level:</label>
			<strong><s:property value="contractor.riskLevel" /></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label># of Employees:</label>
			<strong><s:property value="infoSection[69].answer"/></strong>
		</li>
		<li><label>Total Revenue:</label>
			<strong><s:property value="infoSection[1616].answer"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>SIC:</label>
			<strong><s:property value="infoSection[55].answer"/></strong>
		</li>
		<li><label>NAIC:</label>
			<strong><s:property value="infoSection[57].answer"/></strong>
		</li>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>

<div id="verification_detail">
<s:include value="verification_detail.jsp" />
</div>

<div id="verification_audit"></div>
<div class="clear"></div>
</body>
</html>