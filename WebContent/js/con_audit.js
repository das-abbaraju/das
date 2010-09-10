$(function(){
	
	$('a.hist-category').live('click', function() {
		$.bbq.pushState( $.param.fragment(this.href) );
		return false;
	});
	
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