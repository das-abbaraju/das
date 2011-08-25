$(function(){
	$('div.question .fileUpload').live('click', function(e) {
		var q = $(this).parents('form.qform:first').serialize();
		url = 'AuditDataUpload.action?' + q;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	});

	$('#auditViewArea').delegate('div.question', 'change saveQuestion', function() {
		$(this)
			.block({message: 'Saving answer...'})
			.load('AuditDataSaveAjax.action',
				$('form.qform', this).serializeArray(),
				function(response, status) {
					if (status=='success') {
						$(this).trigger('updateDependent');
						$(this).unblock();
					}
				});
		return false;
	});

	$('#auditViewArea').delegate(
			'input.resetAnswer',
			'click',
			function(e) {
				var me = $(this).parents('div.question:first');
				me.block({
					message : 'Clearing answer...'
				}).load('AuditDataSaveAjax.action',
						$('form.qform', me).serializeArray().map(function(t) {
							if (t.name == 'auditData.answer') {
								t.value = '';
							}
							return t;
						}), function(response, status) {
							if (status == 'success') {
								me.trigger('updateDependent');
								me.unblock();
							}
						});
				return false;
			});

	$('#auditViewArea').delegate('input.verify', 'click', function(e) {
		var me = $(this).parents('div.question:first');
		var pars = $('form.qform', me).serializeArray()
		pars.push({name: "toggleVerify", value:"true"});
		me.block({message: $(this).val()+'ing...'})
			.load('AuditDataSaveAjax.action',
				pars,
				function(response, status) {
					if (status=='success') {
						$(this).trigger('updateDependent');
						$(this).unblock();
					}
				});
		return false;
	});

	$('#auditViewArea').delegate('div.hasDependentRequired', 'updateDependent', function() {
		$.each($(this).find('div.dependentRequired:first').text().split(','), function(i,v) {
			reloadQuestion(v);
		});
	});

	$('#auditViewArea').delegate('div.hasDependentVisible', 'updateDependent', function() {
		$.each($(this).find('div.dependentVisible:first').text().split(','), function(i,v) {
			$('#node_'+v).removeClass('hide');
			$('#title_'+v).removeClass('hide');
		});
		$.each($(this).find('div.dependentVisibleHide:first').text().split(','), function(i,v) {
			$('#node_'+v).addClass('hide');
			$('#title_'+v).addClass('hide');
		});
	});

	$('#auditViewArea').delegate('div.hasFunctions', 'updateDependent', function() {
		$.each($(this).find('div.dependentFunction:first').text().split(','), function(i,v) {
			reloadQuestion(v);
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
	var pars = $('#node_'+qid).find('form.qform input.get_request').serialize()+'&button=reload';
	$('#node_'+qid)
		.block({message: 'Reloading question...'})
		.load('AuditDataSaveAjax.action',pars, function() {
		$(this).unblock();
	});
}

function triggerDependent(qid) {
	$('#node_'+qid).trigger('updateDependent');
}

function setAnswer(questionID, answer) {
	$('input[name="auditData.answer"]', '#node_'+questionID).val(answer);
}
