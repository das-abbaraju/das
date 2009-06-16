/**
 * This is used to enable the submit button when contractors have to resubmit their PQF
 * @param button
 * @param checked
 * @return
 */
function changeButton(button, checked) {
	$(button).disabled = !checked;
}

function verifyAnswer(questionid, answerid) {
	if (catDataID == 0) return;

	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid 
	+ '&auditData.id='+answerid+'&toggleVerify=true';
	
	var myAjax = new Ajax.Updater('','AuditToggleVerifyAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			if (transport.status == 200)

				$('verify_details_' + questionid).toggle();
				var json = transport.responseText.evalJSON();
				
				if( json.who ) {
					$('verifyButton_' + questionid ).value = 'Unverify';
					$('verify_details_' + questionid).innerHTML = 'Verified on ' + json.dateVerified + ' by ' + json.who;
				} else {
					$('verifyButton_' + questionid ).value = 'Verify';
				}

				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
		}
	});
}

function saveAnswer(divId, elm) {
	saveAnswerComment(divId, elm, null);
}

function saveAnswerComment(divId, answerElm, commentElm) {
	if (catDataID == 0) return;

	var	answerid = $(divId + '_answerID').value;
	var	questionid = $(divId + '_questionID').value;
	
	var divName = 'node_'+questionid;
	var pars = 'catDataID='+catDataID+'&auditData.audit.id='+auditID+'&mode='+mode;
	
	if (answerid > 0) {
		pars += '&auditData.id='+answerid;
	}
	else {
		pars += '&auditData.question.id=' + questionid;
	}	
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
					thevalue = ' ';
			}
		} else if( answerElm.type == 'text' || answerElm.type == 'radio' || answerElm.type == 'textarea') {
			thevalue = escape(answerElm.value);
		} else if (answerElm.type == 'select-one') {
			thevalue = answerElm.value;
		} else {
			alert('Unsupported type: ' + answerElm.type + ' ' +answerElm.value );
			return false;
		}
		pars += '&auditData.answer=' + thevalue;
	}
	
	if (commentElm != null)
		pars += '&auditData.comment=' + escape(commentElm.value);
	
	startThinking({div:'thinking_' + divId, message: "Saving Answer"});
	var myAjax = new Ajax.Updater(divName, 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		evalScripts: true,
		parameters: pars,
		onException: function(request, exception) {
			alert(exception.message);
		},
		onSuccess: function(transport) {
			if (transport.status == 200) {
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
			} else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
	return true;
}

function reloadQuestion(divId, answerid) {
	if (catDataID == 0) return;
	var	questionid = $(divId + '_questionID').value;

	var divName = 'node_'+questionid;
	var pars = 'button=reload&catDataID='+catDataID+'&auditData.audit.id='+auditID+'&mode='+mode;

	if (answerid > 0) {
		pars += '&auditData.id='+answerid;
	}
	else {
		pars += '&auditData.question.id=' + questionid;	
	}
	startThinking({div:'thinking_' + divId, message: "Saving Answer"});
	var myAjax = new Ajax.Updater(divName, 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onException: function(request, exception) {
			alert(exception.message);
		},
		onSuccess: function(transport) {
			new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
		}
	});
	return true;

}

function showFileUpload(answerid, questionid, divId) {
	url = 'AuditDataUpload.action?auditID='+auditID+'&answer.question.id=' + questionid+'&divId=' + divId;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function showCertUpload(conid, certid, caoID) {
	url = 'CertificateUpload.action?id='+conid+'&certID='+certid+'&caoID='+caoID;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function showCertificates(conID, caoID) {
	var pars = {
		id: conID,
		caoID: caoID
	};
	
	startThinking({div:'certificates'+caoID, message:' Searching for Certificates'});
	
	var myAjax = new Ajax.Updater('certificates'+caoID, 'ContractorCertificatesAjax.action', {
		method:'post',
		evalScripts:true,
		parameters: pars
	});

}

function saveCao(form, button, divName) {
	
	var pars = $(form).serialize(true);
	
	pars['button'] = button;
	
	if (typeof(divName) == 'undefined')
		divName = 'auditHeader' + pars['cao.id'];

	startThinking({div:'thinking_'+pars['cao.id'], message: "Saving Answer"});

	var myAjax = new Ajax.Updater('cao_'+pars['cao.id'], 'PolicySaveAjax.action', {
		method:'post',
		parameters: pars,
		onComplete: function(transport) {
			stopThinking({div:'thinking_'+pars['cao.id']});
			if (transport.status == 200) 
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
}

function saveCert(certID, caoID) {
	var form = "cao_form"+caoID;
	$(form)['certID'].value = certID;
	saveCao(form, 'Save', 'fileQuestion'+caoID);
}