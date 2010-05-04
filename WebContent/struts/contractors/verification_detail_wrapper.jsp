<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/scrollTo/jquery.scrollTo-min.js"></script>
<script src="js/validate_contractor.js?v=<s:property value="version"/>" type="text/javascript"></script>

<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" /> 
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/summaryreport.css?v=<s:property value="version"/>" />

<script type="text/javascript">
	function toggleVerify(auditId, questionId, answerId) {
		var comment = $('#comment_' + questionId).val();
		var answerelm = $('#answer_' + questionId);
		
		startThinking({div:'status_'+questionId});
		var data= {
				'auditData.audit.id': auditId,
				'auditData.id': answerId,
				'catDataID': -1,
				'auditData.question.id': questionId,
				'auditData.comment': comment,
				'toggleVerify': true
			};

		if( answerelm != null ) {
			data['auditData.answer'] = answerelm.val();
		}

		$.getJSON('AuditToggleVerifyAjax.action', data, function(json){
				$('#verified_' + questionId).toggle();
				if (json.who) {
					$('#verify_' + questionId ).val('Unverify');
					$('#verify_details_' + questionId).text(json.dateVerified + ' by ' + json.who);
				} else {
					$('#verify_' + questionId ).val('Verify');
				}
				setApproveButton( json.percentVerified );
				stopThinking({div:'status_'+questionId});		
			}
		);
		return false;
	}
	
	function toggleOSHAVerify( oshaId ) {
	
		startThinking({div:'status_'+oshaId});
		
		var data= {
				id: oshaId,
				'osha.comment': $('#comment_' + oshaId).val(),
				'osha.manHours': $('#manHours_' + oshaId).val(),
				'osha.fatalities': $('#fatalities_' + oshaId).val(),
				'osha.lostWorkCases': $('#lwc_' + oshaId).val(),
				'osha.lostWorkDays': $('#lwd_' + oshaId).val(),
				'osha.injuryIllnessCases': $('#imc_' + oshaId).val(),
				'osha.restrictedWorkCases': $('#rwc_' + oshaId).val(),
				'osha.modifiedWorkDay': $('#rwd_' + oshaId).val(),
				button: 'toggleVerify'
		};

		$.getJSON('AuditToggleOSHAVerifyAjax.action', data, function(json) {
				$('#verified_' + oshaId).toggle();
				if( json.who ) {
					$('#verify_' + oshaId ).val('Unverify');
					$('#verify_details_' + oshaId).text(json.dateVerified + ' by ' + json.who);
				} else {
					$('#verify_' + oshaId ).val('Verify');
				}
				
				setApproveButton( json.percentVerified );
				stopThinking({div:'status_'+oshaId});
			}
		);
		return false;
	}
	
	function setComment(auditId, questionId, answerId ) {
		startThinking({div:'status_'+questionId});

		var data= {
			'auditData.audit.id': auditId,
			'catDataID': -1,
			'auditData.id': answerId,
			'auditData.question.id': questionId,
			'auditData.comment': $('#comment_' + questionId).val()
		};

		$.post('AuditDataSaveAjax.action', data, function(text, status){
				$('#comment_' + questionId).effect('highlight', {color: '#FFFF11'}, 1000);
				stopThinking({div:'status_'+questionId});
			}
		);
		return false;
	}

	function setOSHAComment( oshaId ) {
		startThinking({div:'status_'+oshaId});
	
		var data= {
				id: oshaId,
				'osha.comment': $('#comment_' + oshaId).val(),
				button: 'save'
		};

		$.post('AuditToggleOSHAVerifyAjax.action', data, function() {
				$('#comment_' + oshaId).effect('highlight', {color: '#FFFF11'}, 1000);
				stopThinking({div:'status_'+oshaId});
			}
		);
		return false;
	}
	
	
	function setApproveButton( newPercent ) {
		if( newPercent == 100 ) {
			$('#approveButton1').show();
			$('#approveButton2').show();
		} else {
			$('#approveButton1').hide();
			$('#approveButton2').hide();
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
		var data= {
			auditID: id,
			auditStatus: auditStatus
		};
		$.post('ContractorAuditSaveAjax.action', data, function() {
				$('#verification_audit').empty();
				//$('#auditHeader').scrollTo();
				refreshNoteCategory(<s:property value="id"/>, '<s:property value="noteCategory"/>');
			}
		);
		return false;
	 }

	function previewEmail() {
		var data= {id: <s:property value="contractor.id"/>};
		$('#emailTemplate').html("<img src='images/ajax_process.gif' />")
			.load('VerifyPreviewEmailAjax.action', data);
	}
	 
	function sendEmail() {
		var data= {id: <s:property value="contractor.id"/>};
		if($('#body') != null && $('#subject') != null) {
			data.emailBody = $('#body').val();
			data.emailSubject = $('#subject').val();
		}
		$('#emailStatus').load('VerifySendEmailAjax.action', data, function() {
				$(this).effect('highlight', {color: '#FFFF11'}, 1000);
				refreshNoteCategory(<s:property value="id"/>, '<s:property value="noteCategory"/>');
			}
		);
	}

	function copyComment(divId, commentID) {
		$('#'+commentID).val($('#'+divId).val()).focus().blur();
	}	
	 
</script>

</head>
<body >
<s:include value="conHeader.jsp" />

<div id="auditHeader" class="auditHeader">
	<fieldset>
	<ul>
		<li><label>CSR:</label>
			<strong><s:property value="contractor.auditor.name" /></strong>
		</li>
		<li><label>Risk Level:</label>
			<strong><s:property value="contractor.riskLevel" /></strong>
		</li>
		<li><label>Seasonal:</label>
			<strong><s:property value="infoSection[71].answer" default="N/A"/></strong>
		</li>
		<li><label>Full-Time:</label>
			<strong><s:property value="infoSection[69].answer" default="N/A"/></strong>
		</li>
		<li><label>Total Revenue:</label>
			<strong><s:property value="infoSection[1616].answer" default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>NAICS:</label>
			<strong><s:property value="infoSection[57].answer" default="N/A"/></strong>
		</li>
		<li><label>Fatalities:</label>
			<strong><s:property value="infoSection[103].answer" default="N/A"/></strong>
		</li>
		<li><label>Citations:</label>
			<strong><s:property value="infoSection[104].answer"  default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<fieldset>
	<ul>
		<li><label>EMR Origin:</label>
			<strong><s:property value="infoSection[123].answer" default="N/A"/></strong>
		</li>
		<li><label>EMR Anniv.:</label>
			<strong><s:property value="infoSection[124].answer"  default="N/A"/></strong>
		</li>
		<li><label>EMR Rate Type:</label>
			<strong><s:property value="infoSection[125].answer"  default="N/A"/></strong>
		</li>
	</ul>
	</fieldset>
	<div class="clear"></div>
</div>

<div id="verification_detail" style="line-height: 15px;">
<s:include value="verification_detail.jsp" />
</div>

<div id="verification_audit"></div>
<br clear="all"/>
<br clear="all"/>
<div class="clear"></div>
<table>
	<tr class="blueMain">
		<td><div>
				<button name="button" onclick="previewEmail();">Preview Email</button>
				<button class="picsbutton positive" name="button" onclick="sendEmail();">Send Email</button>
			</div>
		</td>
	</tr>
	<tr>
		<td id="emailTemplate"></td>
	</tr>
	<tr>
		<td><div id="emailStatus" style="font-style: italic; color: red;"></div></td>
	</tr>
</table>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>