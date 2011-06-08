(function($) {
	$.fn.blank = function() {
		return $.trim($(this).val()).length == 0;
	}
	$.fn.capitalize = function() {
		var val = $(this).val();
		return val.substr(0, 1).toUpperCase() + val.substr(1, val.length);
	}
	$.fn.equalWidth = function() {
		var max_width = 0;
		$(this).each(function() {
			var width = $(this).width();
			if (width > max_width)
				max_width = width;
		});
		$(this).width(max_width + 10);

		return $(this);
	}
	$.fn.think = function(options) {
		var settings = {
			message : 'Communicating with PICS...',
			img : 'images/ajax_process.gif'
		};
		$.extend(settings, options);
		return $(this).empty().append($('<img/>', {
			src : settings.img
		})).append(' ' + settings.message);
	}
	$.fn.unthink = function() {
		if (!$(this).is('.thinking'))
			return $(this);
		return $(this).empty();
	}
	$.fn.msg = function(type, message, empty) {
		if (!message)
			message = 'Please add an error message to make this work.';
		var me = $(this);
		if (empty)
			me.empty();
		return me.prepend($("<div>", {
			"class" : type
		}).html(message));
	}

	$.mainStatus = function(message) {
		var options = {target: '#mainStatus', delay: 2000}
		$(options.target).text(message).show('slow').delay(2000).hide('slow');
	}
})(jQuery)