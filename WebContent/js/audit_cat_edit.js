/**
 * This is used to enable the submit button when contractors have to resubmit their PQF
 * @param button
 * @param checked
 * @return
 */
function changeButton(button, checked) {
	$('#'+button).disabled = !checked;
}

function verifyAnswer(questionid, answerid) {
	if (catDataID == 0) return;
	
	var data = {
			'auditData.audit.id':auditID,
			'catDataID':catDataID,
			'auditData.question.id':questionid,
			'auditData.id':answerid,
			'toggleVerify':true
	};
	
	$.getJSON('AuditToggleVerifyAjax.action', data, function(json, status) {
		$('#verify_details_' + questionid).toggle();
		
		if( json.who ) {
			$('#verifyButton_' + questionid ).val('Unverify');
			$('#verify_details_' + questionid).html('Verified on ' + json.dateVerified + ' by ' + json.who);
		} else {
			$('#verifyButton_' + questionid ).val('Verify');
		}
	});
}

function saveAnswer(divId, elm) {
	saveAnswerComment(divId, elm, null);
}

function saveAnswerComment(divId, answerElm, commentElm) {
	if (catDataID == 0) return;

	var	questionid = $('#'+divId + '_questionID').val();
	
	var divName = '#node_'+questionid;
	var data = {
			'catDataID':catDataID,
			'auditData.audit.id':auditID,
			'auditData.question.id':questionid,
			'mode':mode
	};
	
	if (answerElm != null) {
		var thevalue = '';
		if( answerElm.type == 'checkbox') {
			if(
				( answerElm.name == ('answer' + divId + '_C') || answerElm.name == ('answer' + divId + '_S') )
				&& ( document.getElementById('answer' + divId + '_C') != undefined 
				&& document.getElementById('answer' + divId + '_S') != undefined)  
				) {
					
					if( document.getElementById('answer' + divId + '_C').checked )
					{
						thevalue = thevalue + 'C';
					}
									
					if( document.getElementById('answer' + divId + '_S').checked )
					{
						thevalue = thevalue + 'S';
					}
			}
			else {
				if (answerElm.checked)
					thevalue = 'X';
				else
					thevalue = '';
			}
		} else if( answerElm.type == 'text' || answerElm.type == 'radio' || answerElm.type == 'textarea') {
			thevalue = answerElm.value;
		} else if (answerElm.type == 'select-one') {
			thevalue = answerElm.value;
		} else {
			alert('Unsupported type: ' + answerElm.type + ' ' +answerElm.value );
			return false;
		}
		data['auditData.answer'] = thevalue;
	}
	
	if (commentElm != null) {
		data['auditData.comment'] = commentElm.val();
	}
	
	startThinking({div:'thinking_' + divId, message: "Saving Answer"});
	var dependents = $(divName).find(".dependentQuestions").html();
	var depends = dependents.split(",");
	var x=0;
	for (x=1; x < depends.length; x++) {
		startThinking({div:'thinking_' + depends[x], message: "Recalculating Requirement"});
		$('#node_'+depends[x]).find(":input").attr("disabled", true);
	}
	
	$(divName).load('AuditDataSaveAjax.action', data, function(response, status) {
			if (status=='success') {
				$(this).effect('highlight', {color: '#FFFF11'}, 1000);
				for (x=1; x < depends.length; x++) {
					reloadQuestion(depends[x]);
				}
			} else {
				alert('Failed to save answer ');
			}
		});
	return true;
}

function reloadQuestion(divId) {
	if (catDataID == 0) return;
	var	questionid = $('#'+divId + '_questionID').val();
	
	var divName = '#node_'+questionid;
	var data = {
			button:'reload',
			catDataID:catDataID,
			'auditData.audit.id':auditID,
			'auditData.question.id':questionid,
			'mode':mode
	};

	startThinking({div:'thinking_' + divId, message: "Saving Answer"});
	$(divName).load('AuditDataSaveAjax.action',data);
	return true;
}

function showFileUpload(answerid, questionid, divId) {
	url = 'AuditDataUpload.action?auditID='+auditID+'&answer.question.id=' + questionid+'&divId=' + divId;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function showCertUpload(certid, questionID) {
	url = 'CertificateUpload.action?id='+conid
		+ ((certID !== undefined && certID > 0) ? '&certID='+certid : '')
		+ ((questionID !== undefined && questionID > 0) ? '&questionID='+questionID : '')
		+ '&auditID='+auditID;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function showCertificates(conID, caoID, button, catDataID) {
	var data = {
		id: conID,
		caoID: caoID
	};
	
	if (button != undefined)
		data['button'] = button;
	
	if (catDataID != undefined)
		data['catDataID'] = catDataID;
	
	startThinking({div:'certificates'+caoID, message:' Searching for Certificates'});
	
	$('#certificates'+caoID).load('ContractorCertificatesAjax.action', data);
}

function saveCao(form, button, divName) {
	var data = {};
	$.each($(form).serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	
	data['button'] = button;
	if (divName === undefined)
		divName = '#auditHeader'+data['cao.id'];

	startThinking({div:'thinking_'+data['cao.id'], message: "Saving Answer"});
	$('#cao_'+data['cao.id']).load('PolicySaveAjax.action', data, function(response, status){
		stopThinking({div:'thinking_'+data['cao.id']});
		if (status=='success')
			$(divName).effect('highlight', {color: '#FFFF11'}, 1000);
		else
			alert('failed to save answer');
	});
}

function saveCert(certID, caoID) {
	var form = "#cao_form"+caoID;
	$("#cao_form"+caoID).find('[name="certID"]').val(certID);
	saveCao(form, 'Save', '#fileQuestion'+caoID);
}

function savePolicy(form, button, divName, dataID, catDataID) {
	var data = {};
	$.each($(form).serializeArray(), function(i, e) {
		data[e.name] = e.value;
	});
	
	data['button'] = button;
	
	if (dataID != undefined)
		data['dataID'] = dataID;
	
	if (catDataID != undefined)
		data['catDataID'] = catDataID;
	
	if (divName === undefined)
		divName = '#auditHeader'+data['question.id'];
	
	$('#cert_'+data['question.id']).load('PolicySaveAjax.action', data, function(response, status){
		if (status=='success')
			$(divName).effect('highlight', {color: '#FFFF11'}, 1000);
		else
			alert('failed to save answer');
	});
}

function saveCertQ(certID, questionID, button, dataID, catDataID) {
	var form = "#cert_form"+questionID;
	$(form).find('[name="certID"]').val(certID);
	savePolicy(form, (button == undefined || button == '') ? 'Save' : button, '#fileQuestion'+questionID, dataID, catDataID);
}