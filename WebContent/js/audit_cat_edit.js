function verifyAnswer(questionid, answerid, parentid) {
	if (catDataID == 0) return;
	if (parentid == null || parentid == '')
		parentid = 0;
	
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid 
	+ '&auditData.id='+answerid+'&auditData.parentAnswer.id='+parentid+'&toggleVerify=true';
	
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
	var	parentid = $(divId + '_parentAnswerID').value;
	var	allowMultiple = $(divId + '_multiple').value;
	
	var divName = 'node_'+parentid+'_'+questionid;
	var pars = 'catDataID='+catDataID+'&auditData.audit.id='+auditID+'&mode='+mode;
	
	if (answerid > 0) {
		pars += '&auditData.id='+answerid;
		if (allowMultiple == 'true')
			divName = 'node_'+answerid+'_'+questionid;
	} else {
		if (allowMultiple == 'true') {
			// This is adding a new tuple, we may just want to call addTuple(questionid)
			return;
		}
		pars += '&auditData.question.id=' + questionid;
		if (parentid > 0)
			pars += '&auditData.parentAnswer.id=' + parentid;
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
	var	parentid = $(divId + '_parentAnswerID').value;
	var	allowMultiple = $(divId + '_multiple').value;

	var divName = 'node_'+parentid+'_'+questionid;
	var pars = 'button=reload&catDataID='+catDataID+'&auditData.audit.id='+auditID+'&mode='+mode;

	if (answerid > 0) {
		pars += '&auditData.id='+answerid;
		if (allowMultiple == 'true')
			divName = 'node_'+answerid+'_'+questionid;
	} else {
		pars += '&auditData.question.id=' + questionid;
		if (parentid > 0)
			pars += '&auditData.parentAnswer.id=' + parentid;
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

function addTuple(questionid, elm) {
	var thevalue = escape(elm.value);
	startThinking({div:'thinking_q' + questionid, message: "Adding Answer Group"});

	var pars = 'button=addTuple&catDataID='+catDataID+'&auditData.audit.id='+auditID+'&auditData.question.id=' + questionid + '&auditData.answer=' + thevalue;
	var myAjax = new Ajax.Updater('tuple_' + questionid, 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onException: function(request, exception) {
			alert(exception.message);
		},
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($('tuple_' + questionid),{duration: 0.75, startcolor:'#FFFF11'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
}

function removeTuple(answerid) {
	var divName = 'node_tuple_'+answerid;
	
	startThinking({div:'thinking_a' + answerid, message: "Removing Answer Group"});
	
	var pars = 'button=removeTuple&catDataID='+catDataID+'&auditData.id=' + answerid;
	var myAjax = new Ajax.Updater(divName, 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars
	});
}

function showFileUpload(answerid, questionid, parentid, divId) {
	if (parentid == null || parentid == '')
		parentid = 0;
	
	url = 'AuditDataUpload.action?auditID='+auditID+'&answer.question.id=' + questionid+'&answer.parentAnswer.id=' + parentid + '&divId=' + divId;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function showCertUpload(conid, certid) {
	url = 'CertificateUpload.action?id='+conid+'&certID='+certid;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function saveCao(field, value, caoID, divName) {
	
	var pars = {
		auditID : auditID,
		'cao.id': caoID,
		button  : 'Save'
	};
	
	pars['cao.'+field] = value;

	var myAjax = new Ajax.Updater('cao_layer', 'PolicySaveAjax.action', {
		method:'post',
		parameters: pars,
		onComplete: function(transport) {
			if (transport.status == 200) 
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
}

function saveCaoStatus(caoID, button, useNotes) {
	var pars = {
		auditID : auditID,
		'cao.id': caoID,
		button  : button
	};
	
	if (useNotes)
		pars['cao.notes'] = $('notes_'+caoID).value;
	
	var myAjax = new Ajax.Updater('cao_layer', 'PolicySaveAjax.action', {
		method:'post',
		parameters: pars,
		onComplete: function(transport) {
			if (transport.status == 200) 
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});

}