<%@page language="java" errorPage="exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<script src="js/prototype.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/summaryreport.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" /> 
<script src="js/validate_contractor.js" type="text/javascript"></script>
	<script type="text/javascript"
		src="js/scriptaculous/scriptaculous.js?load=effects"></script>

<script type="text/javascript">
	function toggleVerify(auditId, questionId, catDataId ) {
		var comment = $F($('comment_' + questionId));
		var answer = $F($('answer_' + questionId)); 

		setThinking($('status_'+questionId));

		var pars = 'auditData.audit.id='+auditId+'&catDataID='+catDataId+'&auditData.question.id=' + questionId + '&auditData.answer=' + answer + '&auditData.comment=' + comment + '&toggleVerify=true';
		var myAjax = new Ajax.Updater('','AuditToggleVerifyAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function(transport) {
				var json = transport.responseText.evalJSON();
				
				$('verified_' + questionId).toggle();
				
				if( json.who ) {
					$('verify_' + questionId ).value = 'Unverify';
					$('verify_details_' + questionId).innerHTML = json.dateVerified + ' by ' + json.who;
				} else {
					$('verify_' + questionId ).value = 'Verify';
				}
				setApproveButton( json.percentVerified );
				stopThinking($('status_'+questionId));
			}
		});
		return false;
	}
	function toggleOSHAVerify( oshaId ) {
		var comment = $F($('comment_' + oshaId));
		var applicable = $F($('applicable_' + oshaId));
	
		setThinking($('status_'+oshaId));
	
		var pars = '';
		
		var manHours = $F($('manHours_' + oshaId)); 
		var fatalities = $F($('fatalities_' + oshaId)); 
		var lwc = $F($('lwc_' + oshaId)); 
		var lwd = $F($('lwd_' + oshaId)); 
		var imc = $F($('imc_' + oshaId)); 
		var rwc = $F($('rwc_' + oshaId)); 
		var tii = $F($('tii_' + oshaId)); 

		pars = 'id='+oshaId+'&osha.comment=' + comment +'&osha.applicable='+applicable+'&osha.manHours='+manHours+'&osha.fatalities='+fatalities+'&osha.lostWorkCases='+lwc+'&osha.lostWorkDays='+lwd+'&osha.injuryIllnessCases='+imc+'&osha.restrictedWorkCases='+rwc+'&osha.recordableTotal='+tii + '&button=toggleVerify';

		var myAjax = new Ajax.Updater('','AuditToggleOSHAVerifyAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function(transport) {

				var json = transport.responseText.evalJSON();
				
				$('verified_' + oshaId).toggle();
				
				if( json.who ) {
					$('verify_' + oshaId ).value = 'Unverify';
					$('verify_details_' + oshaId).innerHTML = json.dateVerified + ' by ' + json.who;
				} else {
					$('verify_' + oshaId ).value = 'Verify';
				}
				
				setApproveButton( json.percentVerified );
				stopThinking($('status_'+oshaId));
			}
		});
		return false;
	}
	function setComment(auditId, questionId, catDataId ) {
		var comment = $F($('comment_' + questionId));

		setThinking($('status_'+questionId));

		var pars = 'auditData.audit.id='+auditId+'&catDataID='+catDataId+'&auditData.question.id=' + questionId + '&auditData.comment=' + comment;
		var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function(transport) {
			
				new Effect.Highlight($('comment_' + questionId),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			
				stopThinking($('status_'+questionId));
			}
		});
		return false;
	}
	function setOSHAComment( oshaId ) {
		var comment = $F($('comment_' + oshaId));
	
		setThinking($('status_'+oshaId));
	
		var pars = 'id='+oshaId+'&osha.comment=' + comment + '&button=save';

		var myAjax = new Ajax.Updater('','AuditToggleOSHAVerifyAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function(transport) {
			
				new Effect.Highlight($('comment_' + oshaId),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});

				stopThinking($('status_'+oshaId));
			}
		});
		return false;
	}
	
	function setThinking( thinkElement ) {
		thinkElement.innerHTML="<img src='images/ajax_process.gif' />";
	}
	function stopThinking( thinkElement ) {
		thinkElement.innerHTML="";
	}
	
	
	function setApproveButton( newPercent ) {
	
		if( newPercent == 100 ) {
			$('approveButton1').show();
			$('approveButton2').show();
		} else {
			$('approveButton1').hide();
			$('approveButton2').hide();
		}
		return false;
	}
	
	 function openOsha(oshaId) {
		url = 'DownloadOsha.action?id='+oshaId;
		title = 'Osha300Logs';
		pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
        window.open(url,title,pars);
	 }
	 
	 function changeAuditStatus(id, auditStatus) {
		var pars = 'auditID='+id+'&auditStatus='+auditStatus;
		var myAjax = new Ajax.Updater('','ContractorAuditSaveAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete : function() {
			$('verification_audit').innerHTML='';
			refreshSummary();
			}
		});
		return false;
	 }

	 function refreshSummary() {
		pars = 'id=' + <s:property value="contractor.id"/>;

		$('verification_detail').innerHTML="<img src='images/ajax_process.gif' />";

		var divName = 'verification_detail';
		var myAjax = new Ajax.Updater(divName,'VerifyViewSummaryRefreshAjax.action', 
		{
			method: 'post', 
			parameters: pars,
			onComplete: function() {
				$('auditHeader').scrollTo();
			}
		});
		return false;
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
			<strong><s:property value="infoSection[69].answer" default="N/A"/></strong>
		</li>
		<li><label>Total Revenue:</label>
			<strong><s:property value="infoSection[1616].answer" default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>SIC:</label>
			<strong><s:property value="infoSection[55].answer" default="N/A"/></strong>
		</li>
		<li><label>NAICS:</label>
			<strong><s:property value="infoSection[57].answer" default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>Fatalities:</label>
			<strong><s:property value="infoSection[103].answer" default="N/A"/></strong>
		</li>
		<li><label>Citations:</label>
			<strong><s:property value="infoSection[104].answer"  default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>

<div id="verification_detail" style="line-height: 15px;">
<s:include value="verification_detail.jsp" />
</div>

<div id="verification_audit"></div>
<div class="clear"></div>
</body>
</html>