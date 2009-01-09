function changeAnswer(answerid, questionid, questionType) {
	var elm;
	
	if (questionType == 'Radio' || questionType == 'Yes/No' || questionType == 'Yes/No/NA') {
		var selector = "input[type=radio][name='verifiedAnswer_"+questionid+"'][value='"+value+"']";
		elm = $$(selector)[0];
	}
	else if(questionType == 'Check Box') {
		elm = $('verifiedBox_'+questionid).checked;
	}
	else {		
		elm = $('verifiedBox_'+questionid);
	}
}

function saveComment(answerid, questionid, parentid, elm) {
	if (catDataID == 0) return;

	startThinking({div:'thinking_' + questionid});

	var comment = $F($('comments_' + questionid));
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid + '&auditData.parentAnswer.id=' + parentid + '&auditData.comment=' + comment;
	var divId = questionid + '' + parentid;
	var divName = 'status_'+divId;
	
	var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			else
				alert("Did not get a response from server, may not have saved data" + transport.statusText + transport.responseText);
				
				stopThinking({div:'thinking_' + questionid});
		}
	});
}

function saveAnswer(divId, elm) {
	if (catDataID == 0) return;
	
	var	answerid = $(divId + '_answerID').value;
	var	questionid = $(divId + '_questionID').value;
	var	parentid = $(divId + '_parentAnswerID').value;
	var	allowMultiple = $(divId + '_multiple').value;

	var divName = 'node_'+parentid+'_'+questionid;
	var pars = 'catDataID='+catDataID+'&auditData.audit.id='+auditID;

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
	
	var thevalue = '';
	if( elm.type == 'checkbox') {
		if(
			( elm.name == ('question_' + divId + '_C') || elm.name == ('question_' + divId + '_S') )
			&& ( document.getElementById('question_' + divId + '_C') != undefined 
			&& document.getElementById('question_' + divId + '_S') != undefined)  
			) {

				if( document.getElementById('question_' + divId + '_C').checked )
				{
					thevalue = thevalue + 'C';
				}
								
				if( document.getElementById('question_' + divId + '_S').checked )
				{
					thevalue = thevalue + 'S';
				}
		}
		else {
			if (elm.checked)
				thevalue = 'X';
			else
				thevalue = ' ';
		}
	} else if( elm.type == 'text' || elm.type == 'radio' || elm.type == 'textarea') {
		thevalue = escape(elm.value);
	} else if (elm.type == 'select-one') {
		thevalue = elm.value;
	} else {
		alert('Unsupported type: ' + elm.type + ' ' +elm.value );
		return false;
	}
	pars += '&auditData.answer=' + thevalue;
	
	startThinking({div:'thinking_' + divId, message: "Saving Answer"});
	
	var myAjax = new Ajax.Updater(divName, 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onException: function(request, exception) {
			alert(exception.message);
		},
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
	return true;
}

function addTuple(questionid) {
	var elm = $('answerq'+questionid);
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

function showFileUpload(answerid, questionid, parentid) {
	if (parentid == null || parentid == '')
		parentid = 0;
		
	url = 'AuditDataUpload.action?auditID='+auditID+'&answer.question.id=' + questionid+'&answer.parentAnswer.id=' + parentid;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function reloadQuestion(answerid, questionid, parentid) {
	if (parentid == null || parentid == '')
		parentid = 0;
		
	var pars = 'auditID='+auditID+'&answer.question.id=' + questionid+'&answer.parentAnswer.id=' + parentid;
	var divId = questionid + '' + parentid;
	var divName = 'td_answer_'+divId;
	$(divName).innerHTML="<img src='images/ajax_process.gif' />";
	
	var myAjax = new Ajax.Updater(divName,'ReloadQuestionAjax.action',
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#F3F3F3'});
		}
	});
}