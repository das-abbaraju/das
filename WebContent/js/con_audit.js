var ucTimeout, ucXHR;

$(function () {
	$('ul.vert-toolbar > li').live('mouseenter', function () {
		$(this).addClass('hover');
	}).live('mouseleave', function () {
		$(this).removeClass('hover');
	});

	$('#refresh_cao').live('click', function(e) {
		e.preventDefault();

		updateCategoriesNow();
	});

	$('#next_cat').live('click', function(e) {
		e.preventDefault();

		$('li.current').next('li.catlist').find('a.hist-category').click();
	});

	$('#done').live('click', function(e){
		e.preventDefault();

		updateCategoriesNow();

		$.scrollTo(0, 800, {
		    axis: 'y'
        });
	});

	$('#auditViewArea').delegate('div.hasDependentRules', 'updateDependent', function () {
		updateCategoriesNow();
	});

	$('#auditViewArea').delegate('div.affectsAudit', 'updateDependent', function () {
		updateCategoriesNow();
	});

	$('#auditViewArea').delegate('div.question:not(.affectsAudit)', 'updateDependent', function () {
		updateCategories();
	});

	$('#submitRemind').ajaxComplete(function(e, xhr, settings){
		if(settings.headers && settings.headers.refresh && settings.headers.refresh == 'true') {
			$.getJSON('AuditAjax.action', {
			    auditID: auditID,
			    button: 'SubmitRemind'
	        }, function (json) {
				if (json && json.remind) {
					$('#submitRemind').html($('<div>').attr('class', 'alert').append(json.remind));
				} else {
					$('#submitRemind').html('');
				}
			});
		}
	});

	$('#auditProblems, #problemsHide').live('click', function () {
		$('#problems').slideDown();
		$('#problemsHide').hide();
	});

	$('#problems .bottom').live('click', function () {
		$('#problems').slideUp();
		$('#problemsHide').show();
	});

	$('#importPQFCluetipLink').cluetip({
		arrows: true,
		cluetipClass: 'jtip',
		local: true,
		clickThrough: false,
		activation: 'click',
		sticky: true,
		showTitle: false,
		closeText: "<img src='images/cross.png' width='16' height='16'>",
		width: 675
	});
});

function showNavButtons() {
	if ($('ul.catUL:visible li.catlist:last').is('.current') || $('ul.catUL:visible li.catlist').length == 0) {
		$('#cat-nav-buttons').addClass('last');
	} else {
		$('#cat-nav-buttons').removeClass('last');
	}
}

function highlight_category(category) {
	var ccat = $('#category_'+category).addClass('current');;
	var list = ccat.closest('ul.catUL');

	if (list.is(':hidden')) {
		$('ul.catUL:visible').hide();

		list.show();
	}
}

function _updateCategories() {
	$('#auditHeader').addClass('dirty');

	var currentCategory = $('li.catlist.current'), categoryID = 0;

	if (currentCategory.length > 0) {
		categoryID = currentCategory.attr('id').split('_')[1];
	}

	ucXHR && ucXHR.abort();

	ucXHR = $.ajax({
		url: 'CaoSaveAjax!refresh.action',
		data: {
			auditID: auditID
		},
		headers: {
		    'refresh':'true'
        },
		type: 'post',
		success: function(text, status, xhr) {
			if (xhr.status) {
				var me = $(text);

				$('#auditHeaderSideNav').html(me.filter('#audit_sidebar_refresh').html());
				$('#caoTable').html(me.filter('#cao_table_refresh').html());
				$('#auditScore').html(me.filter('#auditScore_refresh').html());
				$('#auditHeader').removeClass('dirty');

				highlight_category(categoryID);
				showNavButtons();
			}
		}
	});
}

function updateCategories() {
	if (ucTimeout) {
		clearTimeout(ucTimeout);
	}

	ucTimeout = setTimeout(_updateCategories, 10000);
}

function updateCategoriesNow() {
	if (ucTimeout) {
		clearTimeout(ucTimeout);
	}

	_updateCategories();
}

function updateModes(mode) {
	$('#modes a').show();

	if (mode.length > 0) {
		$('#modes a.'+mode).hide();
	}
}

function printPreview(auditID, auditTypeId) {
	if (auditTypeId == '420') {
		window.open('PrintIolRmpAudit.action?audit='+auditID, 'preview', 'menubar=1,scrollbars=1,resizable=1,height=768,width=1024')
	} else {
		window.open('AuditPrintAjax.action?button=load&mode=ViewAll&auditID='+auditID, 'preview', 'menubar=0,scrollbars=1,resizable=1,height=700,width=640')
	}

}