(function($) {
	
	$(document).ready(function() {
		// initialize audit esignature editing
		AUDIT.esignature.init();
		
		AUDIT.load_category.init();
		
		if ($('#auditViewArea').length) {
			$(window).trigger('hashchange'); // trigger audit category refresh on audit pages
		}
		
		// initialize audit question save / reset / verify
		AUDIT.question.init();
		
		// initialize trade preview
		TRADE.preview.init();
	});
	
})(jQuery);