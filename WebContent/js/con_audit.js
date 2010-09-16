$(function(){
	
	$('ul.vert-toolbar li.head .hidden-button').click(function() {
		$('ul.catlist').toggle('slow');
	});
	
	$('.vert-toolbar li').hover(
		function() {
			$(this).addClass('hover');
		},
		function(){
			$(this).removeClass('hover');
		}
	);
	
	// ajax history
	$('a.hist-category').live('click', function() {
		$.bbq.pushState( $.param.fragment(this.href) );
		var li = $(this).parents('li:first');
		li.siblings('li.current').removeClass('current');
		li.addClass('current');
		return false;
	});
	
	$(window).bind('hashchange', function() {
		if ($.bbq.getState().categoryID === undefined)
			$('a.hist-category:first').click();
		else {
			var data = $.deparam.querystring($.param.querystring(location.href, $.bbq.getState()));
			data.button='';
			$('#auditViewArea').load('AuditAjax.action', data);
		}
	});
	
	$(window).trigger('hashchange');
	
});