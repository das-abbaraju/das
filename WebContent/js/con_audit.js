var lastState, ucTimeout, catXHR, ucXHR;
$(function(){
	// AJAX HISTORY
	$('a.hist-category, a.modeset').live('click', function() {
		$.bbq.pushState(this.href);
		$.bbq.removeState('onlyReq');
		$.bbq.removeState('subCat');
		$.bbq.removeState('viewBlanks');
		if ($.bbq.getState().categoryID == lastState.categoryID)
			$.bbq.pushState({"_": (new Date()).getTime()});
		else
			$.bbq.removeState("_");
		if ($.bbq.getState().mode == 'ViewQ' || $.bbq.getState().viewBlanks == "false")
			$.bbq.removeState('mode');
		return false;
	});

	$('ul.subcat-list li a').live('click', function() {
		$.bbq.pushState(this.href);
		$.bbq.removeState('viewBlanks');
		$.bbq.removeState('onlyReq');
		$.bbq.removeState("_");
		return false;
	});

	$('ul.vert-toolbar a.preview').live('click', function() {
		$.bbq.pushState(this.href);
		$.bbq.removeState('viewBlanks');
		$.bbq.removeState('onlyReq');
		$.bbq.removeState('_');
		$.bbq.removeState('subCat');
		return false;
	});

	$(window).bind('hashchange', function() {
		var state = $.bbq.getState();
		if(state.onlyReq !== undefined){
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='PrintReq';
			$('#auditViewArea').block({message: messageLoadingRequirements, centerY: false, css: {top: '20px'} }).load('AuditAjax.action', data, function() {
				$('ul.catUL li.current').removeClass('current');
				$(this).unblock();
			});
			$('#printReqButton').show();
		} else if (state.mode == 'ViewQ') {
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='load';
			loadCategories(data, messageLoadingPreview);
		} else if (state.viewBlanks == "false") {
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='load';
			loadCategories(data, messageLoadingAnsweredQuestions);
		} else if (state.mode == "ViewAll") {
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='load';
			loadCategories(data, messageLoadingAllCategories);
		} else if (state.categoryID === undefined) {
			$.bbq.pushState($.param.fragment(location.href,$('a.hist-category:first').attr('href')));
		} else if (!lastState || !lastState.categoryID || state.categoryID != lastState.categoryID || state.mode != lastState.mode || state["_"]) {
			$('#printReqButton').hide();
			if ($(window).scrollTop() > $('#auditViewArea').offset().top)
				$.scrollTo('#auditViewArea', 800, {axis: 'y'});
			var data = $.deparam.querystring($.param.querystring(location.href, state));
			data.button='load';
			loadCategories(data);
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

	$('#next_cat').live('click', function(e) {
		e.preventDefault();
		$('li.current').next('li.catlist').find('a.hist-category').click();
	});

	$('#done').live('click', function(e){
		e.preventDefault();
		updateCategoriesNow();
		$.scrollTo(0, 800, {axis: 'y'});
	});

	$('#auditViewArea').delegate('div.hasDependentRules', 'updateDependent', function() {
		updateCategoriesNow();
	});

	$('#auditViewArea').delegate('div.affectsAudit', 'updateDependent', function() {
		updateCategoriesNow();
	});

	$('#auditViewArea').delegate('div.question:not(.affectsAudit)', 'updateDependent', function() {
		updateCategories();
	});

	$('#submitRemind').ajaxComplete(function(e, xhr, settings){
		if(settings.headers && settings.headers.refresh &&
				settings.headers.refresh == 'true'){
			$.getJSON('AuditAjax.action', {auditID: auditID, button: 'SubmitRemind'}, function(json){
				if(json && json.remind)
					$('#submitRemind').html($('<div>').attr('class', 'alert').append(json.remind));
				else
					$('#submitRemind').html('');
			});
		}
	});
});

function showNavButtons() {
	if ($('ul.catUL:visible li.catlist:last').is('.current') || $('ul.catUL:visible li.catlist').length == 0)
		$('#cat-nav-buttons').addClass('last');
	else
		$('#cat-nav-buttons').removeClass('last');
}

function loadCategories(data, msg) {
	if (!msg) msg = messageLoadingCategory;
	if (catXHR)
		catXHR.abort();
	$('#auditViewArea').block({message: msg, centerY: false, css: {top: '20px'} });
	catXHR = $.ajax({
		url:'AuditAjax.action',
		data:data,
		success: function(html, status, xhr) {
			if (xhr.status) {
				var state = $.bbq.getState();
				$('li.current').removeClass('current');
				$('#auditViewArea').html(html).unblock();

				var subCatScroll = $('#cathead_'+state.subCat);
				if (subCatScroll.length)
					$.scrollTo(subCatScroll, 800, {axis: 'y'});

				if (state.categoryID !== undefined) {
					var ccat = $('#category_'+state.categoryID).addClass('current');;
					var list = ccat.parents('ul.catUL:first');
					if (list.is(':hidden')) {
						$('ul.catUL:visible').hide();
						list.show();
					}
				}

				if (state.mode == 'ViewQ') {
					$('a.preview').parents('li:first').addClass('current');
				}

				if (state.viewBlanks == "false") {
					$('#viewBlanks').parents('li:first').addClass('current');
				}

				showNavButtons();
				clearLinks();
			}
			$('a.CategoryRulesClueTip').cluetip( {
				sticky: true,
				showTitle: false,
				dropShadow: false,
				mouseOutClose: true,
				clickThrough: false
			});
		}
	});
}

function _updateCategories() {
	$('#auditHeader').addClass('dirty');
	if (ucXHR)
		ucXHR.abort();
	ucXHR = $.ajax({
		url: 'CaoSaveAjax!refresh.action',
		data: {
			auditID: auditID
		},
		headers: {'refresh':'true'},
		type: 'post',
		success: function(text, status, xhr) {
			if (xhr.status) {
				var me = $(text);
				$('#auditHeaderSideNav').html(me.filter('#audit_sidebar_refresh').html());
				$('#caoTable').html(me.filter('#cao_table_refresh').html());
				$('#auditScore').html(me.filter('#auditScore_refresh').html());
				$('#auditHeader').removeClass('dirty');
				var ccat = $('#category_'+$.bbq.getState().categoryID);
				var list = ccat.parents('ul.catUL:first');
				ccat.addClass('current');
				if (list.is(':hidden')) {
					$('ul.catUL:visible').hide();
					list.show();
				}

				showNavButtons();

				if (!$('#aCatlist li.catlist').hasClass('current') || $('#auditViewArea .question').length == 0)
					$('a.hist-category :first').click();
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

function updateModes(mode) {
	$('#modes a').show();
	if (mode.length > 0)
		$('#modes a.'+mode).hide();
}

function printPreview(auditID) {
	window.open('AuditPrintAjax.action?button=load&mode=ViewAll&auditID='+auditID, 'preview', 'menubar=0,scrollbars=1,resizable=1,height=700,width=640')
}