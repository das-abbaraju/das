<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<head>
	<title><s:text name="PQFVerification.Page.Title" /></title>
	
	<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>"/>
	<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" /> 
	<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/summaryreport.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
	<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=<s:property value="version"/>" />
	
	<style type="text/css">
	small {
		font-size: smaller;
	}
	.buttonRow {
		float: right;
	}
	</style>
	
	<s:include value="../jquery.jsp"/>
	
	<script src="js/validate_contractor.js?v=<s:property value="version"/>" type="text/javascript"></script>
	<script src="js/notes.js?v=<s:property value="version"/>" type="text/javascript"></script>
	<script src="js/FusionCharts.js?v=${version}" type="text/javascript"></script>
	<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js?v=${version}"></script>
	
	<script type="text/javascript">
		function toggleVerify(auditId, questionId, answerId, categoryId) {
			var comment = $('#comment_' + questionId).val();
			var answerelm = $('#answer_' + questionId);
			var verifyBtn = $("#qid_" + questionId + " input#verify_" + questionId);
			
			startThinking({div:'status_'+questionId});
			
			var data= {
				'auditData.audit.id': auditId,
				'auditData.id': answerId,
				'categoryID': categoryId,
				'auditData.question.id': questionId,
				'auditData.comment': comment,
				'toggleVerify': true,
				'button' : 'verify'
			};
	
			if( answerelm != null ) {
				data['auditData.answer'] = answerelm.val();
			}
	
			verifyBtn.attr("disabled", "true");
			
			$.getJSON('AuditToggleVerifyAjax.action', data, function (json) {
					$('#verified_' + questionId).toggle();
	
					if (json.who) {
						$('#verify_' + questionId ).val('Unverify');
						$('#verify_details_' + questionId).text(json.dateVerified + ' by ' + json.who);
					} else {
						$('#verify_' + questionId ).val('Verify');
					}
					
					setApproveButton( json.percentVerified );
					
					stopThinking({div:'status_'+questionId});		
					startThinking({div: 'caoActionArea'});
					
					$('#caoActionArea').load('UpdateVerifyAuditAjax.action', {'auditID': auditId}, function(){verifyBtn.removeAttr("disabled");});
				}
			);
			
			return false;
		}
		
		function toggleOSHAVerify(oshaId, oshaType) {
		    var verify;
		    
			startThinking({
			    div: 'status_' + oshaId
		    });
			
			if ($('#verify_' + oshaId).val() == 'Verify') {
				verify = true;
			} else {
				verify = false;	
			}
			
			var osha_data = $('.osha-verification input').serializeArray();
			osha_data.push({
			    name: 'audit',
			    value: oshaId
			}, {
			    name: 'oshaType',
			    value: oshaType
			}, {
			    name: 'verify',
			    value: verify
			});
			
			$.getJSON('AuditToggleOSHAVerifyAjax.action', $.param(osha_data), function(json){
					stopThinking({div:'status_'+oshaId});
					
					$('#verified_' + oshaId).toggle();
					
					if (json.who) {
						$('#verify_' + oshaId ).val('Unverify');
						$('#verify_details_' + oshaId).text(json.dateVerified + ' by ' + json.who);
					} else {
						$('#verify_' + oshaId ).val('Verify');
					}
					
					setApproveButton(json.percentVerified);
					
					stopThinking({div:'status_'+oshaId});
					startThinking({div: 'caoActionArea'});
					
					$('#caoActionArea').load('UpdateVerifyAuditAjax.action', {'auditID': oshaId});
				}
			);
	
			return false;
		}
		
		function setComment(auditId, questionId, answerId, categoryId ) {
			startThinking({div:'status_'+questionId});
	
			var data= {
				'auditData.audit.id': auditId,
				'categoryID': categoryId,
				'auditData.id': answerId,
				'auditData.question.id': questionId,
				'auditData.comment': $('#comment_' + questionId).val(),
				'toggleVerify': true
			};
	
			$.post('AuditDataSaveAjax.action', data, function (text, status) {
				$('#comment_' + questionId).effect('highlight', {color: '#FFFF11'}, 1000);
				
				stopThinking({div:'status_'+questionId});
			});
			
			return false;
		}
	
		function setOSHAComment( oshaId ) {
			startThinking({div:'status_'+oshaId});
		
			var data= {
					'audit': oshaId,
					'comment': $('#comment_' + oshaId).val(),
					'oshaType': 'OSHA'
			};
	
			$.post('AuditToggleOSHAVerifyAjax!stampOshaComment.action', data, function() {
					$('#comment_' + oshaId).effect('highlight', {color: '#FFFF11'}, 1000);
					stopThinking({div:'status_'+oshaId});
				}
			);
			return false;
		}
		
		
		function setApproveButton( newPercent ) {
			if( newPercent == 100 )
				$('.approveButton').show();
			else
				$('.approveButton').hide();
			return false;
		}
		
		 function openOsha(oshaId, questionId) {
			//alert(oshaId);alert(questionId);
			url = 'DownloadAuditData.action?auditID=' + oshaId + '&auditData.question.id=' + questionId;
			title = 'Osha300Logs';
			pars = 'scrollbars=yes,resizable=yes,width=700,height=450';
	        window.open(url,title,pars);
		 }
		 
		function changeAuditStatus(id, auditStatus, button) {
			var normalArgs = 3;
			var caoIDs = new Array(arguments.length - normalArgs);
			for (i = normalArgs; i < arguments.length; i++) {
				caoIDs[i - normalArgs] = arguments[i];
			}
			
			var data= {
				auditID: id,
				status: auditStatus,
				caoIDs: caoIDs
			};
	
			$('#noteAjax').load('CaoSaveAjax!loadStatus.action', data, function(){
		        $.blockUI({ message:$('#noteAjax')}); 
				if($('#noteRequired').val()=='true'){
					$('#yesButton').addClass('disabled');
					$('#addToNotes').live('keyup', function(){
						if($(this).val()!='')
							$('#yesButton').removeClass('disabled');
						else
							$('#yesButton').addClass('disabled');
					});
				}
			    $('#yesButton').click(function(){
					if($(this).hasClass('disabled'))
						return false;
			        $.blockUI({message: 'Saving Status, please wait...'});
			        data.note = $('#addToNotes').val();
	
			        $.post('CaoSaveAjax!save.action', data, function() {
						$.unblockUI();
						$('#verification_audit').empty();
						refreshNoteCategory(<s:property value="id"/>, '<s:property value="noteCategory"/>');
						refreshAuditList();
					});
	
			    });		     
			    $('#noButton').click(function(){
			        $.unblockUI();
			        return false;
			    });
				if($('#nR').val()=='true')
		        	$.blockUI({ message:$('#noteAjax')});
				else  
					$('#yesButton').click();
			});
				
			return false;
		}
	
		function previewEmail() {
			var data= {id: <s:property value="contractor.id"/>};
			$('#emailTemplate').html("<img src='images/ajax_process.gif' />")
				.load('VerifyPreviewEmailAjax.action', data);
		}
	
		function copyComment(divId, commentID) {
			$('#'+commentID).val($('#'+divId).val()).focus().blur();
		}	
		$(function() {
			$('#chartEmrTrir').load('ChartEmrTrirAjax.action', {conID: <s:property value="contractor.id" />});
			$('#chartManHours').load('ChartManHoursAjax.action', {conID: <s:property value="contractor.id" />});
		});
	
		function refreshAuditList() {
			$('#verification_detail').load('VerifyViewAjax.action', { id: <s:property value="contractor.id" /> });
		}
	
	$(function(){
		$('.approveFlagChanges').click(function(){
			startThinking({div:'approve_flags', message:'Loading Flag Differences...'});
			$.scrollTo($('#approve_flags'),400);
			$.post('ContractorCron.action', {conID: <s:property value="contractor.id" />, steps: 'Flag', button: 'Run'}, function(){
				$('#approve_flags').load('ContractorFlagChangesAjax.action', {conID: <s:property value="contractor.id" />}, function(response, status, xhr){
					if(status == "error")
						$('#approve_flags').html($('<div>').addClass('error').append('Flags changes did not load, please try again.'));		
				});
			});
		});
		$('.saveFlag').live('click', function(){
			if($('.coFlag:checked').length){
				$('#approve_flags').load('ContractorFlagChangesAjax.action?'+$('.coFlag:checked').serialize(), 
						{conID: <s:property value="contractor.id" />, button: 'save'}, 
						function(response, status, xhr){
						if(status == "error")
							$('#approve_flags').html($('<div>').addClass('error').append('Flags changes did not load, please try again.'));		
				});
			} else
				alert('No flags selected');
		});
		$('.cancelFlags').live('click', function(){
			$('#approve_flags').html('');
		});
	});
	</script>
</head>
<body>
	<s:include value="conHeader.jsp" />
	
	<div id="auditHeader" class="auditHeader">
		<fieldset>
			<ul>
				<li>
					<label>CSR:</label>
					<strong><s:property value="contractor.auditor.name" /></strong>
				</li>
				<li>
					<label>Safety Assessment:</label>
					<s:if test="contractor.safetyRisk != null">
						<strong><s:text name="%{contractor.safetyRisk.i18nKey}" /></strong>
					</s:if>
					<s:else>
						<strong><s:text name="ContractorAccount.safetyRisk.missing" /></strong>
					</s:else>
				</li>
				<s:if test="contractor.materialSupplier && contractor.productRisk != null">
					<li>
						<label>Product Assessment:</label>
						<strong><s:text name="%{contractor.productRisk.i18nKey}" /></strong>
					</li>
				</s:if>
				<li>
					<label>Seasonal:</label>
					<strong><s:property value="infoSection[71].answer" default="N/A"/></strong>
				</li>
				<li>
					<label>Full-Time:</label>
					<strong><s:property value="infoSection[69].answer" default="N/A"/></strong>
				</li>
				<li>
					<label>Total Revenue:</label>
					<strong><s:property value="infoSection[1616].answer" default="N/A"/></strong>
				</li>
			</ul>
		</fieldset>
		<fieldset>
			<ul>
				<li>
					<label>NAICS:</label>
					<strong><s:property value="infoSection[57].answer" default="N/A"/></strong>
				</li>
				<li>
					<label>Fatalities:</label>
					<strong><s:property value="infoSection[103].answer" default="N/A"/></strong>
				</li>
				<li>
					<label>Citations:</label>
					<strong><s:property value="infoSection[104].answer"  default="N/A"/></strong>
				</li>
			</ul>
		</fieldset>
		<fieldset>
			<ul>
				<li>
					<label>EMR Origin:</label>
					<strong><s:property value="infoSection[123].answer" default="N/A"/></strong>
				</li>
				<li>
					<label>EMR Anniv.:</label>
					<strong><s:property value="infoSection[124].answer"  default="N/A"/></strong>
				</li>
				<li>
					<label>EMR Rate Type:</label>
					<strong><s:property value="infoSection[125].answer"  default="N/A"/></strong>
				</li>
			</ul>
		</fieldset>
		<div class="clear"></div>
	</div>
	
	<div class="buttonRow">
		<button class="approveFlagChanges picsbutton">Check Flag Changes</button>	
	</div>
	
	<div id="verification_detail" style="line-height: 15px;">
		<s:include value="verification_detail.jsp" />
	</div>
	<br />
	
	<s:if test="oshasUS.size > 0 || emrs.size > 0">
		<table style="width: 100%;">
			<tbody>
				<tr>
					<td><div id="chartEmrTrir" align="center"></div></td>
					<td><div id="chartManHours" align="center"></div></td>
				</tr>
			</tbody>
		</table>
	</s:if>
	
	<div id="approve_flags"></div>
	<div id="verification_audit"></div>
	<div id="noteAjax" class="blockDialog"></div>
	<br/>
	<br/>
	<div id="emailTemplate">
		<button name=button class="picsbutton left" onclick="previewEmail();">Preview Email</button>
		<form action="VerifyView.action?id=${id}" method="POST">
			<s:submit cssClass="picsbutton positive left" method="sendEmail" value="Send Email" />
		</form>
		<a onClick="window.open('NoteEditor.action?id=<s:property value="id"/>&note=0&mode=edit&embedded=0&note.noteCategory=Audits&note.canContractorView=true','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=770,height=550'); return false;"
			href="#" title="opens in new window" class="picsbutton positive">Add Note</a>
	</div>
	<br/>
	
	<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>
</body>