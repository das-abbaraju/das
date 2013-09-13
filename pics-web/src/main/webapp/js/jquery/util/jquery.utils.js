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
			message : translate('JS.global.CommunicatingWithPICS'),
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
	$.notify = function(options) {
		var settings = {
			"target" : "#notify",
			"delay" : 2000,
			"type" : "timed",
			"message" : "placeholder"
		}

		if ($.isPlainObject(options)) {
			$.extend(settings, options);
		} else {
			settings.message = options;
		}

		var message = $("<div>", {
			"class" : "notification"
		}).hide().bind("remove.notify", function() {
			message.slideUp("slow", function() {
				message.unbind('.notify').remove();
			});
		});

		if (settings.type == "process") {
			message.append($("<span>", {
				"class" : "processing"
			}));
		}
		message.append(settings.message);

		if (settings.type == "sticky") {
			message.append($("<span>", {
				"class" : "sticky"
			})).bind("click", function() {
				message.trigger("remove.notify");
			});
		}

		var target = $(settings.target).prepend(message);
		message.slideDown("slow")

		if (settings.type == "timed") {
			setTimeout(function() {
				message.trigger("remove.notify");
			}, settings.delay);
		}

		return message;
	}
})(jQuery)