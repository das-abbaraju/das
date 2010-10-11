$(function(){
	
	// AJAX HISTORY

	$('a.hist-category, a.modeset').live('click', function() {
		$.bbq.pushState($.param.fragment(location.href,this.href));
		$.bbq.removeState('onlyReq');
		return false;
	});
	
	$(window).bind('hashchange', function() {
		if($.bbq.getState().onlyReq != undefined){
			var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
			data.button='PrintReq';
			$('#auditViewArea').block({message: 'Loading Requirements', centerY: false, css: {top: '20px'} }).load('AuditAjax.action', data, function() {
				$('ul.catUL li.current').removeClass('current');
				$(this).unblock();
			});
		} else {
			if ($.bbq.getState().categoryID === undefined)
				$.bbq.pushState($.param.fragment(location.href,$('a.hist-category:first').attr('href')));
			else {
				var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
				data.button='';
				$('#auditViewArea').block({message: 'Fetching category...', centerY: false, css: {top: '20px'} }).load('AuditAjax.action', data, function() {
					$('ul.catUL li.current').removeClass('current');
					$('#category_'+$.bbq.getState().categoryID).addClass('current');
					$('ul.catUL li.currSub').hide();
					$('#catSubCat_'+$.bbq.getState().categoryID).show();
					$(this).unblock();
					if ($(window).scrollTop() > $('#auditViewArea').offset().top)
						$.scrollTo('#auditViewArea', 800)
				});
			}
		}
	});
	
	$(window).trigger('hashchange');
	
	// END AJAX HISTORY
	
	$('ul.vert-toolbar li.head .hidden-button').live('click',function() {
		var hidden = $('ul.catUL:hidden')
		$('ul.catUL:visible').fadeOut('slow', function() { hidden.fadeIn('slow'); });
	});
	
	$('.vert-toolbar li:not(li.head, li.currSub, li.subCatli)').live('mouseenter', function() {
		$(this).addClass('hover');
	}).live('mouseleave', function() {
		$(this).removeClass('hover');
	});
	
	$('li.subCatli').live('mouseenter', function() {
		$(this).addClass('subhover');
	}).live('mouseleave', function() {
		$(this).removeClass('subhover');
	}).live('click', function(){
		$.scrollTo($('#subcat_'+$(this).attr('id')), 800, {axis: 'y'});
	});

	if ($('#nacatlist li:not(li.head)').size() > 0) {
		$('ul.catUL li.head').live('mouseenter', function() {
			$(this).addClass('hover');
		}).live('mouseleave', function() {
			$(this).removeClass('hover');
		});
	}
	
	$('#refresh_cao').live('click', function(e) {
		updateCategoriesNow();
		e.preventDefault();
	});

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
						$(this).trigger('updateDependent');
						updateCategories();
					} else {
						alert('Failed to save answer.');
					}
					$(this).unblock();
				});
	});
	
	$('div.hasDependentRequired').live('updateDependent', function() {
		$.each($(this).find('div.dependentRequired:first').text().split(','), function(i,v) {
			reloadQuestion(v);
		});
	});

	$('div.hasDependentVisible').live('updateDependent', function() {
		$.each($(this).find('div.dependentVisible:first').text().split(','), function(i,v) {
			$('#node_'+v).removeClass('hide');
		});
		$.each($(this).find('div.dependentVisibleHide:first').text().split(','), function(i,v) {
			$('#node_'+v).addClass('hide');
		});
	});
	
	$('div.hasDependentRules').live('updateDependent', function() {
		updateCategoriesNow();
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
	var blocked = $('#auditHeader,#auditHeaderSideNav').block({message: 'Updating...'});
	$.ajax({
		url: 'CaoSaveAjax.action',
		data: {
			auditID: auditID,
			button: 'Refresh'
		},
		type: 'post',
		success: function(text, status, xhr) {
			var me = $(text);
			blocked.unblock();
			$('#auditHeaderSideNav').html(me.filter('#audit_sidebar_refresh').html());
			$('#caoTable').html(me.filter('#cao_table_refresh').html());
			$('.cluetip').cluetip({
				arrows: true,
				cluetipClass: 'jtip',
				local: true,
				clickThrough: false
			});
			$('#auditHeader').removeClass('dirty');
		}
	});
}

function updateCategories() {
	if (ucTimeout)
		clearTimeout(ucTimeout);
	$('#auditHeader').addClass('dirty');
	ucTimeout = setTimeout(_updateCategories, 10000);
}

function updateCategoriesNow() {
	if (ucTimeout)
		clearTimeout(ucTimeout);
	$('#auditHeader').addClass('dirty');
	_updateCategories();
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
	$('#node_'+qid).block({message: 'Saving answer...'}).load('AuditDataSaveAjax.action',$('#node_'+qid).find('form.qform').serialize()+'&button=reload', function() {
		$(this).unblock();
	});
}

function updateModes(mode) {
	$('#modes a').show();
	if (mode.length > 0)
		$('#modes a.'+mode).hide();
}