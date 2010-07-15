/*
	Field Focus plugin for jQuery
	Copyright (c) 2010 Kyle Partridge
	Version: 1.0 (07/09/2010)
*/
(function($){
	$.fn.fieldfocus = function() {
		return $(this).live('focus', function() {
			var parent = $(this).parent();
			parent.addClass('fieldhelp-focused');
			var pos = parent.position();
			parent.find('.fieldhelp').css({top: pos.top + 'px', left: (parent.width()+pos.left+40) +'px'});
		}).live('blur', function() {
			$(this).parent().removeClass('fieldhelp-focused');
		});
	}
	
	$.fn.helpstay = function() {
		return $(this).live('mouseover', function() {
			$(this).addClass('hover');
		}).live('mouseout', function(e) {
			$(this).removeClass('hover');
		}).live('mousedown', function() {
			$(this).addClass('mousedown');
			var target = $(this);
			var handler;
			handler = function() {
				target.removeClass('mousedown');
				$('body').unbind('mouseup', handler);
			}
			$('body').bind('mouseup', handler);
		});
	}
})(jQuery)

jQuery(function(){
	$('fieldset.form ol :input:not(:button)').fieldfocus();
	$('fieldset.form ol .fieldhelp').helpstay();
	
	$('fieldset.form ol li').live('click', function() {
		$(this).find(':input').trigger('focus');
	});
});