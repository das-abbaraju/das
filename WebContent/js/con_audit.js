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
	
	$('input.fileUpload').live('click', function() {
		alert('show file upload for this: ' + $(this).attr('id'));
	});
	
	$('div.question form.qform').live('submit', function(e){
		e.preventDefault();
	});
	
	$('div.question :input').live('change', function() {
		$(this).parents('div.question:first').block({message: 'Saving answer...'}).load('AuditDataSaveAjax.action', $(this).parents('form.qform:first').serialize(), function(response, status) {
			if (status=='success') {
				$(this).effect('highlight', {color: '#FFFF11'}, 1000);
				updateCategories();
			} else {
				alert('Failed to save answer ');
			}
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
