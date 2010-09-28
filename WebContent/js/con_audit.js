$(function(){
	
	$('ul.vert-toolbar li.head .hidden-button').click(function() {
		var hidden = $('ul.catlist:hidden')
		$('ul.catlist:visible').fadeOut('slow', function() { hidden.fadeIn('slow'); });
	});
	
	$('.vert-toolbar li:not(li.head)').hover(
		function() {
			$(this).addClass('hover');
		},
		function(){
			$(this).removeClass('hover');
		}
	);

	if ($('#nacatlist li:not(li.head)').size() > 0) {
		$('ul.catlist li.head').hover(
			function() {
				$(this).addClass('hover');
			},
			function(){
				$(this).removeClass('hover');
			}
		);
	}
	
	// AJAX HISTORY

	$('a.hist-category').live('click', function() {
		$.bbq.pushState( $.param.fragment(this.href) );
		return false;
	});
	
	$(window).bind('hashchange', function() {
		if ($.bbq.getState().categoryID === undefined)
			$('a.hist-category:first').click();
		else {
			var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
			data.button='';
			$('#auditViewArea').block({message: 'Fetching category...', centerY: false, css: {top: '20px'} }).load('AuditAjax.action', data, function() {
				$('ul.catlist li.current').removeClass('current');
				$('#category_'+$.bbq.getState().categoryID).addClass('current');
				$(this).unblock();
			});
		}
	});
	
	$(window).trigger('hashchange');
	
	// END AJAX HISTORY
	
	$('div.question form.qform').live('submit', function(e){
		e.preventDefault();
	});
	
	$('div.question .fileUpload').live('click', function(e) {
		var q = $(this).parents('form.qform:first').serialize();
		url = 'AuditDataUpload.action?' + q;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	});
	
	$('div.question :input').live('change', function() {
		$(this).parents('div.question:first')
			.block({message: 'Saving answer...'})
			.load('AuditDataSaveAjax.action', 
					$(this).parents('form.qform:first').serialize(), 
					function(response, status) {
						if (status=='success') {
							$(this).effect('highlight', {color: '#FFFF11'}, 1000);
							updateCategories();
						} else {
							alert('Failed to save answer.');
						}
						$(this).unblock();
					});
	});
	
	$('.buttonOsha').live('click', function(){
		var status;
		if($(this).val()=='Delete'){
			if(!confirm('Are you sure you want to delete this location? This action cannot be undone.'))
				return false;
			status = 'Deleting';			
		} else
			status = 'Saving';			
		$(this).parents('#auditViewArea:first').block({message: status+' OHSA Record'}).load('OshaSaveAjax.action', $(this).parents('form#osSave').serialize()+"&button="+$(this).val(), function(response, status){
			$(this).unblock();
		});
	});
});

var ucTimeout;

function _updateCategories() {
	alert('Update the categories via ajax here');
}

function updateCategories() {
	if (ucTimeout)
		clearTimeout(ucTimeout);
	ucTimeout = setTimeout(_updateCategories, 10000);
}

function showCertUpload(conid, certid, caoID, questionID, auditID) {
	url = 'CertificateUpload.action?id='+conid+'&certID='+certid+'&caoID='+caoID
		+ (questionID != undefined ? '&questionID='+questionID : '')
		+ (auditID != undefined ? '&auditID='+auditID : '');
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}

function reloadQuestion(qid) {
	$('#node_'+qid).block({message: 'Saving answer...'}).load('AuditDataSaveAjax.action',$('#node_'+qid).find('form.qform').serialize(), function() {
		$(this).unblock();
	});
}