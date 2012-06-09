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
		
		// registration
		REGISTRATION.autofill_username.init();
		
		//REGISTRATION.client_site_filter.init();
		
		//REGISTRATION.client_site_manage.init();
		
		REGISTRATION.contractor_country.init();
		
		REGISTRATION.field_validate.init();
		
		REGISTRATION.help_text.init();
		
		REGISTRATION.language_dropdown.init();
		
		REGISTRATION.membership_help.init();
		
		REGISTRATION.payment_check.init();
		
		REGISTRATION.payment_submision.init();
		
		REGISTRATION.services_performed_toggle.init();
		
		// initialize trade preview
		TRADE.preview.init();
	});
	
})(jQuery);