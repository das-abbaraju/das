var lastState, ucTimeout, catXHR, ucXHR;
$(function(){
	
	// AJAX HISTORY

	$('a.hist-category, a.modeset').live('click', function() {
		$.bbq.pushState(this.href);
		$.bbq.removeState('onlyReq');
		$.bbq.removeState('subCat');
		if ($.bbq.getState().categoryID == lastState.categoryID)
			$.bbq.pushState({"_": (new Date()).getTime()});
		else
			$.bbq.removeState("_");
		return false;
	});
	
	$('ul.subcat-list li a').live('click', function() {
		$.bbq.pushState(this.href);
		$.bbq.removeState('onlyReq');
		$.bbq.removeState("_");
		return false;
	});
	
	$(window).bind('hashchange', function() {
		var state = $.bbq.getState();
		if(state.onlyReq !== undefined){
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='PrintReq';
			$('#auditViewArea').block({message: 'Loading Requirements', centerY: false, css: {top: '20px'} }).load('AuditAjax.action', data, function() {
				$('ul.catUL li.current').removeClass('current');
				$(this).unblock();
			});
			$('#printReqButton').show();
		} else if (state.categoryID === undefined) {
			$.bbq.pushState($.param.fragment(location.href,$('a.hist-category:first').attr('href')));
		} else if (!lastState || !lastState.categoryID || state.categoryID != lastState.categoryID || state.mode != lastState.mode || state["_"]) {
			$('#printReqButton').hide();
			if ($(window).scrollTop() > $('#auditViewArea').offset().top)
				$.scrollTo('#auditViewArea', 800, {axis: 'y'});
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='load';
			if (catXHR)
				catXHR.abort();
			$('#auditViewArea').block({message: 'Fetching category...', centerY: false, css: {top: '20px'} });
			catXHR = $.ajax({
				url:'AuditAjax.action', 
				data:data,
				success: function(html, status, xhr) {
					if (xhr.status) {
						$('ul.catUL li.current').removeClass('current');
						$('ul.catUL li.currSub').hide();
						$('#catSubCat_'+state.categoryID).show();
						$('#auditViewArea').html(html).unblock();
						
						var subCatScroll = $('#cathead_'+state.subCat);
						if (subCatScroll.length)
							$.scrollTo(subCatScroll, 800, {axis: 'y'});

						var ccat = $('#category_'+state.categoryID).addClass('current');;
						var list = ccat.parents('ul.catUL:first');
						if (list.is(':hidden')) {
							$('ul.catUL:visible').hide();
							list.show();
						}
					}
				}
			});
		} else if (state.subCat!==undefined) {
			$.scrollTo('#cathead_'+state.subCat, 800, {axis: 'y'});
		}
		lastState = state;
	});
	
	$(window).trigger('hashchange');
	
	// END AJAX HISTORY
	
	$('ul.vert-toolbar li.head .hidden-button').live('click',function() {
		var hidden = $('ul.catUL:hidden')
		$('ul.catUL:visible').fadeOut('slow', function() { hidden.fadeIn('slow'); });
	});
	
	$('ul.vert-toolbar > li').live('mouseenter', function() {
		$(this).addClass('hover');
	}).live('mouseleave', function() {
		$(this).removeClass('hover');
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

	$('div.question .fileUpload').live('click', function(e) {
		var q = $(this).parents('form.qform:first').serialize();
		url = 'AuditDataUpload.action?' + q;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	});

	$('#auditViewArea').delegate('div.question', 'change', function() {
		$(this)
			.block({message: 'Saving answer...'})
			.load('AuditDataSaveAjax.action',
				$('form.qform', this).serialize(),
				function(response, status) {
					if (status=='success') {
						$(this).trigger('updateDependent');
						updateCategories();
					} else {
						alert('Failed to save answer.');
					} 
					$(this).unblock();
				});
		return false;
	});
	

	$('#auditViewArea').delegate('input.verify', 'click', function(e) {
			var me = $(this).parents('div.question:first');
			me.block({message: $(this).val()+'ing...'})
				.load('AuditDataSaveAjax.action',
					$('form.qform', this).serialize(),
					function(response, status) {
						if (status=='success') {
							$(this).trigger('updateDependent');
							updateCategories();
						} else {
							alert('Failed to save answer.');
						} 
						$(this).unblock();
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
		});
		$.each($(this).find('div.dependentVisibleHide:first').text().split(','), function(i,v) {
			$('#node_'+v).addClass('hide');
		});
	});
	
	$('#auditViewArea').delegate('div.hasDependentRules', 'updateDependent', function() {
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

	$('#next_cat').live('click', function(e) {
		e.preventDefault();
		$('li.current').next('li.catlist').find('a.hist-category').click();
	});
});

function _updateCategories() {
	$('#auditHeader').addClass('dirty');
	if (ucXHR)
		ucXHR.abort();
	ucXHR = $.ajax({
		url: 'CaoSaveAjax.action',
		data: {
			auditID: auditID,
			button: 'Refresh'
		},
		type: 'post',
		success: function(text, status, xhr) {
			if (xhr.status) {
				var me = $(text);
				$('#auditHeaderSideNav').html(me.filter('#audit_sidebar_refresh').html());
				$('#caoTable').html(me.filter('#cao_table_refresh').html());
				$('.cluetip').cluetip({
					arrows: true,
					cluetipClass: 'jtip',
					local: true,
					clickThrough: false
				});
				$('#auditHeader').removeClass('dirty');
				var ccat = $('#category_'+$.bbq.getState().categoryID);
				var list = ccat.parents('ul.catUL:first');
				ccat.addClass('current');
				if (list.is(':hidden')) {
					$('ul.catUL:visible').hide();
					list.show();
				}
			}
		}
	});
}

function updateCategories() {
	if (ucTimeout)
		clearTimeout(ucTimeout);
	ucTimeout = setTimeout(_updateCategories, 10000);
}

function updateCategoriesNow() {
	if (ucTimeout)
		clearTimeout(ucTimeout);
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
	$('#node_'+qid)
		.block({message: 'Reloading question...'})
		.load('AuditDataSaveAjax.action',$('#node_'+qid).find('form.qform').serialize()+'&button=reload', function() {
		$(this).unblock();
	});
}

function updateModes(mode) {
	$('#modes a').show();
	if (mode.length > 0)
		$('#modes a.'+mode).hide();
}
