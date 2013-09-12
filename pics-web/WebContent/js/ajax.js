(function($) {
	if (!window.AJAX) {
		AJAX = {};
	}

	AJAX = {
		request: function(options) {
			var defaults = {
				url: window.location.href,
				type: 'POST',
				dataType: 'html',
				data: {},
				success: function(data, textStatus, XMLHttpRequest) {},
				error: function(XMLHttpRequest, textStatus, errorThrown) {},
				complete: function(XMLHttpRequest, textStatus) {}
			};
			
			var config = {};
			
			$.extend(config, defaults, options);
			
			return $.ajax(config);
		}
	};
})(jQuery);