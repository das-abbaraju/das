$(function(){
	
	$('div.question .fileUpload').live('click', function(e) {
		var q = $(this).parents('form.qform:first').serialize();
		url = 'AuditDataUpload.action?' + q;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	});

	$('#auditViewArea').delegate('div.hasDependentRequired', 'updateDependent', function() {
		$.each($(this).find('div.dependent-questions:first').text().split(','), function(i,v) {
			reloadQuestion(v);
		});
	});

    $('#auditViewArea').delegate('div.hasDependentVisible', 'updateDependent', function() {
        $.each($(this).find('div.dependent-questions:first').text().split(','), function(i,v) {
            reloadQuestion(v);
        });
    });
	
	$('#auditViewArea').delegate('div.hasFunctions', 'updateDependent', function() {
		$.each($(this).find('div.dependent-questions').text().split(','), function(i,v) {
			reloadQuestion(v);
			$('#node_' + v).trigger('updateDependent');
		});
	});
	
	// Insurance Methods
	$('a.uploadNewCertificate').live('click',function(e) {
		e.preventDefault();
		var data = $.deparam($(this).parents('form.qform:first').serialize());
		showCertUpload(data['certID'], data['auditData.question.id']);
	});

	$('#auditViewArea').delegate('div.question a.showExistingCertificates','click',function(e) {
		e.preventDefault();
		var container = $(this).parents('div.question:first').find('div.certificateContainer');
		container.think({message: 'Loading certificates...'}).load("ContractorCertificatesAjax.action", {id: conID});
	});

	$('#auditViewArea').delegate('div.question a.saveCertificate','click',function(e) {
		e.preventDefault();
		$(this).parents('form.qform:first').find('input[name="auditData.answer"]').val($(this).attr('rel'));
		$(this).parents('div.question:first').trigger('saveQuestion');
	});

	$('#auditViewArea').delegate('div.question a.detachCertificate','click',function(e) {
		e.preventDefault();
		$(this).parents('form.qform:first').find('input[name="auditData.answer"]').val('');
		$(this).parents('div.question:first').trigger('saveQuestion');
	});

	$('#auditViewArea').delegate('div.question a.viewCertificate','click',function(e) {
		e.preventDefault();
		var data = $.deparam($(this).parents('form.qform:first').serialize());
		showCertUpload($(this).attr('rel'), data['auditData.question.id']);
	});
});

function showCertUpload(certID, questionID) {
	url = 'CertificateUpload.action?id='+conID
		+ ((certID !== undefined && certID > 0) ? '&certID='+certID : '')
		+ ((questionID !== undefined && questionID > 0) ? '&questionID='+questionID : '')
		+ '&auditID='+auditID;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function reloadQuestion(qid) {
	var element = $('#node_' + qid);
	var url = 'AuditDataSaveAjax.action';
	var data;
	var inputs = element.find('form.qform :input.get_request');
	if (inputs.length) {
		data = inputs.serializeArray();
		data.push({ name:'button', value:'reload' });
	} else {
		data = {
			button: 'reload',
			'auditData.question': qid,
			'auditData.audit': auditID
		};
	}
	
	element.block({
		message: translate('JS.Audit.ReloadingQuestion')
	});

	$.post(url, data, function(data, textStatus, XMLHttpRequest) {
		element.replaceWith(data);
		
		AUDIT.question.initTagit();
	});
}

function triggerDependent(qid) {
	$('#node_'+qid).trigger('updateDependent');
}

function setAnswer(questionID, answer) {
	$('input[name="auditData.answer"]', '#node_'+questionID).val(answer);
}
