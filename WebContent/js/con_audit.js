$(function(){
	
	$('ul.vert-toolbar li.head').live('mouseenter', function(){$(this).addClass('over');}).live('mouseleave', function(){$(this).removeClass('over');});

	$('ul.vert-toolbar li.head .hidden-button').click(function() {
		$('ul.catlist').toggle('slow');
	});
	
	$('a.hist-category').live('click', function() {
		$.bbq.pushState( $.param.fragment(this.href) );
		var li = $(this).parents('li:first');
		li.siblings('li.current').removeClass('current');
		li.addClass('current');
		return false;
	});
	
	$('.vert-toolbar li:not(.head)').live('mouseenter', function(){$(this).addClass('hover');}).live('mouseleave', function(){$(this).removeClass('hover');});
	
	$(window).bind('hashchange', function() {
		if ($.bbq.getState().categoryID === undefined)
			$('a.hist-category:first').click();
		else {
			var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
			data.button='adsf';
			$('.auditViewArea').load('AuditAjax.action', data);
		}
	});
	
	$(window).trigger('hashchange');
});