(function($) {
	if (!window.TRADE) {
		TRADE = {};
	}
	
	// trade preview - hover
	TRADE.preview = {
		init: function() {
			var element = $('#contractor_dashboard a.trade').not('.trade-not-viewable');
			
			if (element.length) {
				// initialize on contractor dashboard
				this.popover(element);
			}
		},
		
		popover: function(element) {
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
	};
})(jQuery);