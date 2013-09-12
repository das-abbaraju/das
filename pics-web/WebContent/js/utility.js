(function($) {
	if (!window.UTILITY) {
		UTILITY = {};
	}
	
	UTILITY.debounce = function(func, threshold) {
		var timeout;
		
		return function() {
			var obj = this;
			var args = arguments;
			
			if (timeout) {
				clearTimeout(timeout);
			}
			
			timeout = setTimeout(function() {
				func.apply(obj, args);
				
				timeout = null;
			}, threshold || 250);
		};
	};
	
	UTILITY.throttle = function(func, delay) {
		var timer;
		
		return function () {
			if (!timer) {
				timer = setTimeout(function () {
					func.apply(this, arguments);
					
					timer = null;
				}, delay);
			}
		};
	};
})(jQuery);