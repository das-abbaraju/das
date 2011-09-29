(function($) {
	if (!window.TRADE) {
		TRADE = {};
	}
	
	// trade preview - hover
	TRADE.preview = {
		init: function() {
			// initialize on contractor dashboard
			this.popover($('#contractor_dashboard a.trade'));
		},
		
		popover: function(element) {
			if (element.length) {
				element.popover({
					html: true,
					placement: 'left',
					content: function() {
						var element = $(this);
						var content = element.attr('data-content');
						
						if (!content) {
							AJAX.request({
								url: $(this).attr('rel'),
								async: false,
								success: function(data, textStatus, XMLHttpRequest) {
									element.attr('data-content', data);
									content = data;
								}
							});
						}
						
						return content;
					}
				});
			}
		}
	};
})(jQuery);