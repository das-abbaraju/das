/*
	Field Focus plugin for jQuery
	Copyright (c) 2010 Kyle Partridge
	Version: 1.0 (07/09/2010)
*/
(function($){
	$.fn.fieldfocus = function() {
		return $(this).live('focus', function() {
			var parent = $(this).parents('li');
			parent.addClass('fieldhelp-focused');
			var pos = parent.position();
			parent.find('.fieldhelp').css({top: pos.top + 'px', left: (parent.width()+pos.left+40) +'px'});
		}).live('blur', function() {
			$(this).parents('li').removeClass('fieldhelp-focused');
		});
	}
	$.fn.helpstay = function() {
		return $(this).live('mouseover', function() {
			$(this).addClass('hover');
		}).live('mouseout', function(e) {
			$(this).removeClass('hover');
		}).live('mousedown', function() {
			var target = $(this);
			target.addClass('mousedown');
			var handler;
			handler = function() {
				target.removeClass('mousedown');
				$('body').unbind('mouseup', handler);c
			}
			$('body').bind('mouseup', handler);
		});
	}
	$.fn.requiredfields = function() {
		return $(this).live('blur', function() {
			var me = $(this);
			if (me.blank())
				me.parent().removeClass('hasdata');
			else
				me.parent().addClass('hasdata');
		} );
	}
	$.fn.jumpTo = function() {
		var me = $(this);
		var addInnerSpan;
		addInnerSpan = function(e) {
			var headers = $('<ul>').addClass('jump-header-list');
			$(me.selector).each(function(i, v) {
				var h = $(v);
				if (!h.is('.jump-header')) {
					h.addClass('jump-header').html($('<span>').addClass('form-title').html(h.html()));
				}
				var l = $('<li>').text(h.find('.form-title').text());
				l.bind('mouseover', function() {
					$(this).addClass('hover');
				}).bind('mouseout', function() {
					$(this).removeClass('hover');
				}).bind('click', function() {
					$.scrollTo(h, 800, {axis: 'y'});
				});
				headers.append(l);
			});
			$('<span>').addClass('jump-to').text('Jump To').bind('mouseover', function() {
				var me = $(this).addClass('hover');
			}).bind('mouseout', function() {
				$(this).removeClass('hover');
			}).append(headers).appendTo(e);
		}
		return me.live('mouseover', function() {
			if ($(this).find('span.jump-to').size() == 0)
				addInnerSpan(this);
			$(this).addClass('hover');
		}).live('mouseout', function() {
			$(this).removeClass('hover');
		});
	}
})(jQuery)

jQuery(function(){
	$('fieldset.form ol li :input:not(:button)').fieldfocus();
	$('fieldset.form ol li .fieldhelp').helpstay();
	$('.required :input').requiredfields();
	
	$('fieldset.form ol li').live('click', function() {
		$(this).not('.fieldhelp-focused').find(':input:first').focus();
	});
	
	$('fieldset.form h2.formLegend').jumpTo();
	 
});