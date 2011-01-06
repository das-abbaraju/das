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
		return $(this).live('mouseenter', function() {
			$(this).addClass('hover');
		}).live('mouseleave', function(e) {
			$(this).removeClass('hover');
		}).live('mousedown', function() {
			var target = $(this);
			target.addClass('mousedown');
			var handler;
			handler = function() {
				target.removeClass('mousedown');
				$(document).unbind('mouseup', handler);
			}
			$(document).bind('mouseup', handler);
		});
	}
	$.fn.requiredfields = function() {
		return $(this).live('blur', function() {
			var me = $(this);
			if (me.blank() && !me.hasClass('multifield'))
				me.parent().removeClass('hasdata');
			else
				me.parent().addClass('hasdata');
		} );
	}
	$.fn.jumpTo = function() {
		var me = $(this);
		var addInnerSpan;
		addInnerSpan = function(e) {
			if ($(me.selector).size() < 3)
				return;
			var hlist = [{text: 'Top', e: $(me.selector).parents('form:first'), type: 'jump-top'}];
			var type = 'jump-up';
			$(me.selector).each(function(i, v){
				var h = $(v);
				if (!h.is('.jump-header')) {
					h.addClass('jump-header').html($('<span>').addClass('form-title').html(h.html()));
				}
				var hTitle = h.find('.form-title').text();
				if (hTitle == $(e).find('.form-title').text())
					type = 'jump-current';
				else if (type == 'jump-current')
					type = 'jump-down';
				hlist.push({text: hTitle, e: h, type: type});
			});
			if ($('fieldset.form.submit').size() > 0)
				hlist.push({text: 'Bottom', e: $('fieldset.form.submit'), type: 'jump-bottom'});
			var headers = $('<ul>').addClass('jump-header-list');
			$.each(hlist, function(i, v) {
				var a = $('<a>').text(v.text).addClass(v.type)
							.click(function(e) {
								e.preventDefault();
								$.scrollTo(v.e, 800, {axis: 'y'});
							});
				var l = $('<li>').append(a).hover(function() {
					$(this).addClass('hover');
				}, function() {
					$(this).removeClass('hover');
				});
				headers.append(l);
			});
			$('<span>').addClass('jump-to').text('Jump To').bind('mouseenter', function() {
				var me = $(this).addClass('hover');
			}).bind('mouseleave', function() {
				$(this).removeClass('hover');
			}).append(headers).appendTo(e);
		}
		return me.live('mouseenter', function() {
			if ($(this).find('span.jump-to').size() == 0)
				addInnerSpan(this);
			$(this).addClass('hover');
		}).live('mouseleave', function() {
			$(this).removeClass('hover');
		});
	}
})(jQuery)

jQuery(function(){
	$('fieldset.form ol li :input:not(:button)').fieldfocus();
	$('fieldset.form ol li .fieldhelp').helpstay();
	$('.required :input:not(:checkbox)').requiredfields();
	
	$('fieldset.form ol li').live('click', function() {
		$(this).not('.fieldhelp-focused').find(':input:visible:first:not(:disabled)').focus();
	});
	
	$('fieldset.form h2.formLegend:not(.noJump)').jumpTo();
	 
});