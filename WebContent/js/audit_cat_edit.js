function changeAnswer(questionid, questionType) {
	var elm = 'answer_'+questionid;
	var value = $F($(elm));
	if (questionType == 'Radio' || questionType == 'Yes/No' || questionType == 'Yes/No/NA') {
		var selector = "input[type=radio][name='verifiedAnswer_"+questionid+"'][value='"+value+"']";
		var input = $$(selector)[0];
		input.checked = true;
	}
	else if(questionType == 'Check Box') {
		if(value = 'X') {
			$('verifiedBox_'+questionid).checked = true;
		}
	}
	else {		
		$('verifiedBox_'+questionid).value = value;
	}
	saveVerifiedAnswer(questionid, elm); 
}

function saveVerifiedAnswer(questionid, elm) {
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.questionID=' + questionid + '&auditData.answer=' + escape($F(elm));
	var divName = 'status_'+questionid;
	var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			else
				alert("Failed to save comment" + transport.statusText + transport.responseText);
		}
	});
}


function saveComment(questionid, elm) {
	if (catDataID == 0) return;
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.questionID=' + questionid + '&auditData.comment=' + escape($F(elm));
	var divName = 'status_'+questionid;
	var myAjax = new Ajax.Updater('','AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			else
				alert("Failed to save comment" + transport.statusText + transport.responseText);
		}
	});
}


function saveAnswer( questionid, elm ) {
	if (catDataID == 0) return;
	
	var thevalue = '';
	
	if( elm.type == 'checkbox') {
		if(
			( elm.name == ('question_' + questionid + '_C') || elm.name == ('question_' + questionid + '_S') )
			&& ( document.getElementById('question_' + questionid + '_C') != undefined 
			&& document.getElementById('question_' + questionid + '_S') != undefined)  
			) {

				if( document.getElementById('question_' + questionid + '_C').checked )
				{
					thevalue = thevalue + 'C';
				}
								
				if( document.getElementById('question_' + questionid + '_S').checked )
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
	
	var divName = 'status_'+questionid;
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.questionID=' + questionid + '&auditData.answer=' + thevalue;
	
	var myAjax = new Ajax.Updater('', 'AuditDataSaveAjax.action', 
	{
		method: 'post', 
		parameters: pars,
		onException: function(request, exception) {
			alert(exception);
		},
		onSuccess: function(transport) {
			$('required_td'+questionid).innerHTML = '';
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			else
				alert("Failed to save answer" + transport.statusText + transport.responseText);
		}
	});
	return true;
}

function showFileUpload( questionid ) {
	url = 'AuditDataUpload.action?auditID='+auditID+'&question.questionID=' + questionid;
	title = 'Upload'+ questionid;
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function reloadQuestion( questionid ) {
	var pars = 'auditID='+auditID+'&questionID=' + questionid;
	var divName = 'td_answer_'+questionid;
	$(divName).innerHTML="<img src='images/ajax_process.gif' />";
	var myAjax = new Ajax.Updater(divName,'ReloadQuestionAjax.action',
	{
		method: 'post', 
		parameters: pars,
		onSuccess: function(transport) {
			if (transport.status == 200)
				new Effect.Highlight($(divName),{duration: 0.75, startcolor:'#FFFF11', endcolor:'#EEEEEE'});
			else
				alert("Failed to save comment" + transport.statusText + transport.responseText);
		}
	});
}