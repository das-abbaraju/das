function changeAnswer(questionid, questionType) {
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
	saveVerifiedAnswer(questionid, elm); 
}

function saveVerifiedAnswer(questionid, elm) {
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid + '&auditData.answer=' + escape($F(elm)) + '&toggleVerify=true';
	var divName = 'status_'+questionid;
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


function saveComment(questionid, elm) {
	if (catDataID == 0) return;

	var comment = $F($('comments_' + questionid));
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid + '&auditData.comment=' + comment;
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
	var pars = 'auditData.audit.id='+auditID+'&catDataID='+catDataID+'&auditData.question.id=' + questionid + '&auditData.answer=' + thevalue;
	
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
	url = 'AuditDataUpload.action?auditID='+auditID+'&question.id=' + questionid;
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