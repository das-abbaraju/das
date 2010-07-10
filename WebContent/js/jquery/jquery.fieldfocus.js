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
})(jQuery)

jQuery(function(){
	$('fieldset.form ol :input').fieldfocus();
	
	$('fieldset.form ol li').live('click', function() {
		$(this).find(':input').focus();
	})
});