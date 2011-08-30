$(function(){
	if (!window.AUDIT) {
		AUDIT = {};
	}
	
	// audit question
	AUDIT.question = {
		init: function() {
			$('#auditViewArea').delegate('input.resetAnswer', 'click', this.events.reset);
			$('#auditViewArea').delegate('div.question', 'saveQuestion', this.events.save);
			$('#auditViewArea').delegate('div.question:not(.save-disable)', 'change', this.events.save);
			$('#auditViewArea').delegate('input.verify', 'click', this.events.verify);
			
			// question save trigger for "save-disable" questions
			$('#auditViewArea').delegate('.question-save', 'click', function(event) {
				AUDIT.question.events.save.apply($(this).closest('div.question'));
			});
		},
		
		// question events
		events: {
			reset: function(event) {
				var element = $(this).parents('div.question:first');
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				
				var data = form.serializeArray().map(function(data) {
					if (data.name == 'auditData.answer') {
						data.value = '';
					}
					
					return data;
				});
				
				element.block({
					message : 'Clearing answer...'
				});
				
				AUDIT.question.execute(element, url, data);
			},
			save: function(event) {
				var element = $(this);
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				var data = form.serializeArray();
				
				element.block({
					message: 'Saving answer...'
				});
				
				AUDIT.question.execute(element, url, data);
			},
			verify: function(event) {
				var element = $(this).parents('div.question:first');
				var form = $('form.qform', element);
				var url = 'AuditDataSaveAjax.action';
				var data = form.serializeArray();
				
				data.push({
					name: 'toggleVerify',
					value: 'true'
				});
				
				element.block({
					message: $(this).val() + 'ing...'
				});
				
				AUDIT.question.execute(element, url, data);
			}
		},
		
		// question methods
		execute: function(element, url, data) {
			$.post(url, data, function(data, textStatus, XMLHttpRequest) {
				element.trigger('updateDependent');
				element.replaceWith(data);
			});
		}
	};
	
	AUDIT.question.init();
	
	$('div.question .fileUpload').live('click', function(e) {
		var q = $(this).parents('form.qform:first').serialize();
		url = 'AuditDataUpload.action?' + q;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
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
	var element = $('#node_' + qid);
	var url = 'AuditDataSaveAjax.action';
	var data = element.find('form.qform input.get_request').serializeArray();
	
	data.push({
		button: 'reload'
	});
	
	element.block({
		message: 'Reloading question...'
	});
	
	$.post(url, data, function(data, textStatus, XMLHttpRequest) {
		element.replaceWith(data);
	});
}

function triggerDependent(qid) {
	$('#node_'+qid).trigger('updateDependent');
}

function setAnswer(questionID, answer) {
	$('input[name="auditData.answer"]', '#node_'+questionID).val(answer);
}
